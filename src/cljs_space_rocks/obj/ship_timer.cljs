(ns cljs-space-rocks.obj.ship-timer
  "ns for fns etc for spawning of new ships"
  (:require
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq dissoc-fn floor]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.obj.boom :as boom]
   [cljs-space-rocks.obj.ship :as ship]))

;; constants

(def init-max-roll
  "initial maximum number of ticks between spawns"
  120)

(def init-min-roll
  "initial minimum number of ticks between spawns"
  (floor (/ init-max-roll 2)))

(def min-max-roll
  "lowest max-roll can go"
  30)

(def min-min-roll
  "lowest min-roll can go"
  (floor (/ min-max-roll 2)))

(def speed-up
  "factor which determines ship spawn rate increases with player score
  formula is: current-max-roll = (max (floor (- r-max (* (+ gem pod) speed-up))) min-max-roll)"
  0.5)

(def num-rolls
  "number of rolls to get the next tick"
  5)

;; model / update

(defn update-timer
  "given a timer, calculate and assoc the next ship"
  [{:keys [r-min r-max r-num seed] :as timer} {gem :gem pod :pod}]
  (let [set-seed! (drand/set-seed! seed)
        current-max-roll (max (floor (- r-max (* (+ gem pod) speed-up))) min-max-roll)
        current-min-roll (max (floor (- r-min (* (+ gem pod) speed-up 0.5))) min-min-roll)
        countdown (apply + (repeatedly r-num #(drand/dint current-min-roll current-max-roll)))
        ship (ship/make-spawn)
        new-seed (drand/drseed)]
    (assoc timer
           :countdown countdown
           :ship ship
           :seed new-seed)))

(defn create
  "create a new ship-timer"
  [seed r-min r-max r-num]
  (update-timer {:seed seed :r-min r-min :r-max r-max :r-num r-num} {:pod 0 :gem 0}))

(defn init-timer
  "initialize the ship-timer"
  [] (create (drand/rrseed) init-min-roll init-max-roll num-rolls))

;; manipulation

(defn dec-countdown
  [timer]
  (update timer :countdown dec))

(defn tick
  [{{:keys [countdown ship] :as timer} :ship-timer
    :keys [ships booms score] :as scene}]
  (cond
    ;; if we're at 0, add ship & reset timer
    (= 0 countdown)
    (assoc scene
           :ships (assoc-fn ships :id ship)
           :ship-timer (update-timer timer score))
    ;; if we're at the flash time, add the flash obj
    (= (::boom/in boom/type->lifetime) countdown)
    (assoc scene
           :booms (assoc-fn booms :id (boom/obj->in ship {:fizzbuzz false}))
           :ship-timer (dec-countdown timer))
    ;; otherwise add the decremented counter
    :else
    (assoc scene :ship-timer (dec-countdown timer))))


