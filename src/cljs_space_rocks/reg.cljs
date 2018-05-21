(ns cljs-space-rocks.reg
  "ns for re-frame registrations"
  (:require
    [re-frame.core :as rf]
    [helper.browser :as hb]
    [helper.log :refer [jlog clog]]
    [cljs-space-rocks.model :as mod]))


;; reg cofx

(rf/reg-cofx :get-local-store
  (fn [cofx ls-key]
    (assoc cofx :get-local-store
      (hb/get-local-storage ls-key))))


;; reg fx

(rf/reg-fx :set-local-store
  (fn [[ls-key data]]
    (hb/set-local-storage ls-key data)))

(rf/reg-fx :clear-local-store
  (fn [ls-key]
    (hb/del-local-storage ls-key)))

(rf/reg-fx :play-sound
  (fn [id]
    (hb/play-audio id)))


;; reg event

(rf/reg-event-fx :init
  [(rf/inject-cofx :get-local-store "cljs-space-rocks")]
  (fn [cofx _]
    (mod/init-app-state (:db cofx) (:get-local-store cofx))))

(rf/reg-event-fx :tick
  (fn [cofx [_ dt]]
    {}))

(rf/reg-event-fx :key-down
  (fn [cofx [_ data]]
    {}))

(rf/reg-event-fx :key-up
  (fn [cofx [_ data]]
    {}))

;; reg sub
