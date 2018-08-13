(ns cljs-space-rocks.obj.player
  "ns for constants and functions related to the player"
  (:require
   [clojure.string :as string]
   [re-frame.core :as rf]
   [helper.log :refer [clog]]
   [helper.fun :as fun :refer [sjoin]]
   [helper.geom :as geom :refer [ra->xy deg->rad]]
   [helper.svg :as svg :refer [css-transform]]
   [helper.color :as color :refer [rgb hsl]]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.emblem :as emb]
   [cljs-space-rocks.obj :as obj]))

;; player constants

(def player-radius
  "radius of the player ship (should be 10)"
  100)

(def player-turn-speed
  "turn speed in degrees per tick"
  4)

(def player-thrust
  "player forward acceleration in px per tick per tick"
  1)

(def player-retro
  "player reverse acceleration in px per tick per tick"
  -0.2)

(def player-drag
  "drag on the player ship"
  0.005)

(def player-reload
  "how long (ticks) to reload the cannon between shots"
  6)

(def player-max-glow
  "number of ticks maximum glow lasts"
  30)

(def glow-fire
  "glow setting after firing"
  (* 1 player-max-glow))

(def glow-thrust
  "glow setting while thrusting"
  (* 0.67 player-max-glow))

(def glow-turn
  "glow setting while turning"
  (* 0.33 player-max-glow))

;; helpers

;; model

(defn initial-player
  "initial player data"
  []
  {:x misc/xc-box :y misc/yc-box ;; starting position
   :vx 0 :vy 0 ;; velocity (px/tick)
   :a misc/north :va 0 ;; angle (deg) and angular velocity (deg/tick)
   :acc 0 ;; acceleration (forward px/tick/tick)
   :drag player-drag ;; drag (ratio/tick)
   :r player-radius ;; radius
   :glow 0 ;; glow state
   :turn-rate player-turn-speed
   :thrust player-thrust
   :retro player-retro
   :reload 0
   :firing false
   :type ::player
   :fizzbuzz true})

;; manipulation

(defn turn
  "turn the ship
  sign = 0 to stop
  sign = +1 to turn clockwise
  sign = -1 to turn widdershins"
  [ship sign]
  (assoc ship :va (* sign (:turn-rate ship))))

(defn acc
  "accelerate the ship
  sign = 0 to stop
  sign = +1 thrust forward
  sign = -1 to retro in reverse"
  [ship sign]
  (let [dv (cond (= sign 0) 0
                 (> sign 0) (:thrust ship)
                 (< sign 0) (:retro ship))]
    (assoc ship :acc dv)))

(defn set-firing
  "set the firing state: true/false"
  [ship firing]
  (assoc ship :firing firing))

(defn set-neutral
  "set the ship control to no acc/turn/firing"
  [ship]
  (assoc ship :acc 0 :va 0 :firing false))

(defn fire?
  "is the ship firing and ready to fire?"
  [{reload :reload firing :firing}]
  (and (= 0 reload) firing))

(defn tick-reload
  "update the reload for this tick"
  [{reload :reload :as player}]
  (if (fire? player)
    player-reload
    (max 0 (dec reload))))

(defn tick-glow
  "update the glow for this tick"
  [{:keys [glow acc va] :as ship}]
  (cond
    (fire? ship) (max (dec glow) glow-fire)
    (not= 0 acc) (max (dec glow) glow-thrust)
    (not= 0 va) (max (dec glow) glow-turn)
    :else (max 0 (dec glow))))

(defn tick
  "progress the ship one tick"
  [ship]
  (-> ship
      (obj/physics)
      (assoc
       :glow (tick-glow ship)
       :reload (tick-reload ship))))

(defmethod obj/tick ::player [obj] (tick obj))

(defn merge-control
  "merge control settings onto player"
  [player {:keys [acc va firing] :as control}]
  (assoc player
         :firing firing
         :acc acc
         :va va))

;; view

(defn svg-body
  [r fill stroke]
  [:path
   {:fill fill
    :stroke stroke
    :stroke-width 10
    :d (sjoin
        ["M" 0 r
         "C" (* 1.5 r) r (* 1.5 r) (- r) 0 (- r)
         "Q" (* 0.5 r) (* -0.25 r) (- r) 0
         "Q" (* 0.5 r) (* 0.25 r) 0 r])}])

(def player-body-path
  "svg path for the player ship"
  (svg-body player-radius "#FFFFFF" "#FF0000"))

(defn player-one-engine-path
  "svg path for one player engine"
  [r sign bright dim]
  [:ellipse
   {:fill bright
    :stroke dim
    :stroke-width 10
    :cx (/ r 12)
    :cy (* r 0.5 sign)
    :rx (* 0.5 r)
    :ry (* 0.125 r)}])

(defn svg-player
  "svg group for the player ship"
  [{:keys [x y a glow r] :as player}]
  (let [ratio (/ glow player-max-glow)
        bright (misc/get-glow-bright ratio)
        dim (misc/get-glow-dim ratio)]
    [:g {:transform (css-transform player)}
     player-body-path
    ;  player-emblem-path
     (emb/fat-cross "#FF0000" (/ r 2) 0 0 (emb/scale (/ r 3)))
    ;  (emb/orbitals "#FF0000" (/ r 2) 0 0 (emb/scale (/ r 3)))
    ;  (emb/tee-bar "#FF0000" (/ r 2) 0 0 (emb/scale (/ r 3)))
     (player-one-engine-path player-radius 1 bright dim)
     (player-one-engine-path player-radius -1 bright dim)]))

(defmethod obj/svg ::player [obj] (svg-player obj))

(defn svg-omega-player
  [{:keys [x y a] :as player}]
  (let [r player-radius
        color (hsl 150 100 75 0.5)]
    [:g {:transform (css-transform player)}
     (svg-body player-radius color color)
     (player-one-engine-path r 1 color color)
     (player-one-engine-path r -1 color color)
     [:circle {:stroke color :stroke-width 20 :fill "none"
               :cx 0 :cy 0 :r (* 7 player-radius)}]]))
