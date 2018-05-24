(ns helper.browser
  "some browser functions"
  (:require
   [helper.log :refer [clog]]
   [helper.transit :as ht]))


(defn get-element
  "get an element by id"
  [id]
  (js/document.getElementById id))

(defn add-listener
  "add an event listener to an element"
  [ele event handler]
  (.addEventListener ele event handler))

;; request animation frame

(def raf
  "request animation frame
  https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame
  - takes a callback to call on the next animation frame
  the callback receives a timestamp as the arg
  (ideally: 60 fps or every ~16ms)
  note that the callback will also need to call raf if you want it called again
  - raf returns a frame id that can be used to abort using
  window.cancelAnimationFrame()"
  js/requestAnimationFrame)

(def raf-clock
  "last raf time stamp (ms)"
  (atom 0))

(def raf-token
  "last raf stop token"
  (atom nil))

(defn wrap-raf-cb
  "wrap the raf callback"
  [callback]
  (letfn
   [(raf-loop [timestamp]
      (let [dt (- timestamp @raf-clock)]
        (reset! raf-clock timestamp)
        (callback dt)
        (reset! raf-token (raf raf-loop))))]
    raf-loop))

(defn raf-dt
  "like raf but gives dt (delta time since the last call) to the callback"
  [callback]
  ((wrap-raf-cb callback) 0))

(defn stop-raf
  "calls cancelAnimationFrame on the animation id in @raf-token if there is one"
  []
  (let [id @raf-token]
    (js/cancelAnimationFrame id)))


;; local storage

(defn set-local-storage
  "set something in the local storage"
  [name value]
  (.setItem js/localStorage name (ht/write value)))

(defn get-local-storage
  "get something from local storage"
  ([name]
   (ht/read (.getItem js/localStorage name)))
  ([name dflt]
   (or (get-local-storage name) dflt)))

(defn del-local-storage
  "remove something from local storage"
  [name]
  (.removeItem js/localStorage name))


;; sound

(defn play-audio
  "play an audio file"
  [file]
  (.play (js/Audio. file)))

(defn play-audio-el
  "play an audio element"
  [id]
  (.play (get-element id)))
