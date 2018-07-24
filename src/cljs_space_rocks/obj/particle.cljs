(ns cljs-space-rocks.obj.particle
  "ns for particle effects"
  (:require
   [com.rpl.specter :as sp]
   [helper.log :refer [clog]]
   [helper.fun :as fun :refer [floor]]
   [helper.color :refer [hsl]]
   [helper.geom :as geom :refer [ra->xy]]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.drand :as drand]))


;; constants

(def max-life 50)

(def min-speed 20)

(def max-speed
  100)

(def svg-length
  "length of the svg in units of v"
  0.3)

(def drag 0.08)

;; helpers

(defn get-color
  [ratio]
  (hsl
   (* 60 ratio ratio)
   (+ 70 (* 30 ratio))
   (+ 30 (* 70 ratio))))

;; model / creation

(defn create
  "create a particle from args"
  [x y vx vy]
  {:x x :y y :vx vx :vy vy :drag drag
   :life max-life :id (id/get-id)})

(defn obj->ex-particle
  "make an explosion particle from an object"
  [{:keys [x y vx vy r] :as obj}]
  (let [[ax ay] (ra->xy (drand/rrand min-speed max-speed) (drand/rangle))]
    (create x y (+ vx ax) (+ vy ay))))

(defn make-particles
  "get sequence of n particles on obj"
  [n obj]
  (repeatedly n #(obj->ex-particle obj)))

;; query

(defn kill?
  "a particle should be removed if its life is 0"
  [obj]
  (= 0 (:life obj)))

;; manipulation

(defn tick
  "tick one particle"
  [obj]
  (if (kill? obj) sp/NONE
      (-> obj
          (misc/physics)
          (assoc :life (dec (:life obj))))))

;; view

(defn svg
  "make an svg for one particle"
  [{:keys [x y vx vy life color] :as obj}]
  (let [ratio life]
    [:line
     {:x1 (floor (- x vx)) :y1 (floor (- y vy))
      :x2 (floor (+ x (* svg-length vx))) :y2 (floor (+ y (* svg-length vy)))
      :stroke (get-color (/ life max-life))
      :stroke-width 20}]))
