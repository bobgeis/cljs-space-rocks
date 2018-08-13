(ns helper.fun
  "basic helper functions"
  (:require
   [clojure.string :as string]))

;; misc utility

(def floor "rounds down a float" Math/floor)

(defn sjoin
  "string join on space"
  [strings]
  (string/join " " strings))

(defn square
  "square it"
  [x]
  (* x x))

(defn wrap
  "wrap a number once if it is outside 0->max"
  [s max]
  (cond
    (> 0 s) (+ s max)
    (< max s) (- s max)
    :else s))

(defn clamp
  "clamp a number if it is outside 0->max"
  [s maximum]
  (max 0 (min s maximum)))

(defn inside?
  "true if any of s+/-r is within 0->max"
  ([s r max]
   (or (> s (- r)) (< s (+ max r))))
  ([s max]
   (inside? s 0 max)))

(defn outside?
  "true if any of s+/-r is outside 0->max.
  will be -1 if any is below 0, else 1 if any is above max."
  ([s r max]
   (cond
     (< s r) -1
     (> s (- max r)) 1
     :else nil))
  ([s max]
   (outside? s 0 max)))


(defn mmap
  "map over just the values of a map, producing a new map
  this is a copy of fmap just for maps and map-like structs
  Note how empty m ensures that the product is the same type"
  [f m]
  (into (empty m) (for [[k v] m] [k (f v)])))

(defn distance
  "get the distance between two point maps
  two arg version takes two maps that each have :x :y
  four arg version takes x1 y1 x2 y2"
  ([{x1 :x y1 :y} {x2 :x y2 :y}]
   (distance x1 y1 x2 y2))
  ([x1 y1 x2 y2]
   (Math/hypot (- x2 x1) (- y2 y1))))

(defn within?
  "are two points within r of each other"
  [p1 p2 r]
  (> r (distance p1 p2)))

(defn filtermap
  "map then filter nils/false
  many args to filtermap over one or more colls"
  [f & colls]
  (filter identity (apply map f colls)))

(defn map->vec
  "take a map and keys and turn it into a vec with the values in the order given
  if the coll is not a map, then it is passed through unchanged"
  [coll keys]
  (if (map? coll)
    (reduce #(conj %1 (get coll %2)) [] keys)
    coll))

(defn vec->map
  "takes a vector or sequence and keys, and turns it into a map
  with the first value & first key assoced, etc"
  [coll keys]
  (if (map? coll) coll
      (zipmap keys coll)))

(defn assoc-fn
  "take a map/vec, fun, and values, and assoc using (fun val) to get the keys"
  ([coll fun val]
   (assoc coll (fun val) val))
  ([coll fun val & vals]
   (let [ret (assoc-fn coll fun val)]
     (if vals
       (recur ret fun (first vals) (next vals))
       ret))))

(defn assoc-fn-seq
  "List assoc-fn but takes a seq of vals.
  If vals contains nils, will stop at first nil"
  [coll fun vals]
  (if (nil? (first vals)) coll
      (recur (assoc-fn coll fun (first vals)) fun (rest vals))))

(defn dissoc-fn
  "take a map/vec, fun, and values, and dissoc using (fun val) to get keys"
  ([coll fun val]
   (dissoc coll (fun val) val))
  ([coll fun val & vals]
   (let [ret (dissoc-fn coll fun val)]
     (if vals
       (recur ret fun (first vals) (next vals))
       ret))))

(defn dissoc-fn-seq
  "Like dissoc-fn but takes a seq of vals.
  If vals contains nils, will stop at first nil"
  [coll fun vals]
  (if (nil? (first vals)) coll
      (recur (dissoc-fn coll fun (first vals)) fun (rest vals))))
