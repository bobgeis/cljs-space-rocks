(ns cljs-space-rocks.obj.boom
  "ns for 'boom' functions and constants.
  Booms are circular visual effects produced by explosions."
  (:require
   [com.rpl.specter :as sp]
   [re-frame.core :as rf]
   [helper.fun :as fun :refer [assoc-fn floor]]
   [helper.color :refer [hsl]]
   [helper.rf :as hr :refer [<sub >evt spy]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.obj :as obj]))


;; constants

(def types
  {::ex "an explosion"
   ::in "a flash in"
   ::out "a flash out"})

(def type->lifetime
  "lifetime of the boom (ticks)"
  {::ex 13
   ::in 60
   ::out 20
   ::long-life 10000})

(def type->dr
  "change in radius of boom"
  {::ex 50
   ::in -20
   ::out 40
   ::long-life 0})

;; helpers

(defn get-fill-ex
  "get the fill color for ::ex given the life ratio"
  [ratio]
  (hsl
   (* ratio 60)
   100
   (+ 40 (* ratio 60))
   (+ 0.5 (/ ratio 2))))

(defn get-fill-in
  "get the fill color for ::in"
  [ratio]
  (hsl
   200
   100
   (- 100 (* 60 ratio))
   (fun/square (- 1 ratio))))

;; model

(defn create
  "create from args"
  [x y vx vy r type]
  {:x x :y y :vx vx :vy vy :r r
   :type type :id (id/get-id)
   :dr (type type->dr)
   :life (type type->lifetime)
   :fizzbuzz true})

(defn obj->ex
  "create a explosion from an obj"
  ([{:keys [x y vx vy r] :as obj}]
   (create x y 0 0 r ::ex)))

(defn obj->in
  "create a FTL arrival flash from an obj"
  ([{:keys [x y r]}]
   (let [dr (::in type->dr) life (::in type->lifetime)]
     (create x y 0 0 (- r (* dr life)) ::in)))
  ([obj opts]
   (merge (obj->in obj) opts)))

(defn obj->out
  "create an FTL exit flash from an obj"
  ([{:keys [x y r]}]
   (create x y 0 0 r ::out))
  ([obj opts]
   (merge (obj->out obj) opts)))

(defn initial-booms
  "the initial booms map"
  []
  {})

;; query

;; manipulation

(defn tick
  [obj]
  (if (obj/kill? obj) sp/NONE
      (-> obj
          transient
          (obj/physics!)
          (assoc! :r (+ (:r obj) (:dr obj)))
          (assoc! :life (dec (:life obj)))
          persistent!)))

(defmethod obj/tick ::ex [obj] (tick obj))
(defmethod obj/tick ::in [obj] (tick obj))
(defmethod obj/tick ::out [obj] (tick obj))

;; view

(defmethod obj/svg ::ex
  [{:keys [x y a r type life] :as obj}]
  [:circle
   {:cx (floor x) :cy (floor y) :r (floor r)
    :fill (get-fill-ex
           (/ life (::ex type->lifetime)))}])

(defmethod obj/svg ::in
  [{:keys [x y a r type life] :as obj}]
  [:circle
   {:cx (floor x) :cy (floor y) :r (floor r)
    :fill (get-fill-in (/ life (type type->lifetime)))
    :stroke-width 10}])

(defmethod obj/svg ::out
  [{:keys [x y a r type life] :as obj}]
  [:circle
   {:cx (floor x) :cy (floor y) :r (floor r)
    :fill (get-fill-in (- 1 (/ life (type type->lifetime))))
    :stroke-width 10}])
