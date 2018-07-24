(ns helper.svg
  "some svg helper functions"
  (:require
   [helper.fun :refer [floor]]))


(defn css-transform
  "take an object and generate a transform string"
  ([{:keys [x y a]}]
   (str "translate( " (floor x) " " (floor y) " ) rotate( " (floor a) " )"))
  ([x y a]
   (str "translate( " (floor x) " " (floor y) " ) rotate( " (floor a) " )")))

(defn css-rotate
  [a]
  (str "rotate(" (floor a) ")"))
