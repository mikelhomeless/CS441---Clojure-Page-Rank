(ns rescue.core
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str]))

(def outdata
  (with-open [rdr (io/reader "test.txt")]
  (into {} (map #(vector (first %) (rest %))
           (map #(str/split % #" ") (line-seq rdr))))))

(def indata
  (apply merge-with into
    (flatten
      (reduce-kv
        (fn [ncoll k v]
            (conj ncoll (into [] (map #(hash-map % (vector k)) v))))
        []
        outdata))))

(def pageRanks)
