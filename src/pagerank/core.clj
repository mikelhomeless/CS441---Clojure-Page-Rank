(ns pagerank.core
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str]))

(def outlinks
  (with-open [rdr (io/reader "test.txt")]
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
;; we go ahead and just sum up the inlink counts as the page rank for each page
(def pageRanks
  (into {} (map
              (fn [[page links]] (vector page (count links)))
              inlinks)))
