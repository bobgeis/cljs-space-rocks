(ns helper.svg
  "some svg helper functions")


(defn css-transform
  "take an object and generate a transform string"
  ([{:keys [x y a]}]
   (str "translate( " x " " y " ) rotate( " a " )"))
  ([x y a]
   (str "translate( " x " " y " ) rotate( " a " )")))

(defn css-rotate
  [a]
  (str "rotate(" a ")"))
