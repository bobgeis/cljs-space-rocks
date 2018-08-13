(ns helper.svg
  "some svg helper functions"
  (:require
   [helper.fun :refer [floor]]))


(defn css-transform
  "take an object or args and generate a transform string.
  x y are length units in the svg coordinate system.
  a is an angle in degrees.  s is a scaling factor"
  ([x y a]
   (str "translate(" (floor x) "," (floor y) ") rotate(" (floor a) ")"))
  ([{:keys [x y a]}]
   (css-transform x y a))
  ([x y]
   (str "translate(" (floor x) "," (floor y) ")"))
  ([x y a s]
   (str "translate(" (floor x) "," (floor y) ") rotate(" (floor a) ") scale(" s ")")))

(defn css-rotate
  [a]
  (str "rotate(" (floor a) ")"))
