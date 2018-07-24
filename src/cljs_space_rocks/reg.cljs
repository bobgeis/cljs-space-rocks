(ns cljs-space-rocks.reg
  "ns for re-frame registrations"
  (:require
   [re-frame.core :as rf]
   [helper.browser :as hb]
   [helper.log :refer [clog]]
   [cljs-space-rocks.model :as mod]
   [cljs-space-rocks.misc :as misc]))


;; reg cofx

(rf/reg-cofx
 :get-local-store
 (fn [cofx ls-key]
   (assoc cofx :get-local-store
          (hb/get-local-storage ls-key))))

(rf/reg-cofx
 :get-win-size
 (fn [cofx]
   (assoc cofx :get-win-size
          (misc/choose-size js/window.innerWidth js/window.innerHeight))))


;; reg fx

(rf/reg-fx
 :set-local-store
 (fn [[ls-key data]]
   (hb/set-local-storage ls-key data)))

(rf/reg-fx
 :clear-local-store
 (fn [ls-key]
   (hb/del-local-storage ls-key)))

(rf/reg-fx
 :play-sound
 (fn [id]
   (hb/play-audio id)))

(rf/reg-fx
 :log-time
 (fn [msg]
   (js/console.log (js/Date.now) msg)))

;; reg event

(rf/reg-event-fx
 :init
 [(rf/inject-cofx :get-local-store "cljs-space-rocks")
  (rf/inject-cofx :get-win-size)]
 (fn [cofx _]
   (mod/init-app-state (:db cofx) (:get-local-store cofx) (:get-win-size cofx))))

(rf/reg-event-fx
 :sync-local-score
 [(rf/inject-cofx :get-local-store "cljs-space-rocks")]
 (fn [cofx _]
   (mod/sync-local-score (:db cofx) (:get-local-store cofx))))

(rf/reg-event-fx
 :wipe-hiscores
 (fn [cofx _]
   (mod/wipe-hiscores-cofx cofx)))

(rf/reg-event-fx
 :tick
 (fn [cofx [_ dt]]
   (mod/tick-app-state cofx)))

(rf/reg-event-fx
 :key-down
 (fn [cofx [kw data]]
   (mod/handle-key-change cofx kw data)))

(rf/reg-event-fx
 :key-up
 (fn [cofx [kw data]]
   (mod/handle-key-change cofx kw data)))

(rf/reg-event-fx
 :resize
 [(rf/inject-cofx :get-win-size)]
 (fn [cofx _]
   (mod/set-window-size-cofx cofx)))

(rf/reg-event-fx
 :log-time
 (fn [{db :db} [_ msg]]
   {:log-time msg
    :db db}))


;; reg sub

(rf/reg-sub
 :win-size
 (fn [db _] (:win-size db)))

(rf/reg-sub
 :mode
 (fn [db _] (:mode db)))

(rf/reg-sub
 :player
 (fn [db _] (get-in db [:scene :player])))

(rf/reg-sub
 :bases
 (fn [db _] (get-in db [:scene :bases])))

(rf/reg-sub
 :booms
 (fn [db _] (get-in db [:scene :booms])))

(rf/reg-sub
 :bullets
 (fn [db _] (get-in db [:scene :bullets])))

(rf/reg-sub
 :loot
 (fn [db _] (get-in db [:scene :loot])))

(rf/reg-sub
 :particles
 (fn [db _] (get-in db [:scene :particles])))

(rf/reg-sub
 :rocks
 (fn [db _] (get-in db [:scene :rocks])))

;; score related subs

(rf/reg-sub
 :score
 (fn [db _] (get-in db [:scene :score])))

(rf/reg-sub
 :cargo
 (fn [db _] (get-in db [:scene :cargo])))

(rf/reg-sub
 :hiscore
 (fn [db _] (get-in db [:hiscore])))

;; omega related subs

(rf/reg-sub
 :omega-trigger
 (fn [db _] (:omega-trigger db)))

(rf/reg-sub
 :omega-player
 (fn [db _] (mod/get-db-omega-player db)))

(rf/reg-sub
 :omega-rocks
 (fn [db _] (mod/get-db-omega-rocks db)))

(rf/reg-sub
 :omega-seconds
 (fn [db _] (mod/get-db-omega-queue-seconds db)))

(rf/reg-sub
 :omega-full?
 (fn [db _] (mod/get-db-omega-queue-full? db)))
