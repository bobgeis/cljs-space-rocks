(ns cljs-space-rocks.obj.ship
  "ns for traveler ships related data and functions"
  (:require
   [com.rpl.specter :as sp]
   [re-frame.core :as rf]
   [helper.geom :refer [ra->xy deg->rad]]
   [helper.log :refer [clog]]
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq sjoin floor]]
   [helper.svg :as svg :refer [css-transform]]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.emblem :as emb]
   [cljs-space-rocks.obj :as obj]
   [cljs-space-rocks.obj.loot :as loot]))

;; constants and helpers

(def factions
  {:med "medical vessel, transporting medicine, doctors, and/or patients"
   :min "ore freighter, containing valuable gems"
   :civ "civilian passenger ship"
   :sci "science or exploratory vessel"})

(def radius
  "ship radius in svg units"
  70)

(def speed
  "ship speed in svg-units/tick"
  10)

(def max-glow
  100)

(def hull-colors
  "ship colors"
  {:med "#FFFFFF"
   :min "#C8C8C8"
   :civ "#FAFAFA"
   :sci "#FFFFFF"})

(def trim-colors
  {:med "#FF0000"
   :min "#0000FF"
   :civ "#FA00FA"
   :sci "#009696"})

(def emblems
  {:med (emb/fat-cross "#FF0000" (/ radius 4) 0 0 (emb/scale (/ radius 2.5)))
   :min (emb/tee-bar "#0000FF" (/ radius 4) 0 0 (emb/scale (/ radius 2.5)))
   :civ (emb/pipe-triangle "#FA00FA" (/ radius 4) 0 0 (emb/scale (/ radius 2.5)))
   :sci (emb/orbitals "#009696" (/ radius 4) 0 0 (emb/scale (/ radius 2.5)))})

(def spawn-fac
  "vec of faction keywords to choose with rand nth"
  [:civ
   :civ
   :civ
   :civ
   :med
   :med
   :med
   :min
   :min
   :min
   :sci
   :sci])

(def max-loot "most loot that can be spawned at once" 3)
(def spawn-loot
  "vectors of loot for each fac type"
  {:civ [:pod :pod :pod]
   :min [:pod :gem :gem]
   :sci [:pod :pod :gem]
   :med [:pod :pod :pod]})

;; model / creation

(defn create
  "create a ship from args"
  [x y a vx vy fac seed]
  {:x x :y y :a a :vx vx :vy vy :r radius
   :fac fac :seed seed :id (id/get-id)
   :type ::ship :glow max-glow :clamp true})

(defn make-spawn
  "spawn a random ship on the edge of the map"
  []
  (let [dir (misc/dr-direction)
        [x y] (misc/drand-edge-point dir)
        a (misc/dr-angle-in dir)
        [vx vy] (ra->xy speed (deg->rad a))
        fac (drand/dnth spawn-fac)
        seed (drand/drseed)]
    (create x y a vx vy fac seed)))

(defn initial-ships
  "get the initial ships map"
  []
  (drand/set-seed! (drand/rrseed))
  (assoc-fn {} :id
            ;; for testing
            ; (create 5000 500 0 0 0 :civ 1)
            ; (create 5000 1000 0 0 0 :min 1)
            ; (create 5000 1500 0 0 0 :med 1)
            ; (create 5000 2000 0 0 0 :sci 1)
            (make-spawn)))

;; query

(defn on-edge?
  "is the ship on the edge of the play area?"
  [{x :x y :y}]
  (cond
    (= x 0) true
    (= y 0) true
    (= x obj/max-x) true
    (= y obj/max-y) true
    :else false))

;; manipulation

(defn make-loot
  "make a seq of loot when the given ship explodes"
  [{fac :fac seed :seed :as ship}]
  (drand/set-seed! seed)
  (let [items (take (drand/dint 1 max-loot) (fac spawn-loot))]
    (map #(loot/obj->loot ship %) items)))

;; update

(defmethod obj/tick ::ship
  [{:keys [glow] :as ship}]
  (-> ship
      obj/physics
      (assoc :glow (if (= glow 0) max-glow (dec glow)))))

;; view

(defn miner-emblem
  [x y a r color w]
  [:g {:transform (css-transform x y a)}
   [:path {:stroke color :fill "none" :stroke-width w
           :d
           (sjoin
            ["L" (* -0.5 r) 0
             "M" 0 (* -0.5 r)])}]])

(defmethod obj/svg ::ship
  [{:keys [x y r fac glow] :as ship}]
  (let [hull (fac hull-colors)
        trim (fac trim-colors)
        ratio (/ glow max-glow)
        bright (misc/get-glow-bright ratio)
        dim (misc/get-glow-dim ratio)]
    [:g {:transform (css-transform ship)}
     [:circle {:r r :cx 0 :cy 0 :fill hull :stroke trim :stroke-width 20}]
     (fac emblems)
     [:ellipse {:cx (* -0.75 r) :cy 0 :rx (* 0.6 r) :ry (/ r 4) :fill bright :stroke dim :stroke-width 10}]]))
