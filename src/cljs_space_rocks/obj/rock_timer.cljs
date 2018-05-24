(ns cljs-space-rocks.obj.rock-timer
  "ns for functions related to the spawning of new rocks"
  (:require
   [com.rpl.specter :as sp]
   [helper.geom :as geom :refer [cc-hit?]]
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq dissoc-fn]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.misc :as misc :refer [not-NONE]]
   [cljs-space-rocks.obj.boom :as boom]
   [cljs-space-rocks.obj.particle :as particle]
   [cljs-space-rocks.obj.rock :as rock]))

;; constants

(def min-roll
  "minimum number of ticks between spawns"
  45)

(def max-roll
  "maximum number of ticks between spawns"
  90)

(def num-rolls
  "number of rolls to get the next tick"
  6)

;; helpers

;; model

(defn update-timer
  "given a timer, calculate and assoc the next rock and flash"
  [{:keys [r-min r-max r-num seed] :as timer}]
  (let [set-seed! (drand/set-seed! seed)
        countdown (apply + (repeatedly r-num #(drand/dint r-max r-min)))
        rock (rock/make-spawn)
        new-seed (drand/drseed)]
    (assoc timer
           :countdown countdown
           :rock rock
           :seed new-seed)))

(defn create
  "create a new rock-timer"
  [seed r-min r-max r-num]
  (update-timer {:seed seed :r-min r-min :r-max r-max :r-num r-num}))

(defn init-timer
  "initialize the rock-timer"
  [] (create (drand/rrseed) min-roll max-roll num-rolls))

;; manipulation

(defn tick
  [{{:keys [countdown rock flash] :as timer} :rock-timer
    :keys [rocks flashes] :as scene}]
  (cond
    ;; if we're at 0, add rock & reset timer
    (= 0 countdown)
    (assoc scene
           :rocks (assoc-fn rocks :id rock)
           :rock-timer (update-timer timer))
    ;; if we're at the flash time, add the flash obj
    ;; otherwise add the decremented counter
    :else (update-in scene [:rock-timer :countdown] dec)))
