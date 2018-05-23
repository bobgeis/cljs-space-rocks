(ns cljs-space-rocks.model
  "ns for game model manipulation fns"
  (:require
    [re-frame.core :as rf]
    [com.rpl.specter :as sp]
    [helper.log :refer [jlog clog]]
    [helper.rf :refer [spy]]
    [helper.browser :as hb]))

(defn init-player
  "initial player data"
  []
  {:x 400 :y 325
   :vx 0 :vy 0
   :a 0 :va 0
   :r 0})

(defn init-state
  "an initial state"
  []
  {:mode "splash"
   :games #queue []
   :hiscore {}
   :game {
           :player (init-player)
          ;  :rocks []
          ;  :shots []
          ;  :ships []
          ;  :loot []
          ;  :booms []
          ;  :bases []
          ;  :cargo {}
          ;  :score {}
          ;  :seed {}
          ;  :ship-timer 0
          ;  :rock-timer 0
           :ticks 0}})

(defn init-app-state
  "initialize the app state"
  [db ls-data]
  (clog "initializing app state")
  {:db (init-state)})

(defn change-mode
  "change the game mode"
  [db new-mode]
  (assoc db :mode new-mode))


(def splash-up-axns
  "map keyups to actions in splash mode"
  {" " :start
   "Escape" :clear-local-storage})

(def play-down-axns
  "map keydowns to actions in play mode"
  {" " :shoot-on
   "ArrowLeft" :turn-left-on
   "ArrowRight" :turn-right-on
   "ArrowUp" :thrust-on
   "ArrowDown" :reverse-on})

(def play-up-axns
  "map keyups to actions in play mode"
  {" " :shoot-off
   "ArrowLeft" :turn-left-off
   "ArrowRight" :turn-right-off
   "ArrowUp" :thrust-off
   "ArrowDown" :reverse-off
   "p" :pause})

(def pause-up-axns
  "map of keyup actions for pause"
  {"p" :unpause})

(def gameover-up-axns
  "map of keyup actions for gameover mode"
  {"Enter" :start})

(def up-axns-by-mode
  "map of key-up actions for each mode"
  {"splash" splash-up-axns
   "play" play-up-axns
   "pause" pause-up-axns
   "gameover" gameover-up-axns})
