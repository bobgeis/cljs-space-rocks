(ns cljs-space-rocks.obj.boom
  "ns for 'boom' functions and constants.
  Booms are circular visual effects produced by explosions or FTL jumps."
  (:require
   [com.rpl.specter :as sp]
   [helper.fun :as fun :refer [assoc-fn]]
   [helper.color :refer [hsl]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.id :as id]))


;; constants

(def types
  {:rock-ex "a rock exploding"
   :rock-in "a rock being dropped into the zone"
   :ship-ex "a ship exploding"
   :ship-in "a ship FTLing into the zone"})

(def type->lifetime
  "lifetime of the boom (ticks)"
  {:rock-ex 13
   :rock-in 10
   :ship-ex 15
   :ship-in 10
   :long-life 10000})

(def outer-radii
  "radii of the outer edge of the boom (px)"
  {:rock-ex 60
   :rock-in 40
   :ship-ex 40
   :ship-in 30})

(def type->dr
  "change in radius of boom"
  {:rock-ex 5
   :rock-in 2
   :ship-ex 3
   :ship-in 2
   :long-life 0})

;; helpers

(defn get-color-ex
  "choose the color given the life ratio"
  [ratio]
  (hsl
   (* ratio 60)
   100
   (+ 40 (* ratio 60))
   (+ 0.5 (/ ratio 2))))

(defn get-color-in
  [ratio]
  (hsl
   (- 240 (* ratio 60))
   100
   (+ 40 (* ratio 60))
   (+ 0.5 (/ ratio 2))))

(def type->get-color
  {:rock-ex get-color-ex
   :rock-in get-color-in
   :ship-ex get-color-ex
   :ship-in get-color-in
   :long-life get-color-ex})

;; model

(defn create
  "create from args"
  [x y vx vy r type]
  {:x x :y y :vx vx :vy vy :r r
   :type type :id (id/get-id)
   :dr (type type->dr)
   :life (type type->lifetime)})

(defn obj->boom
  "create a boom from an obj"
  [{:keys [x y vx vy r] :as obj} type]
  (create x y 0 0 r type))

(defn initial-booms
  "create some initial booms for testing"
  []
  {})
  ; (assoc-fn
  ;  {} :id
  ;  (create 50 50 0 0 100 :long-life)))

;; query

(defn kill?
  "a boom should be removed if its life is 0"
  [obj]
  (= 0 (:life obj)))

;; manipulation

(defn tick
  "tick one object"
  [obj]
  (if (kill? obj) sp/NONE
      (-> obj
          (misc/physics)
          (update :r #(+ % (:dr obj)))
          (update :life dec))))

;; view

(defn svg
  "make an svg for the given boom"
  [{:keys [x y a r type life] :as obj}]
  [:circle
   {:cx x :cy y :r r
    :fill ((type type->get-color)
           (/ life (type type->lifetime)))}])
