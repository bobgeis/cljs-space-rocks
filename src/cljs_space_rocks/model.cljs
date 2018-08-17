(ns cljs-space-rocks.model
  "ns for game model manipulation fns"
  (:require
   [re-frame.core :as rf]
   [com.rpl.specter :as sp]
   [helper.log :refer [clog]]
   [helper.rf :refer [spy]]
   [helper.browser :as hb]
   [helper.fun :as fun :refer [assoc-fn floor]]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.omega :as omega]
   [cljs-space-rocks.obj :as obj]
   [cljs-space-rocks.obj.player :as player]
   [cljs-space-rocks.obj.base :as base]
   [cljs-space-rocks.obj.boom :as boom]
   [cljs-space-rocks.obj.bullet :as bullet]
   [cljs-space-rocks.obj.loot :as loot]
   [cljs-space-rocks.obj.particle :as particle]
   [cljs-space-rocks.obj.rock :as rock]
   [cljs-space-rocks.obj.ship :as ship]
   [cljs-space-rocks.obj.base-player :as baseplay]
   [cljs-space-rocks.obj.bullet-rock :as bullrock]
   [cljs-space-rocks.obj.loot-player :as lootplay]
   [cljs-space-rocks.obj.player-rock :as playrock]
   [cljs-space-rocks.obj.rock-ship :as rockship]
   [cljs-space-rocks.obj.rock-timer :as rock-timer]
   [cljs-space-rocks.obj.ship-timer :as ship-timer]))

;; constants and helpers

(def game-modes
  "list of game modes"
  [:splash
   :play
   :gameover
   :pause
   :go-pause])

;; model

(defn init-state
  "an initial state"
  ([]
   {:mode :splash
    :omega omega/initial-omega
    :hiscore {}
    :save-exists nil
    :win-size [(/ misc/xt-box 10) (/ misc/yt-box 10)]
    :scene {:player (player/initial-player) ;; player is a map of player-ship data
           ;; bases through ships are maps of multiple of that type of object
            ;; their :id kws are used as kw of the map,
            ;; eg: :rock {5 {:id 5 ...} ...}
            :bases (base/initial-bases)
            :booms (boom/initial-booms)
            :bullets {}
            :flashes {}
            :loot {}
            :particles {}
            :rocks (rock/initial-rocks)
            :ships (ship/initial-ships)
           ;; cargo and score represent loot delivery and traveler safety
            :cargo {:gem 0 :pod 0}
            :score {:rock 0 :gem 0 :pod 0 :ship 0}
           ;; tick is the number of ticks this scene has progressed for
            :tick 0
            :rock-timer (rock-timer/init-timer)
            :ship-timer (ship-timer/init-timer)
           ;; effects are changes in the scene that affect the larger game state
            :effects {}}})
  ([{hiscore :hiscore win-size :win-size save-exists :save-exists}]
   (assoc (init-state)
          :hiscore hiscore
          :win-size win-size
          :save-exists (if save-exists :true))))

(defn init-app-state
  "initialize the app state"
  [db ls-score size save-exists]
  {:db (init-state {:hiscore ls-score :win-size size :save-exists save-exists})})

(defn sync-local-score
  "update the local-store's hiscore"
  [{hiscore :hiscore} ls-score]
  {:set-local-store [misc/ls-score-key (merge-with max ls-score hiscore)]})

;; query

(defn get-db-mode
  "get the current game mode (eg :play)"
  [db]
  (:mode db))

(defn get-db-score
  "get the score of the current active scene"
  [db]
  (sp/select [:scene :score] db))

;; manipulation

(defn tick-objs
  "tick all objects in an objects map"
  [objs tick]
  (sp/transform [sp/MAP-VALS] tick objs))

(defn player-update
  "update the player in the scene map"
  [scene]
  (update scene :player obj/tick))

(defn player-neutral
  "set the player to neutral in the scene map (no acc/turn/firing)"
  [scene]
  (update scene :player player/set-neutral))

(defn self-updates
  "apply physics, lifetime, etc"
  [{{reload :reload firing :firing :as player} :player :as scene}]
  (assoc scene
         :bases (obj/tick-all (:bases scene))
         :bullets (cond-> (:bullets scene)
                    true (obj/tick-all)
                    (player/fire? player) (assoc-fn :id (bullet/create player)))
         :booms (obj/tick-all (:booms scene))
         :loot (obj/tick-all (:loot scene))
         :particles (obj/tick-all (:particles scene))
         :rocks (obj/tick-all (:rocks scene))
         :ships (obj/tick-all (:ships scene))))

(defn inc-tick
  "increment the tick counter"
  [{:keys [tick] :as scene}]
  (update scene :tick inc))

(defn merge-scores
  "merge the scene's score into the db's high score"
  [{{score :score} :scene hiscore :hiscore :as db}]
  (assoc db :hiscore (merge-with max hiscore score)))

