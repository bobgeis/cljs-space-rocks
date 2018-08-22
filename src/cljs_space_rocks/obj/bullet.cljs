(ns cljs-space-rocks.obj.bullet
  "ns for bullet constants and functions"
  (:require
   [com.rpl.specter :as sp]
   [re-frame.core :as rf]
   [helper.geom :refer [ra->xy deg->rad]]
   [helper.color :refer [hsl]]
   [helper.log :refer [clog]]
   [helper.fun :refer [floor]]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.obj :as obj]))

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

(def ext->int-type
  {:blue ::blue
   :gold ::gold
   :red ::red})

(def speeds
  {::blue 100
   ::gold 150
   ::red 80})

(def lifetimes
  {::blue 50
   ::gold 50
   ::red 60})

(def radii
  {::blue 10
   ::gold 25
   ::red 15})

(def type->hsl
  {::blue (hsl 180 100 90)
   ::gold (hsl 60 100 90)
   ::red (hsl 300 100 90)})

;; model

(defn create
  "make a bullet launched from the given object"
  ([{:keys [x y vx vy a bullet-type] :as obj}]
   (let [[ax ay] (ra->xy speed (deg->rad a))
         type (bullet-type ext->int-type)]
     {:x (+ x ax) :y (+ y ay) :a 0
      :vx (+ vx ax) :vy (+ vy ay)
      :type type
      :r (type radii)
      :life (type lifetimes)
      :id (id/get-id)}))
  ([obj override]
   (merge (create obj) override)))

(defn fire-bullets
  "make one or more bullets depending on the player's bullet-type"
  [{:keys [bullet-type] :as player}]
  (cond
    (= bullet-type :blue) [(create player)]
    (= bullet-type :gold) [(create player)]
    (= bullet-type :red)
    [(create (update player :a #(+ 15 %)))
     (create player)
     (create (update player :a #(+ -15 %)))]))


;; manipulation

(defn tick
  "tick one bullet"
  [obj]
  (if (obj/kill? obj) sp/NONE
      (-> obj
          transient
          (obj/physics!)
          (assoc! :life (dec (:life obj)))
          persistent!)))

(defmethod obj/tick ::blue [obj] (tick obj))
(defmethod obj/tick ::red [obj] (tick obj))
(defmethod obj/tick ::gold [obj] (tick obj))

;; view

(defn svg
  "make a line svg for the given bullet"
  [{:keys [x y vx vy a r type] :as obj}]
  [:line
   {:x1 (floor (- x vx)) :y1 (floor (- y vy))
    :x2 (floor (+ x (* svg-length vx))) :y2 (floor (+ y (* svg-length vy)))
    :stroke (type type->hsl) :stroke-width r}])

(defmethod obj/svg ::blue [obj] (svg obj))
(defmethod obj/svg ::red [obj] (svg obj))
(defmethod obj/svg ::gold [obj] (svg obj))
