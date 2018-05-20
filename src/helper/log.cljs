(ns helper.log
  "ns for logging helpers"
  (:require
    [cljs.pprint :as pp :refer [pprint]]))


(defn clog
  "pprint arg to console transparently"
  ([arg]
   (pprint arg)
   arg)
  ([arg & rest]
   (apply clog arg rest)))

(defn jlog
  "console.log arg transparently"
  ([arg]
   (js/console.log arg)
   arg)
  ([arg & rest]
   (apply jlog arg rest)))