(defn clear-scene-effects
  "clear the scene effects map"
  [scene]
  (assoc scene :scene-effects {}))

(defn change-mode
  "change the game mode"
  [db new-mode]
  (assoc db :mode new-mode))

(defn turn-player
  "start the player turning left (-1), right, or stop"
  [db sign]
  (update-in db [:scene :player] #(player/turn % sign)))

(defn acc-player
  "accelerate player with thrust or retro or stop"
  [db sign]
  (update-in db [:scene :player] #(player/acc % sign)))

(defn firing-player
  "change the firing state"
  [db firing]
  (update-in db [:scene :player] #(player/set-firing % firing)))

;; update functions

(defn tick-play-scene
  "tick a scene map, producing a new scene map"
  [scene]
  (-> scene
      (inc-tick)
      (self-updates)
      (player-update)
      (baseplay/interact)
      (bullrock/interact)
      (lootplay/interact)
      (playrock/interact)
      (rockship/interact)
      (rock-timer/tick)
      (ship-timer/tick)))

(defn tick-gameover-scene
  "tick the scene in gameover mode"
  [scene]
  (-> scene
      (self-updates)
      (bullrock/interact)
      (rockship/interact)
      (rock-timer/tick)
      (ship-timer/tick)))

(defn maybe-game-over
  "handle possible play->gameover"
  [{scene :scene :as db}]
  (if (and (= :play (:mode db)) (not (:player scene)))
    (do
      (rf/dispatch [:sync-local-score (:hiscore db)])
      (assoc db :mode :gameover))
    db))

(defn trim-queue
  "trim a queue in linear time to length n"
  [q n]
  (if (> (count q) n)
    (recur (pop q) n)
    q))

(defn tick-db-play
  "handle play ticks on db."
  [db]
  (-> db
      (update :scene tick-play-scene)
      (merge-scores)
      (maybe-game-over)
      (omega/tick-timeline-db)
      (omega/tick-or-trigger-db)))

(defn tick-db-gameover
  "handle gameover ticks on db.
  should tick things that can act without the player."
  [db]
  (-> db
      (update :scene tick-gameover-scene)
      (omega/tick-or-trigger-db)))

(defn tick-db-splash
  "handle splash ticks on db (no ops for now)"
  [db]
  (omega/tick-or-trigger-db db))

(defn tick-db-pause
  "handle pause ticks on db (no ops for now)"
  [db]
  (omega/tick-or-trigger-db db))

(defn tick-db-go-pause
  "handle go-pause ticks on db (no ops for now)"
  [db]
  (omega/tick-or-trigger-db db))

(def mode->tick-fn
  "update function for each game mode"
  {:play tick-db-play
   :pause tick-db-pause
   :splash tick-db-splash
   :gameover tick-db-gameover
   :go-pause tick-db-go-pause})

(defn tick-app-state
  "tick the app state"
  [{{mode :mode :as db} :db :as cofx}]
  (let [tick-fn (mode mode->tick-fn)
        db' (tick-fn db)]
    {:db db'}))

;; cofx fns: these take a cofx map and return an fx map

(defn init-state-cofx
  [{db :db}]
  {:db (init-state db)
   :set-local-store [misc/ls-score-key (:hiscore db)]})

(defn no-op
  "given cofx, return {:db db}"
  [{db :db :as cofx}]
  {:db db})

(defn clog-cofx
  [{db :db :as cofx}]
  (clog db)
  {:db db})

(defn turn-player-cofx
  [{db :db} sign]
  {:db (turn-player db sign)})

(defn acc-player-cofx
  [{db :db} sign]
  {:db (acc-player db sign)})

(defn firing-player-cofx
  [{db :db} val]
  {:db (firing-player db val)})

(defn change-mode-cofx
  [{db :db} mode]
  {:db (change-mode db mode)})

(defn omega-on-cofx
  "try to turn on the omega-13 device"
  [{db :db}]
  {:db (omega/start-db db)})

(defn omega-trigger-cofx
  "set the trigger for the omega-13 device"
  [{db :db}]
  {:db (omega/set-trigger-db db)})

(defn omega-off-cofx
  "turn off the omega-13 device"
  [{db :db}]
  {:db (omega/stop-db db)})

(defn wipe-hiscores-cofx
  [{db :db}]
  {:db (dissoc db :hiscore)
   :clear-local-store misc/ls-score-key})

(defn set-window-size-cofx
  [{db :db size :get-win-size :as cofx}]
  {:db (assoc db :win-size size)})

;; keymaps

(def splash-down-axns
  {"Enter" #(change-mode-cofx % :play)
   "Space" #(change-mode-cofx % :play)
   "Escape" init-state-cofx
   "u" wipe-hiscores-cofx
   "l" clog-cofx})

(def splash-up-axns
  "map keyups to actions in splash mode"
  {})

(def play-down-axns
  "map keydowns to actions in play mode"
  {"ArrowLeft" #(turn-player-cofx % -1)
   "ArrowRight" #(turn-player-cofx % 1)
   "ArrowUp" #(acc-player-cofx % 1)
   "ArrowDown" #(acc-player-cofx % -1)
   "a" #(turn-player-cofx % -1)
   "d" #(turn-player-cofx % 1)
   "w" #(acc-player-cofx % 1)
   "s" #(acc-player-cofx % -1)
   " " #(firing-player-cofx % true)
   "Shift" #(firing-player-cofx % true)
   "p" #(change-mode-cofx % :pause)
   "Enter" #(change-mode-cofx % :pause)
   "z" #(omega-on-cofx %)
   "x" #(omega-off-cofx %)
   "u" wipe-hiscores-cofx
   "l" clog-cofx})

(def play-up-axns
  "map keyups to actions in play mode"
  {"ArrowLeft" #(turn-player-cofx % 0)
   "ArrowRight" #(turn-player-cofx % 0)
   "ArrowUp" #(acc-player-cofx % 0)
   "ArrowDown" #(acc-player-cofx % 0)
   "a" #(turn-player-cofx % 0)
   "d" #(turn-player-cofx % 0)
   "w" #(acc-player-cofx % 0)
   "s" #(acc-player-cofx % 0)
   " " #(firing-player-cofx % false)
   "Shift" #(firing-player-cofx % false)
   "z" #(omega-trigger-cofx %)})

(def pause-down-axns
  {"ArrowLeft" #(turn-player-cofx % -1)
   "ArrowRight" #(turn-player-cofx % 1)
   "ArrowUp" #(acc-player-cofx % 1)
   "ArrowDown" #(acc-player-cofx % -1)
   "a" #(turn-player-cofx % -1)
   "d" #(turn-player-cofx % 1)
   "w" #(acc-player-cofx % 1)
   "s" #(acc-player-cofx % -1)
   " " #(firing-player-cofx % true)
   "Shift" #(firing-player-cofx % true)
   "p" #(change-mode-cofx % :play)
   "Enter" #(change-mode-cofx % :play)
   "z" #(omega-on-cofx %)
   "x" #(omega-off-cofx %)
   "Escape" init-state-cofx
   "u" wipe-hiscores-cofx
   "l" clog-cofx})

(def pause-up-axns
  "map of keyup actions for pause"
  {"ArrowLeft" #(turn-player-cofx % 0)
   "ArrowRight" #(turn-player-cofx % 0)
   "ArrowUp" #(acc-player-cofx % 0)
   "ArrowDown" #(acc-player-cofx % 0)
   "a" #(turn-player-cofx % 0)
   "d" #(turn-player-cofx % 0)
   "w" #(acc-player-cofx % 0)
   "s" #(acc-player-cofx % 0)
   " " #(firing-player-cofx % false)
   "Shift" #(firing-player-cofx % false)
   "z" #(omega-trigger-cofx %)})

(def gameover-down-axns
  {"p" #(change-mode-cofx % :go-pause)
   "z" #(omega-on-cofx %)
   "x" #(omega-off-cofx %)
   "Enter" init-state-cofx
   "Escape" init-state-cofx
   "u" wipe-hiscores-cofx
   "l" clog-cofx})

(def gameover-up-axns
  "map of keyup actions for gameover mode"
  {"z" #(omega-trigger-cofx %)})

(def go-pause-down-axns
  {"p" #(change-mode-cofx % :gameover)
   "z" #(omega-on-cofx %)
   "x" #(omega-off-cofx %)
   "Enter" init-state-cofx
   "Escape" init-state-cofx
   "u" wipe-hiscores-cofx
   "l" clog-cofx})

(def go-pause-up-axns
  "map of keyup actions for gameover mode"
  {"z" #(omega-trigger-cofx %)})

(def down-axns-by-mode
  "map of key-down actions for each mode"
  {:splash splash-down-axns
   :play play-down-axns
   :pause pause-down-axns
   :gameover gameover-down-axns
   :go-pause go-pause-down-axns})

(def up-axns-by-mode
  "map of key-up actions for each mode"
  {:splash splash-up-axns
   :play play-up-axns
   :pause pause-up-axns
   :gameover gameover-up-axns
   :go-pause go-pause-up-axns})

(def all-axns
  "map of all actions"
  {:key-down down-axns-by-mode
   :key-up up-axns-by-mode})

(defn handle-key-change
  "handle key ups and downs"
  [{db :db :as cofx} kw {:keys [key shift alt] :as data}]
  (let [axn (get-in all-axns [kw (:mode db) key] no-op)]
    (axn cofx)))

