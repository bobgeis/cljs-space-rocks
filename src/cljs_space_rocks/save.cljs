(ns cljs-space-rocks.save
  "ns for code related to saving game scenes into local storage"
  (:require
   [re-frame.core :as rf]
   [helper.browser :as hb]
   [helper.color :refer [rgb hsl]]
   [helper.rf :as hrf :refer [>evt <sub]]
   [cljs-space-rocks.omega :as omega]))


;; constants

(def ls-key "cljs-space-rocks-scene")

;; functions

(defn get-scene "get the save from the local store" []
  (hb/get-local-storage ls-key nil))

(defn set-scene "set the save to the local store" [scene]
  (hb/set-local-storage ls-key scene))

(defn del-scene "delete the save" []
  (hb/del-local-storage ls-key))

;; cofx

(defn save-cofx [{{scene :scene :as db} :db :as cofx} _]
  {:db (assoc db :save-exists :true)
   :save-scene scene})

(defn load-cofx [{db :db scene :load-scene} _]
  (if-not scene db
          {:db (assoc db :scene scene :omega omega/initial-omega)}))

(defn clear-cofx [{db :db} _]
  {:db db
   :delete-save :true})

;; reg-cofx

(rf/reg-cofx
 :load-scene
 (fn [cofx]
   (assoc cofx :load-scene (get-scene))))

;; reg-fx

(rf/reg-fx
 :save-scene
 (fn [scene]
   (set-scene scene)))

(rf/reg-fx
 :delete-save
 (fn [_]
   (del-scene)))

;; reg-event

(rf/reg-event-fx
 :save-scene
 save-cofx)

(rf/reg-event-fx
 :load-scene
 [(rf/inject-cofx :load-scene)]
 load-cofx)

(rf/reg-event-fx
 :delete-save
 clear-cofx)

;; reg-sub

(rf/reg-sub
 :save-exists
 (fn [{save-exists :save-exists} _] save-exists))

;; view

(def button-style
  {:background-color (rgb 255 0 0)})

(def load-button
  [:input
   {:type "button"
    :value "Load Saved Game"
    :style {:background-color (rgb 0 75 0)
            :color (rgb 255 255 255)
            :border-radius "10px" :padding 5}
    :on-click (>evt [:load-scene])}])

(def save-button
  [:input
   {:type "button"
    :value "Save Current Game"
    :style {:background-color (rgb 0 0 75)
            :color (rgb 255 255 255)
            :border-radius "10px" :padding 5}
    :on-click (>evt [:save-scene])}])

(def clear-button
  [:input
   {:type "button"
    :value "Clear Saved Game"
    :style {:background-color (rgb 75 0 0)
            :color (rgb 255 255 255)
            :border-radius "10px" :padding 5}
    :on-click (>evt [:delete-save])}])

(def load-and-clear-button
  [:div
   load-button
   [:br]
   [:br]
   [:br]
   clear-button])

(defn button-panel [button [w h]]
  [:div
   {:style {:position "absolute"
            ; :top (- h 25) :left (/ w 2)
            :top (- h 25) :left (- w 25)
            ; :width "350px"
            :transform "translate(-100%,-100%)"
            :font-size "10px" :padding 10
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   button])

