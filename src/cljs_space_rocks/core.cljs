(ns cljs-space-rocks.core
    (:require
      [reagent.core :as reagent :refer [atom]]
      [re-frame.core :as rf]
      [helper.log :refer [jlog clog]]
      [cljs-space-rocks.model :as mod]
      [cljs-space-rocks.reg :as reg]
      [cljs-space-rocks.view :as view]
      [cljs-space-rocks.input :as input]))

(enable-console-print!)

(def root-div
  "name of the app's root element"
  "cljs-space-rocks")

(defn main-loop
  "dispatch tick every animation frame"
  [dt]
  (rf/dispatch [:tick dt]))

(defonce begin!
  (do
    (clog "begin!")
    (rf/dispatch-sync [:init])
    (view/render-root root-div)
    (input/add-top-listeners root-div)))
    ; (raf-dt main-loop)))

