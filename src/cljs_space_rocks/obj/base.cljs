(ns cljs-space-rocks.obj.base
  "ns for functions etc related to the space bases"
  (:require
   [helper.geom :as geom :refer [ra->xy tau]]
   [helper.color :refer [hsl]]
   [helper.svg :as svg :refer [css-transform]]
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq dissoc-fn sjoin]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.misc :as misc :refer [not-NONE]]))


;; constants and helpers

(def types
  "types of bases"
  {:gem "refinery that accepts valuabel minerals"
   :pod "hospital that accepts escape pods"})

(def glow-max
  "ticks to go from max to min glow"
  100)

(def pod-r 30)
(def gem-r 30)
(def radius
  {:gem gem-r
   :pod pod-r})


(def pod-va1 2)
(def pod-va2 1)
(def gem-va1 2)
(def gem-va2 -4)

(def va1
  {:gem gem-va1
   :pod pod-va1})

(def va2
  {:gem gem-va2
   :pod pod-va2})

(def gray (hsl 0 0 30))
(def lgray (hsl 0 0 55))
(def dgray (hsl 0 0 20))
(def llgray (hsl 0 0 75))

(defn svg-emblem-medic
  "medical emblem svg"
  [r stroke]
  [:path
   {:stroke stroke
    :stroke-width 3
    :fill "none"
    :d (sjoin
        ["M" (- r) 0
         "L" r 0
         "M" 0 (- r)
         "L" 0 r])}])

(defn svg-emblem-miner
  "the miners emblem svg"
  [r stroke]
  [:path
   {:stroke stroke
    :stroke-width 1.5
    :fill "none"
    :d (sjoin
        ["M" (- r) 0
         "L" 0 r
         "L" r 0
         "L" 0 (- r)
         "L" (- r) 0
         "L" r 0])}])


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
  [:line {:x1 0 :y1 0 :x2 x :y2 y :stroke dgray :stroke-width 4}])
(defn get-gem-cir2
  [[x y]]
  [:circle {:cx x :cy y :r 10 :fill gray :stroke "black"}])

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
   [:circle {:cx 0 :cy 0 :r 8 :fill lgray :stroke dim}]
   [:line {:x1 0 :y1 0 :x2 gem-r :y2 0 :stroke lgray :stroke-width 7}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke lgray :stroke-width 8}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke dim :stroke-width 4}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke bright :stroke-width 2}]
   (svg-emblem-miner 5 "#0055CC")])


(defn pod-bottom
  [a]
  (into [:g {:transform (svg/css-rotate a)}]
        (map get-gem-arm1 gem-p1s)))

(defn pod-middle
  [a]
  [:g {:transform (svg/css-rotate a)}
   [:line {:x1 0 :y1 (- 15) :x2 0 :y2 (- pod-r) :stroke-width 3 :stroke llgray}]
   [:line {:x1 0 :y1 15 :x2 0 :y2 pod-r :stroke-width 3 :stroke llgray}]
   [:ellipse {:cx 0 :cy (- 15) :rx 10 :ry 5 :fill llgray :stroke gray}]
   [:ellipse {:cx 0 :cy 15 :rx 10 :ry 5 :fill llgray :stroke gray}]])

(defn pod-top
  [bright dim]
  [:g {}
   [:circle {:cx 0 :cy 0 :r 8 :fill lgray :stroke dim}]
   [:line {:x1 0 :y1 0 :x2 gem-r :y2 0 :stroke lgray :stroke-width 7}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke lgray :stroke-width 8}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke dim :stroke-width 4}]
   [:circle {:cx 0 :cy 0 :r gem-r :fill "none" :stroke bright :stroke-width 2}]
   (svg-emblem-medic 5 "#FF0000")])

;; model

(defn create
  "create a base"
  [x y a type]
  {:x x :y y :a a :type type
   :r (type radius) :glow 0 :id (id/get-id)
   :a1 0 :a2 0 :va1 (type va1) :va2 (type va2)})

(defn initial-bases
  []
  (assoc-fn
   {} :id
   (create 150 500 (drand/rangle) :gem)
  ;  (create 650 150 (drand/rangle) :pod)
))

;; query

;; manipulation

;; update

(defn tick
  "tick the given base"
  [obj]
  (-> obj
      (misc/physics)
      (update :glow #(max 0 (dec %)))
      (update :a1 #(+ % (:va1 obj)))
      (update :a2 #(+ % (:va2 obj)))))

;; svg

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

(defmulti svg :type)
(defmethod svg :gem [base] (svg-refinery base))
(defmethod svg :pod [base] (svg-hospital base))
