(ns cljs-space-rocks.misc
  "misc constants and helper functions"
  (:require
   [com.rpl.specter :as sp]
   [helper.fun :as fun :refer [floor]]
   [helper.color :refer [rgb hsl]]
   [helper.rf :as hr :refer [<sub >evt spy]]
   [helper.log :refer [clog]]
   [helper.geom :as geom :refer [max-degrees ra->xy deg->rad]]
   [cljs-space-rocks.drand :as drand]))

;; svg
(def xt-box
  "width of the play area in internal units"
  8000)

(def yt-box
  "height of the play area in internal units"
  6500)

(def max-x xt-box)
(def max-y yt-box)

(def xc-box
  "center of the play area
  x-axis rounded down in internal units"
  (floor (/ xt-box 2)))

(def yc-box
  "center of the play area
  y-axis rounded down in internal units"
  (floor (/ yt-box 2)))

(def svg-ratio "ratio of width/height" (/ xt-box yt-box))

(defn choose-svg-size
  "given window width and height, choose the dimensions for the SVG"
  [w h]
  (let [h' (* h svg-ratio)]
    (if (> w h')
      [h' h]
      [w (/ w svg-ratio)])))

;; onscreen functions

(defn x-onscreen?
  "is the given x-coordinate on screen? 1 arg assumes r=0"
  ([x r]
   (fun/inside? x r xt-box))
  ([x]
   (x-onscreen? x 0)))

(defn y-onscreen?
  "is the given y-coordinate on screen? 1 arg assumes r=0"
  ([y r]
   (fun/inside? y r yt-box))
  ([y]
   (y-onscreen? y 0)))

(defn onscreen?
  "is the given x y r circle onscreen?
  2 args assumes r=0, 1 arg assumes map"
  ([x y r]
   (and (x-onscreen? x r) (y-onscreen? y r)))
  ([x y]
   (onscreen? x y 0))
  ([{:keys [x y r]}]
   (onscreen? x y r)))

(defn x-offscreen?
  "true if any of x+/-r is outside 0->xmax"
  ([x r]
   (fun/outside? x r xt-box))
  ([x]
   (x-offscreen? x)))

(defn y-offscreen?
  "true if any of y+/-r is outside 0->ymax"
  ([y r]
   (fun/outside? y r yt-box))
  ([y]
   (y-offscreen? y 0)))

(defn get-offscreen
  "returns a vector of [x y]-offscreen? from an obj map"
  [{:keys [x y r]}]
  [(x-offscreen? x r) (y-offscreen? y r)])

(defn get-replica-objs
  "get replica objects offset by the svg-box size.
  these can be used to draw objects at the edge of the screen that should wrap."
  [{:keys [x y] :as obj}]
  (let [[sx sy] (get-offscreen obj)
        x' (- x (* sx max-x))
        y' (- y (* sy max-y))]
    (if (and (not sx) (not sy)) nil
        (cond-> [obj]
          sx (conj (merge obj {:x x' :y y}))
          sy (conj (merge obj {:x x :y y'}))
          (and sx sy) (conj (merge obj {:x x' :y y'}))))))

;; directions (degrees)

(def east 0)
(def south 90)
(def west 180)
(def north 270)

(def directions
  {:east east
   :south south
   :west west
   :north north})

(def opposite-directions
  {east west
   west east
   north south
   south north})

(defn dr-direction
  "get a (deterministically) random direction"
  []
  (drand/dnth (vals directions)))

(defn dr-angle-in
  "get an angle pointing into the area from the edge of the given direction.
  eg if north (270) is passed in, get a random angle pointing more-or-less south (90)"
  [dir]
  (let [dir' (get opposite-directions dir)]
    (drand/dctr dir' 60)))

;; glow colors

(defn get-glow-bright
  "get the brighter color for the engine glow.
  ratio is 0-1 and hue is an hsl hue 0-360 0 red, 120 green, 240 blue"
  ([ratio]
   (get-glow-bright ratio 180))
  ([ratio hue]
   (hsl
    hue
    (+ 60 (* 40 ratio))
    (+ 50 (* 50 ratio)))))

(defn get-glow-dim
  "get the dimmer color for the engine glow"
  ([ratio hue]
   (hsl
    hue
    (+ 40 (* 40 ratio))
    (+ 30 (* 40 ratio))))
  ([ratio]
   (get-glow-dim ratio 200)))

;; specter

(defn is-NONE?
  [val]
  (= sp/NONE val))

(defn not-NONE
  "turn sp/NONE to nil"
  [val]
  (if (is-NONE? val) nil val))

;; random point on the edge

(defn drand-edge-point
  "get a (det) random point & angle [x,y,a] on the edge of the play area, the angle will point in."
  ([dir]
   (let [x (drand/dint xt-box)
         y (drand/dint yt-box)]
     (cond
       (= dir north) [x 0]
       (= dir east) [xt-box y]
       (= dir south) [x yt-box]
       (= dir west) [0 y])))
  ([]
   (drand-edge-point (dr-direction))))

;; local-store

(def ls-score-key "cljs-space-rocks")

