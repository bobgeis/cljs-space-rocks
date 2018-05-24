(ns cljs-space-rocks.input
  "ns for user input handling"
  (:require
   [re-frame.core :as rf]
   [helper.log :as hl :refer [clog]]
   [helper.browser :as hb]))


(defn disp-key-handler
  "dispatch a key event"
  [e kw]
  (if-not e.repeat (rf/dispatch [kw {:key e.key :shift e.shiftKey :alt e.altKey}])))

(defn add-top-listeners
  "add the listeners important for the app"
  [id]
  (hb/add-listener js/document "keydown" #(disp-key-handler % :key-down))
  (hb/add-listener js/document "keyup" #(disp-key-handler % :key-up)))
