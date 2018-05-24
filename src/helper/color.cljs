(ns helper.color
  "ns for some color functions"
  (:require
   [helper.fun :refer [sjoin]]))


(defn rgb
  "generate a rgb or rgba color string.
  r, g, & b should be a number 0-255
  a should be 0-1"
  ([r g b]
   (str "rgb( " r "," g "," b ")"))
  ([r g b a]
   (str "rgb( " r "," g "," b "," a ")")))

(defn hsl
  "generate a hsl or hsla color string.
  h is the hue in degrees;
  red = 0, yellow = 60, green = 120, cyan = 180, blue = 240, magenta = 300,
  and values > 360.0 wrap.
  s is saturation, should be a number 0.0 - 100.0.
  l is lightness, should be a number 0.0 - 100.0.
  a is alpha, should be a number 0.0 (fully transparent) - 1.0 (fully opaque)."
  ([h s l]
   (str "hsl( " h ", " s "%, " l "%)"))
  ([h s l a]
   (str "hsla( " h ", " s "%, " l "%, " a ")")))
