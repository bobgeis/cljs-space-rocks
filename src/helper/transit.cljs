(ns helper.transit
  "ns to try out and use transit"
  (:require
   [cognitect.transit :as tr]))

(def reader (tr/reader :json-verbose))
(def writer (tr/writer :json-verbose))

(defn read
  "turn transit string back into cljs data"
  [string]
  (tr/read reader string))

(defn write
  "serialize cljs data into a transit string"
  [data]
  (tr/write writer data))

