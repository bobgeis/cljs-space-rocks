(ns cljs-space-rocks.obj.loot
  "ns for gem & pod functions"
  (:require
   [com.rpl.specter :as sp]
   [helper.geom :refer [ra->xy deg->rad]]
   [helper.svg :refer [css-transform]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.misc :as misc]))

;; constants

(def types
  {:gem "a valuable crystalline mineral"
   :pod "an escape pod or lifepod from an exploded ship"})

(def lifetime 1500)
(def speed (/ 50 60))
(def spin 0.2)
(def radius 3.5)

;; helpers

;; model

(defn create
  "create from args"
  [x y vx vy a va type]
  {:x x :y y :vx vx :vy vy :a a :va va
   :r radius :type type :id (id/get-id)
   :life lifetime})

(defn obj->loot
  "create a loot item on the given obj"
  [{:keys [x y a vx vy va r] :as obj} type]
  (let [dva (drand/dctr spin)
        [ax ay] (ra->xy (drand/drand speed) (drand/dangle))
        [dx dy] (ra->xy (drand/drand r) (drand/dangle))]
    (create (+ x dx) (+ y dy) (+ vx ax) (+ vy ay) a (+ va dva) type)))

;; query

;; manipulation

;; update

(defn tick
  "tick one loot obj"
  [obj]
  (if (misc/kill? obj) sp/NONE
      (-> obj
          (misc/physics)
          (update :life dec))))

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
    [:circle
     {:cx x :cy y :r r
      :fill glow :stroke dim}]))

(defmulti svg :type)
(defmethod svg :gem  [obj] (draw-gem obj))
(defmethod svg :pod [obj] (draw-pod obj))
