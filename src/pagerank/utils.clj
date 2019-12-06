(ns pagerank.utils)
; (defn my-pmap
;   ([f threads coll]
;    (let [n (+ 2 (.. Runtime getRuntime availableProcessors))
;          rets (map #(future (f %)) coll)
;          step (fn step [[x & xs :as vs] fs]
;                 (lazy-seq
;                  (if-let [s (seq fs)]
;                    (cons (deref x) (step xs (rest s)))
;                    (map deref vs))))]
;      (step rets (drop n rets))))
;   ([f threads coll & colls]
;    (let [step (fn step [cs]
;                 (lazy-seq
;                  (let [ss (map seq cs)]
;                    (when (every? identity ss)
;                      (cons (map first ss) (step (map rest ss)))))))]
;      (pmap #(apply f %) threads (step (cons coll colls))))))



(defn partition-into [n coll]
  (let [size (int (Math/ceil (/ (count coll) n)))]
    (partition-all size coll)))

;; default pmap is horribly inefficient. create x thread pools and use pmap to
;; asychronously kick those threads off (way way way faster than default implementation of pmap)
(defn my-pmap
  [f threads coll]
  (let [t (map #(future (f %)) (partition-into threads coll))]
    (pmap deref t)))
