(ns cljs-space-rocks.input
  "ns for user input handling"
  (:require
   [re-frame.core :as rf]
   [helper.log :as hl :refer [clog]]
   [helper.browser :as hb]
   [cljs-space-rocks.misc :as misc]))


(defn disp-key-handler
  "dispatch a key event"
  [e kw]
  (if-not e.repeat (rf/dispatch [kw {:key e.key :shift e.shiftKey :alt e.altKey}])))

(def timeout-atom (atom nil))

(defn handle-resize
  "handle the resize"
  [e]
  (js/clearTimeout @timeout-atom)
  (reset! timeout-atom
          (js/setTimeout
           (fn []
             (rf/dispatch [:resize])
             (reset! timeout-atom nil))
           200)))

(defn add-top-listeners
  "add the listeners important for the app"
  [id]
  (hb/add-listener js/document "keydown" #(disp-key-handler % :key-down))
  (hb/add-listener js/document "keyup" #(disp-key-handler % :key-up))
  (hb/add-listener js/window "resize" handle-resize))
