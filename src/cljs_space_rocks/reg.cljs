(ns cljs-space-rocks.reg
  "ns for re-frame registrations"
  (:require
   [re-frame.core :as rf]
   [helper.browser :as hb]
   [helper.rf :as hrf]
   [helper.log :refer [clog]]
   [cljs-space-rocks.model :as mod]
   [cljs-space-rocks.omega :as omega]
   [cljs-space-rocks.save :as save]
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
          (misc/choose-svg-size js/window.innerWidth js/window.innerHeight))))


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
 [(rf/inject-cofx :get-local-store misc/ls-score-key)
  (rf/inject-cofx :get-win-size)
  (rf/inject-cofx :load-scene)]
 (fn [cofx _]
   (mod/init-app-state (:db cofx) (:get-local-store cofx) (:get-win-size cofx) (:load-scene cofx))))

(rf/reg-event-fx
 :sync-local-score
 [(rf/inject-cofx :get-local-store misc/ls-score-key)]
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

;; basic subs

(hrf/basic-sub :win-size [:win-size])
(hrf/basic-sub :mode [:mode])
(hrf/basic-sub :hiscore [:hiscore])

(hrf/basic-sub :player [:scene :player])
(hrf/basic-sub :score [:scene :score])
(hrf/basic-sub :cargo [:scene :cargo])

(hrf/basic-sub :bases [:scene :bases])
(hrf/basic-sub :booms [:scene :booms])
(hrf/basic-sub :bullets [:scene :bullets])
(hrf/basic-sub :loot [:scene :loot])
(hrf/basic-sub :particles [:scene :particles])
(hrf/basic-sub :rocks [:scene :rocks])
(hrf/basic-sub :ships [:scene :ships])

;; omega related subs

(rf/reg-sub
 :omega-seconds
 (fn [db _] (min 13 (omega/timeline-seconds-db db))))

(rf/reg-sub
 :omega-seconds-left
 (fn [db _] (omega/current-second (get-in db [:omega :current]))))

(rf/reg-sub
 :omega-scene
 (fn [db _] (omega/current-scene-db db)))

(rf/reg-sub
 :omega-player
 (fn [db _] (:player (omega/current-scene-db db))))

(rf/reg-sub
 :omega-rocks
 (fn [db _] (:player (omega/current-scene-db db))))
