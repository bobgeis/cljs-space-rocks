(ns helper.log
  "ns for logging helpers"
  (:require
   [cljs.pprint :as pp :refer [pprint]]))

(enable-console-print!)

(defn clog
  "print one arg to console transparently"
  [arg]
  (js/console.log arg)
  arg)
