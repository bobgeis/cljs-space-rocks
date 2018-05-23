(ns cljs-space-rocks.view
  "ns for view code"
  (:require
    [reagent.core :as r]
    [cljs-space-rocks.model :as mod]
    [helper.log :refer [jlog clog]]
    [helper.rf :as hr :refer [<sub >evt]]
    [helper.fun :as hf]
    [helper.browser :as hb]))

(defn svg-player
  "draw the player svg"
  []
  (let [player (<sub [:player])]
    (clog "svg player")
    (clog player)
    [:text {:x 400 :y 350} (str (:x player))]))

(defn svg-board
  "top svg"
  []
  (let [test "put top level subs here"]
    [:svg
      {:style {:width 800
               :height 650
               :background "url(img/stars.jpg) no-repeat center"
               :background-size "cover"}}
      (svg-player)]))

(defn main-view
  "main container view"
  []
  (svg-board))


(defn render-root
  "render the root of the view"
  [id]
  (r/render
    [main-view]
    (hb/get-element id)))
