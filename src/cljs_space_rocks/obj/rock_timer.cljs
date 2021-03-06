(ns cljs-space-rocks.obj.rock-timer
  "ns for functions related to the spawning of new rocks"
  (:require
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq dissoc-fn floor]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.obj.boom :as boom]
   [cljs-space-rocks.obj.rock :as rock]))

;; constants

(def init-max-roll
  "initial maximum number of ticks between spawns"
  100)

(def init-min-roll
  "initial minimum number of ticks between spawns"
  (floor (/ init-max-roll 2)))

(def min-max-roll
  "lowest max-roll can go"
  20)

(def min-min-roll
  "lowest min-roll can go"
  (floor (/ min-max-roll 2)))

(def speed-up
  "factor determining how player ship score increases rock spawn rate
  formula is: current-max-roll = (max (floor (- r-max (* ship speed-up))) min-max-roll)"
  0.5)

(def num-rolls
  "number of rolls to get the next tick"
  5)

;; helpers

;; model

(defn update-timer
  "given a timer, calculate and assoc the next rock"
  [{:keys [r-min r-max r-num seed] :as timer} {ship :ship}]
  (let [set-seed! (drand/set-seed! seed)
        current-max-roll (max (floor (- r-max (* ship speed-up))) min-max-roll)
        current-min-roll (max (floor (- r-min (* ship 0.5 speed-up))) min-min-roll)
        countdown (apply + (repeatedly r-num #(drand/dint current-min-roll current-max-roll)))
        rock (rock/make-spawn)
        new-seed (drand/drseed)]
    (assoc timer
           :countdown countdown
           :rock rock
           :seed new-seed)))

(defn create
  "create a new rock-timer"
  [seed r-min r-max r-num]
  (update-timer {:seed seed :r-min r-min :r-max r-max :r-num r-num} {:ship 0}))

(defn init-timer
  "initialize the rock-timer"
  [] (create (drand/rrseed) init-min-roll init-max-roll num-rolls))

;; manipulation

(defn dec-countdown
  [timer]
  (update timer :countdown dec))

(defn tick
  [{{:keys [countdown rock] :as timer} :rock-timer
    :keys [rocks booms score] :as scene}]
  (cond
    ;; if we're at 0, add rock & reset timer
    (= 0 countdown)
    (assoc scene
           :rocks (assoc-fn rocks :id rock)
           :rock-timer (update-timer timer score))
    ;; if we're at the flash time, add the flash obj
    (= (::boom/in boom/type->lifetime) countdown)
    (assoc scene
           :booms (assoc-fn booms :id (boom/obj->in rock))
           :rock-timer (dec-countdown timer))
    ;; otherwise add the decremented counter
    :else
    (assoc scene :rock-timer (dec-countdown timer))))
