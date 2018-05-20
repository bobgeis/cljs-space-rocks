(ns cljs-space-rocks.core
    (:require
      [reagent.core :as reagent :refer [atom]]
      [re-frame.core :as rf]
      [cljs-space-rocks.model :as mod]
      [cljs-space-rocks.reg :as reg]
      [cljs-space-rocks.view :as view]
      [cljs-space-rocks.input :as input]))

(enable-console-print!)

(defn main-loop
  "dispatch tick every animation frame"
  [dt]
  (rf/dispatch [:tick dt]))

(defonce begin!
  (do
  ; (rf/dispatch-sync [:init])
    (view/render-root)
    (input/add-top-listeners)))
    ; (raf-dt main-loop)))

