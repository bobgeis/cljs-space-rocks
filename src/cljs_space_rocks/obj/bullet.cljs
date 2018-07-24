(ns cljs-space-rocks.obj.bullet
  "ns for bullet constants and functions"
  (:require
   [com.rpl.specter :as sp]
   [helper.geom :refer [ra->xy deg->rad]]
   [helper.log :refer [clog]]
   [helper.fun :refer [floor]]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.misc :as misc]))

;; constants and helpers

(def speed
  "bullet muzzle velocity"
  100)

(def lifetime
  "lifetime (ticks) of a bullet"
  50)

(def radius
  "radius of a bullet"
  10)

(def svg-length
  "length of the bullet svg-line in units of velocity"
  2)

;; model

(defn create
  "make a bullet launched from the given object"
  [{:keys [x y vx vy a] :as obj}]
  (let [[ax ay] (ra->xy speed (deg->rad a))]
    {:x (+ x ax) :y (+ y ay) :r radius :life lifetime
     :vx (+ vx ax) :vy (+ vy ay)
     :id (id/get-id)}))

;; manipulation

(defn tick
  "tick one bullet"
  [obj]
  (if (misc/kill? obj) sp/NONE
      (-> obj
          (misc/physics)
          (assoc :life (dec (:life obj))))))

;; view

(defn svg
  "make a line svg for the given bullet"
  [{:keys [x y vx vy a life] :as obj}]
  [:line
   {:x1 (floor (- x vx)) :y1 (floor (- y vy))
    :x2 (floor (+ x (* svg-length vx))) :y2 (floor (+ y (* svg-length vy)))
    :stroke "#00ffff" :stroke-width 10}])
