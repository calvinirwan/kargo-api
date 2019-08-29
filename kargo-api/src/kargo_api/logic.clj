(ns kargo-api.logic
  #_(:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test]
            [clojure.pprint :as pprint]
            [pedes.logic :as logic]))

(defn bubble-sort
  [coll]
  (let [size (count coll)]
    (loop [i 0
           j (inc i)
           coll coll
           switched? false]
      (cond
        (or (zero? size) (= 1 size))
        coll
        
        (>= j size)
        (if switched?
          (recur 0 1 coll false)
          coll)

        (> (nth coll i) (nth coll j))
        (recur (inc i)
               (inc j)
               (assoc coll
                      i (nth coll j)
                      j (nth coll i))
               true)

        :else
        (recur (inc i) (inc j) coll switched?)))))

(defn bubble-sort-key
  [coll key]
  (let [size (count coll)]
    (loop [i 0
           j (inc i)
           coll coll
           switched? false]
      (cond
        (or (zero? size) (= 1 size))
        coll
        
        (>= j size)
        (if switched?
          (recur 0 1 coll false)
          coll)

        (> (get (nth coll i) key)
           (get (nth coll j) key))
        (recur (inc i)
               (inc j)
               (assoc coll
                      i (nth coll j)
                      j (nth coll i))
               true)

        :else
        (recur (inc i) (inc j) coll switched?)))))
