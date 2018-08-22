(ns cljs-space-rocks.obj.base
  "ns for functions etc related to the space bases"
  (:require
   [re-frame.core :as rf]
   [helper.geom :as geom :refer [ra->xy tau]]
   [helper.color :refer [hsl]]
   [helper.svg :as svg :refer [css-transform]]
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq dissoc-fn sjoin]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.emblem :as emb]
   [cljs-space-rocks.misc :as misc :refer [not-NONE]]
   [cljs-space-rocks.obj :as obj]))

;; constants and helpers

(def types
  "types of bases"
  {::gem "refinery that accepts valuable minerals"
   ::pod "hospital that accepts escape pods"})

(def reses
  "resource types"
  {:gem "valuable minerals"
   :pod "lifepods from exploding ships"})

(def glow-max
  "ticks to go from max to min glow"
  60)

(def pod-r 300)
(def gem-r 300)
(def radius
  {::gem gem-r
   ::pod pod-r})


(def pod-va1 2)
(def pod-va2 1)
(def gem-va1 2)
(def gem-va2 -4)

(def va1
  {::gem gem-va1
   ::pod pod-va1})

(def va2
  {::gem gem-va2
   ::pod pod-va2})

(def gray (hsl 0 0 30))
(def lgray (hsl 0 0 55))
(def dgray (hsl 0 0 20))
(def llgray (hsl 0 0 75))

;; model

(defn create
  "create a base"
  [x y a res type]
  {:x x :y y :a a :type type :res res
   :r (type radius) :glow 0 :id (id/get-id)
   :a1 0 :a2 0 :va1 (type va1) :va2 (type va2)})

(defn initial-bases
  []
  (assoc-fn
   {} :id
   (create 1500 5000 (drand/rangle) :gem ::gem)
   (create 6500 1500 (drand/rangle) :pod ::pod)))

;; query

;; manipulation

;; update

(defn tick
  "tick the given base"
  [obj]
  (-> obj
      transient
      (assoc! :glow (max 0 (dec (:glow obj))))
      (assoc! :a1 (+ (:a1 obj) (:va1 obj)))
      (assoc! :a2 (+ (:a2 obj) (:va2 obj)))
      persistent!))

(defmethod obj/tick ::gem [obj] (tick obj))
(defmethod obj/tick ::pod [obj] (tick obj))

;; svg

(def svg-emblem-medic
  "medical emblem svg"
  (emb/fat-cross "#FF0000" 0 0 0 (emb/scale 50)))

(def svg-emblem-miner
  "the miners emblem svg"
  (emb/tee-bar "#FABA00" 0 0 0 (emb/scale 50)))

; (def gem-r1 (* gem-r 1.5))
(def gem-r1 gem-r)
(def gem-n1 7)
(def gem-n2 3)

(def pod-n1 6)
(def pod-n2 2)

(defn get-circle-pts [r n]
  (let [da (/ tau n)]
    (for [i (range n)
          :let [a (* i da)]]
      (ra->xy r a))))

(def gem-p1s (get-circle-pts gem-r1 gem-n1))
(def gem-p2s (get-circle-pts gem-r gem-n2))

(defn get-gem-arm1
  [[x y]]
  [:line {:x1 0 :y1 0 :x2 x :y2 y :stroke dgray :stroke-width 40}])
(defn get-gem-cir2
  [[x y]]
  [:circle {:cx x :cy y :r 100 :fill gray :stroke "black"}])

(defn gem-bottom
  [a]
  (into [:g {:transform (svg/css-rotate a)}]
        (map get-gem-arm1 gem-p1s)))

(defn gem-middle
  [a]
  (into [:g {:transform (svg/css-rotate a)}]
        (map get-gem-cir2 gem-p2s)))

(defn gem-top
  [bright dim]
  [:g {}
   [:circle {:cx 0 :cy 0 :r 80 :fill lgray :stroke dim}]
   [:line {:x1 0 :y1 0 :x2 gem-r :y2 0 :stroke lgray :stroke-width 70}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke lgray :stroke-width 80}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke dim :stroke-width 40}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke bright :stroke-width 20}]
   svg-emblem-miner])


(defn pod-bottom
  [a]
  (into [:g {:transform (svg/css-rotate a)}]
        (map get-gem-arm1 gem-p1s)))

(defn pod-middle
  [a]
  [:g {:transform (svg/css-rotate a)}
   [:line {:x1 0 :y1 (- 150) :x2 0 :y2 (- pod-r) :stroke-width 30 :stroke llgray}]
   [:line {:x1 0 :y1 150 :x2 0 :y2 pod-r :stroke-width 30 :stroke llgray}]
   [:ellipse {:cx 0 :cy (- 150) :rx 70 :ry 40 :fill llgray :stroke gray}]
   [:ellipse {:cx 0 :cy 150 :rx 70 :ry 40 :fill llgray :stroke gray}]])

(defn pod-top
  [bright dim]
  [:g {}
   [:circle {:cx 0 :cy 0 :r 80 :fill lgray :stroke dim}]
   [:line {:x1 0 :y1 0 :x2 gem-r :y2 0 :stroke lgray :stroke-width 140}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke lgray :stroke-width 80}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke dim :stroke-width 40}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke bright :stroke-width 20}]
   svg-emblem-medic])

(defn svg-refinery
  [{:keys [x y a1 a2 r glow] :as base}]
  (let [ratio (/ glow glow-max)
        bright (misc/get-glow-bright ratio)
        dim (misc/get-glow-dim ratio)
        ri (* r 1.5)]
    [:g {:transform (css-transform base)}
     (gem-bottom a1)
     (gem-middle a2)
     (gem-top bright dim)]))

(defn svg-hospital2
  [{:keys [x y a r glow] :as base}]
  (let [ratio (/ glow glow-max)
        bright (misc/get-glow-bright ratio)
        dim (misc/get-glow-dim ratio)]
    [:circle
     {:cx x :cy y :r r
      :fill bright :stroke dim :stroke-width 2}]))

(defn svg-hospital
  [{:keys [x y a1 a2 r glow] :as base}]
  (let [ratio (/ glow glow-max)
        bright (misc/get-glow-bright ratio)
        dim (misc/get-glow-dim ratio)
        ri (* r 1.5)]
    [:g {:transform (css-transform base)}
     (pod-bottom a1)
     (pod-middle a2)
     (pod-top bright dim)]))

(defmethod obj/svg ::gem [base] (svg-refinery base))
(defmethod obj/svg ::pod [base] (svg-hospital base))
