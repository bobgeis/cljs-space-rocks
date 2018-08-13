(ns helper.geom
  "ns for geometry helper functions"
  (:require
   [helper.fun :as fun :refer [map->vec vec->map]]))

;; circle constants

(def pi
  "pi"
  Math/PI)

(def two-pi
  "two pi"
  (* pi 2))

(def half-pi
  "one half pi - 90 degress in radians"
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

(def max-degrees
  "degrees in a circle"
  360)

;; some common trig results

(def sin30
  (Math/sin (/ half-pi 3)))
(def cos30
  (Math/cos (/ half-pi 3)))

(def sin45
  (Math/sin (/ half-pi 2)))
(def cos45
  (Math/cos (/ half-pi 2)))

(def sin60
  (Math/sin (* 2 (/ half-pi 3))))
(def cos60
  (Math/cos (* 2 (/ half-pi 3))))

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
  (* max-degrees a))

(defn undegree
  "convert degrees to circles"
  [a]
  (/ a max-degrees))

(defn deg->rad
  "convert degrees to radians"
  [a]
  (radians (undegree a)))

(defn rad->deg
  "convert radians to degrees"
  [a]
  (degrees (unrad a)))


    ;; vector arithemtic

(defn addv
  "add two vectors/points together"
  ([x1 y1 x2 y2]
   [(+ x1 x2) (+ y1 y2)])
  ([[x1 y1] x2 y2]
   (addv x1 y1 x2 y2))
  ([[x1 y1] [x2 y2]]
   (addv x1 y1 x2 y2)))

(defn subv
  "subtract the second vector/point from the first"
  ([x1 y1 x2 y2]
   [(- x1 x2) (- y1 y2)])
  ([[x1 y1] x2 y2]
   (subv x1 y1 x2 y2))
  ([[x1 y1 [x2 y2]]]
   (subv x1 y1 x2 y2)))

;; magnitude (mag) and angle (ang) of [x y] vectors
(defn mag
  "get the magnitude of a vector"
  ([x y]
   (Math/hypot x y))
  ([point]
   (apply mag (map->vec point [:x :y]))))

(defn ang
  "get the angle of a vector"
  ([x y]
   (Math/atan2 y x))
  ([point]
   (apply ang (map->vec point [:x :y]))))

(defn dist
  "distance between two xy points"
  ([x1 y1 x2 y2]
   (mag (- x2 x1) (- y2 y1)))
  ([x1 y1 p2]
   (apply dist x1 y1 (map->vec p2 [:x :y]))))

;; make points
(defn xy-map
  "turn [x y] into {:x x :y y}"
  [v]
  (vec->map v [:x :y]))

(defn ra-map
  "turn [r a] into {:r r :a a}"
  [v]
  (vec->map v [:r :a]))

;; cartesian to polar conversion
(defn ra->xy
  "convert polar coordinates to cartesian.
  a is in rad
  returns [x y]"
  ([r a]
   [(* r (Math/cos a)) (* r (Math/sin a))])
  ([point]
   (apply ra->xy (map->vec point [:r :a]))))

(defn xy->ra
  "convert cartesian coordinates to polar
  returns [r a]"
  ([x y]
   [(mag x y) (ang x y)])
  ([point]
   (apply xy->ra (map->vec point [:x :y]))))

;; intersection and collison

(defn cc-hit?
  "are these two circles colliding?
  expects two maps that each have :x :y and :r keys"
  [{x1 :x y1 :y r1 :r} {x2 :x y2 :y r2 :r}]
  (> (+ r1 r2) (dist x1 y1 x2 y2)))

;; translation
(defn trans-xy
  "translate an xy point by dx dy yielding [x' y']"
  [x y dx dy]
  [(+ x dx) (+ y dy)])

(defn trans-ra
  "translate an xy point by a distance (r) in a direction (a radians)
    yielding [x' y']"
  [x y r a]
  (let [[dx dy] (ra->xy r a)]
    (trans-xy x y dx dy)))

