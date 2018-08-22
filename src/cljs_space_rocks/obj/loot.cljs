(ns cljs-space-rocks.obj.loot
  "ns for gem & pod functions"
  (:require
   [com.rpl.specter :as sp]
   [re-frame.core :as rf]
   [helper.geom :refer [ra->xy deg->rad]]
   [helper.svg :refer [css-transform]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.obj :as obj]))

;; constants

(def types
  {::gem "a valuable crystalline mineral"
   ::pod "an escape pod or lifepod from an exploded ship"})

(def res->type
  "resource names to loot-type names"
  {:gem ::gem
   :pod ::pod})

(def lifetime "how many ticks loot lasts before fading" 1500)
(def speed "additional velocity loot gets on creation" 10)
(def spin "additional angular velocity loot gets on creation" 10)
(def radius 35)

;; helpers

;; model

(defn create
  "create from args"
  [x y vx vy a va res type]
  {:x x :y y :vx vx :vy vy :a a :va va
   :r radius :id (id/get-id)
   :res res :type type
   :life lifetime})

(defn obj->loot
  "create a loot item on the given obj"
  [{:keys [x y a vx vy va r] :as obj} res]
  (let [dva (drand/dctr spin)
        [ax ay] (ra->xy (drand/drand (/ speed 2) speed) (drand/dangle))
        [dx dy] (ra->xy (drand/drand r) (drand/dangle))]
    (create (+ x dx) (+ y dy) (+ vx ax) (+ vy ay) a (+ va dva) res (res res->type))))

;; query

;; manipulation

;; update

(defn tick
  "tick one loot obj"
  [obj]
  (if (obj/kill? obj) sp/NONE
      (-> obj
          transient
          (obj/physics!)
          (assoc! :life (dec (:life obj)))
          persistent!)))

(defmethod obj/tick ::gem [obj] (tick obj))
(defmethod obj/tick ::pod [obj] (tick obj))

;; svg

(defn draw-gem
  [{:keys [life r] :as pod}]
  (let [ratio (/ life lifetime)
        bright (misc/get-glow-bright ratio)
        dim (misc/get-glow-dim ratio)]
    [:g {:transform (css-transform pod)}
     [:rect {:x (- r) :y (- r) :width (* 2 r) :height (* 2 r) :fill dim}]
     [:rect {:x 0 :y 0 :width r :height r :fill bright}]
     [:rect {:x (- r) :y (- r) :width r :height r :fill bright}]]))

(defn draw-pod
  [{:keys [x y a r life] :as pod}]
  (let [ratio (/ life lifetime)
        glow (misc/get-glow-bright ratio)
        dim (misc/get-glow-dim ratio)]
    [:g {:transform (css-transform pod)}
     [:circle
      {:cx 0 :cy r :r (/ r 2.5)
       :fill glow :stroke dim
       :stroke-width 10}]
     [:circle
      {:cx 0 :cy 0 :r r
       :fill "#DDDDDD" :stroke "#FF0000"
       :stroke-width 15}]
     [:circle
      {:cx 0 :cy r :r (/ r 2.5)
       :fill glow :stroke dim
       :stroke-width 10}]]))

(defmethod obj/svg ::gem [obj] (draw-gem obj))
(defmethod obj/svg ::pod [obj] (draw-pod obj))
