(ns cljs-space-rocks.emblem
  "ns for drawing emblems for ships and stations"
  (:require
   [helper.log :refer [clog]]
   [helper.svg :refer [css-transform]]
   [helper.geom :as geo]
   [helper.fun :as fun :refer [sjoin]]))

;; constants

(def emblem-width 500)

(defn scale
  "scale the emblem to a given radius"
  [r]
  (/ r emblem-width))

;; medic - fat-cross

(def fat-cross-path
  (sjoin
   ["M" -475 0
    "H" 475
    "M" 0 -475
    "V" 475]))

(defn fat-cross
  ([color transform]
   [:path
    {:stroke color
     :transform transform
     :fill "none"
     :stroke-width 325
     :d fat-cross-path}])
  ([color x y a s]
   (fat-cross color (css-transform x y a s))))

;; hammer - tee-bar

(def tee-bar-path
  (sjoin
   ["M" 375 -400
    "V" 400
    "M" 75 -400
    "V" 400
    "M" 75 0
    "H" -475]))

(defn tee-bar
  ([color transform]
   [:path
    {:transform transform
     :stroke color
     :fill "none"
     :stroke-width 200
     :d tee-bar-path}])
  ([color x y a s]
   (tee-bar color (css-transform x y a s))))

;; orbitals - six ellipses

(def orbitals-path
  (sjoin
   ["M" 450 0
    "A" 450 150 0 1 0 -450 0
    "A" 450 150 0 1 0 450 0
    "M" (* 450 geo/cos60) (* 450 geo/sin60)
    "A" 450 150 60 1 0 (* -450 geo/cos60) (* -450 geo/sin60)
    "A" 450 150 60 1 0 (* 450 geo/cos60) (* 450 geo/sin60)
    "M" (* -450 geo/cos60) (* 450 geo/sin60)
    "A" 450 150 -60 1 0 (* 450 geo/cos60) (* -450 geo/sin60)
    "A" 450 150 -60 1 0 (* -450 geo/cos60) (* 450 geo/sin60)]))

(defn orbitals
  ([color transform]
   [:path
    {:transform transform
     :stroke color
     :fill "none"
     :stroke-width 75
     :d orbitals-path}])
  ([color x y a s]
   (orbitals color (css-transform x y a s))))

;; widget-maker

(def widget-maker-path
  (sjoin
   ["M" -200 -125
    "H" 475
    "M" -200 125
    "H" 475
    "M" -475 -375
    "H" 150
    "V" 375
    "H" -375
    "V" -375]))

(defn widget-maker
  ([color transform]
   [:path
    {:transform transform
     :stroke color
     :fill "none"
     :stroke-width 200
     :d widget-maker-path}])
  ([color x y a s]
   (widget-maker color (css-transform x y a s))))

;; widget-maker

(def pipe-triangle-path
  (sjoin
   ["M" -100 -350
    "V" 350
    "L" 375 0
    "Z"
    "M" 150 0
    "H" -450]))

(defn pipe-triangle
  ([color transform]
   [:path
    {:transform transform
     :stroke color
     :fill "none"
     :stroke-width 125
     :d pipe-triangle-path}])
  ([color x y a s]
   (pipe-triangle color (css-transform x y a s))))

;; shield

(def shield-path
  (sjoin
   ["M" 400 400
    "V" -400
    "H" 0
    "L" -400 0
    "L" 0 400
    "Z"]))

(defn shield
  ([color transform]
   [:path
    {:transform transform
     :fill color
     :d shield-path}])
  ([color x y a s]
   (shield color (css-transform x y a s))))

;; test view

(defn test-view
  []
  [:g {:transform (css-transform 2500 600)}
   [:rect
    {:x -500 :y -500 :width 1000 :height 1000 :fill "#FFFFFF" :stroke "#000000" :stroke-width 10}]
   ;; for testing
  ;  (widget-maker "#0000FF" (css-transform 0 0 0 1))
  ;  (pipe-triangle "#0000FF" (css-transform 0 0 0 1))
  ;  (fat-cross "#0000FF" (css-transform 0 0 0 1))
  ;  (tee-bar "#0000FF" (css-transform 0 0 0 1))
  ;  (orbitals "#0000FF" (css-transform 0 0 0 1))
   (shield "#0000FF" (css-transform 0 0 0 1))
   ;;
])
