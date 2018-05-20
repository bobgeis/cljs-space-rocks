(ns helper.fun
  "helper functions")


;; circle constants

(def pi
  "pi"
  Math/PI)

(def two-pi
  "two pi"
  (* pi 2))

(def half-pi
  "one half pi"
  (/ pi 2))

(def tau
  "tau is two pi"
  two-pi)

(def half-tau
  "one half tau (pi)"
  pi)

(def quarter-tau
  "one quarter of tau (half-pi"
  half-pi)

;; geometry functions

;; angle conversions
(defn radians
  "convert circles to radians"
  [a]
  (* tau a))

(defn unrad
  "convert radians to circles"
  [a]
  (/ a tau))

(defn degrees
  "convert circles to degrees"
  [a]
  (* 360 a))

(defn undegree
  "convert degrees to circles"
  [a]
  (/ a 360))

(defn deg-to-rad
  "convert degrees to radians"
  [a]
  (radians (undegree a)))

(defn rad-to-deg
  "convert radians to degrees"
  [a]
  (degrees (unrad a)))

;; magnitude (mag) and get angle (gang) of [x y] vectors
(defn mag
  "get the magnitude of a vector [x y]"
  [x y]
  (Math/hypot x y))

(defn gang
  "get the angle of a vector [x y]"
  [x y]
  (Math/atan2 y x))

;; cartesian to polar conversion
(defn ra-to-xy
  "convert polar coordinates to cartesian"
  [r a]
  [(* r (Math/cos a)) (* r (Math/sin a))])

(defn xy-to-ra
  "convert cartesian coordinates to polar"
  [x y]
  [(mag x y) (gang x y)])

;; translation
(defn trans-xy
  "translate an xy point by dx dy yielding [x' y']"
  [x y dx dy]
  [(+ x dx) (+ y dy)])

(defn trans-ra
  "translate an xy point by a distance (r) in a direction (a radians)
    yielding [x' y']"
  [x y r a]
  (let [[dx dy] (ra-to-xy r a)]
    (trans-xy x y dx dy)))


;; misc utility

(defn square
  "square it"
  [x]
  (* x x))

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
