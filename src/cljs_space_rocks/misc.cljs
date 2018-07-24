(ns cljs-space-rocks.misc
  "misc constants and helper functions"
  (:require
   [com.rpl.specter :as sp]
   [helper.fun :as fun :refer [floor]]
   [helper.color :refer [rgb hsl]]
   [helper.log :refer [clog]]
   [helper.geom :as geom :refer [max-degrees ra->xy deg->rad]]
   [cljs-space-rocks.drand :as drand]))

;; size of the play svg in external coordinates
(def xt-svg 800)
(def yt-svg 650)
(def xc-svg (/ xt-svg 2))
(def yc-svg (/ yt-svg 2))
(def svg-ratio (/ xt-svg yt-svg))

(defn choose-size
  [w h]
  (let [h' (* h svg-ratio)]
    (if (> w h')
      [h' h]
      [w (/ w svg-ratio)])))

;; size of the play svg viewbox
(def xt-box
  "width of the play area"
  8000)

(def yt-box
  "height of the play area"
  6500)

(def xc-box
  "center of the play area
  x-axis rounded down"
  (floor (/ xt-box 2)))

(def yc-box
  "center of the play area
  y-axis rounded down"
  (floor (/ yt-box 2)))

;; drag

(defn apply-drag
  [v d]
  (* v (- 1 d)))

;; physics

(defn physics
  "move an object using velocity, acceleration and drag"
  [{:keys [x vx y vy a va acc drag clamp] :as obj}]
  (let [[ax ay] (if acc (ra->xy acc (deg->rad a)) [0 0])
        edge (if clamp fun/clamp fun/wrap)]
    (assoc obj
           :x (edge (+ x vx ax) xt-box)
           :y (edge (+ y vy ay) yt-box)
           :a (fun/wrap (+ a va) max-degrees)
           :vx (apply-drag (+ vx ax) drag)
           :vy (apply-drag (+ vy ay) drag))))

(defn kill?
  "an object should be removed if its life is 0"
  [obj]
  (= 0 (:life obj)))


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
   (or (x-onscreen? x r) (y-onscreen? y r)))
  ([x y]
   (onscreen? x y 0))
  ([{:keys [x y r]}]
   (onscreen? x y r)))

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

(defn dr-direction
  "get a (deterministically) random direction"
  []
  (drand/dnth (vals directions)))

;; glow colors

(defn get-glow-bright
  "get the brighter color for the engine glow"
  [ratio]
  (hsl
   180
   (+ 60 (* 40 ratio))
   (+ 50 (* 40 ratio))))

(defn get-glow-dim
  "get the dimmer color for the engine glow"
  [ratio]
  (hsl
   200
   (+ 40 (* 40 ratio))
   (+ 40 (* 40 ratio))))

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
  "get a (det) random point [x,y] on the edge of the play area"
  []
  (let [dir (dr-direction)
        x (drand/dint xt-box)
        y (drand/dint yt-box)]
    (cond
      (= dir north) [x 0]
      (= dir east) [xt-box y]
      (= dir south) [x yt-box]
      (= dir west) [0 y])))

;; omega-13 related

(def omega-13-countdown
  "number of ticks it takes for the omega-13 to activate."
  120)

(def omega-13-seconds
  "number of seconds for the omega-13 to become available"
  13)

(def ticks-per-second
  60)

(def scenes-per-second
  "how many scenes to save every second"
  6)

(def ticks-per-scene-save
  "how many ticks to wait before saving a scene"
  (/ ticks-per-second scenes-per-second))

(def number-scenes-to-save
  "how many scenes should be saved (max)"
  (* omega-13-seconds scenes-per-second))
