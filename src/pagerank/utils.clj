(ns pagerank.utils)
(defn my-pmap
  ([threads f coll]
   (let [n threads
         rets (map #(future (f %)) coll)
         step (fn step [[x & xs :as vs] fs]
                (lazy-seq
                 (if-let [s (seq fs)]
                   (cons (deref x) (step xs (rest s)))
                   (map deref vs))))]
     (step rets (drop n rets)))))

(defn partition-into [n coll]
  (let [size (int (Math/ceil (/ (count coll) n)))]
    (partition-all size coll)))

;; default pmap is horribly inefficient. create x thread pools and use pmap to
;; asychronously kick those threads off (way way way faster than default implementation of pmap)
(defn map-pool
  [f threads coll]
  (let [t (map #(future (f %)) (partition-into threads coll))]
    (my-pmap threads deref t)))
