(ns pagerank.core
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [pagerank.utils :as utils]))

(def outlinks
  (with-open [rdr (io/reader "pages.txt")]
    (into {} (map #(vector (first %) (rest %))
             (map #(str/split % #" ") (line-seq rdr))))))

(def inlinks
  (apply merge-with into
    (flatten
      (reduce-kv
        (fn [ncoll k v]
          (conj ncoll (into [] (map #(hash-map % (vector k)) v))))
        []
        outlinks))))

;; since each page has a weight of 1 at the beginning,
(def pageranks
  (into {} (map #(vector (str %) 1) (range (count outlinks)))))

;; calculate the rank of a page divided by its outlink count
(defn rank-over-count [page]
  (/ (get pageranks page) (count (get outlinks page))))

;; update a page's rank by iterating over each of it's inlinks
(defn update-pagerank [[page _]]
  (let [x (reduce + (map rank-over-count (get inlinks page)))]
    (def pageranks (assoc pageranks page (+ 0.15 (* x 0.85))))))

(doseq [x '(1 2 4 8 16 32 64)]
  (println (str "Running on " x " threads..."))
(time (dotimes [_ 1000] (doall (utils/map-pool #(doall (map update-pagerank %)) x pageranks)))))

(println pageranks)
