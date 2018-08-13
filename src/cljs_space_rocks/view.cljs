(ns cljs-space-rocks.view
  "ns for view code"
  (:require
   [reagent.core :as r]
   [clojure.string :as string]
   [helper.log :refer [clog]]
   [helper.rf :as hr :refer [<sub >evt spy]]
   [helper.fun :as hf :refer [sjoin]]
   [helper.browser :as hb]
   [cljs-space-rocks.model :as mod]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.text :as text]
   [cljs-space-rocks.obj :as obj]
   [cljs-space-rocks.obj.player :as player]
   [cljs-space-rocks.obj.bullet :as bullet]
   [cljs-space-rocks.obj.loot :as loot]
   [cljs-space-rocks.obj.particle :as particle]
   [cljs-space-rocks.obj.rock :as rock]))


;; text

(defn text-omega-count
  "text of the omega-13 count"
  []
  (let [count (<sub [:omega-seconds])
        size (<sub [:win-size])
        omega-left (<sub [:omega-seconds-left])]
    (text/omega-count count size omega-left)))

(defn text-hiscore
  []
  (let [hiscore (<sub [:hiscore])
        size (<sub [:win-size])]
    (text/hiscore hiscore size)))

(defn text-cargo-score
  []
  (let [cargo (<sub [:cargo])
        score (<sub [:score])
        size (<sub [:win-size])]
    (text/cargo-score cargo score size)))

(defn text-descriptions
  [mode]
  (let [size (<sub [:win-size])]
    (text/descriptions mode size)))

(defn text-gems
  []
  (let [size (<sub [:win-size])]
    (text/desc-gems size)))

(defn text-pods
  []
  (let [size (<sub [:win-size])]
    (text/desc-pods size)))

(defn test-omega-desc
  []
  (let [size (<sub [:win-size])]
    (text/desc-omega size)))

;; svgs

(defn svg-board-settings
  []
  (let [[w h] (<sub [:win-size])]
    {:style {:width w
             :height h
             :background "url(img/stars.jpg) no-repeat center"
             :background-size "cover"}
     :view-box (sjoin [0 0 misc/xt-box misc/yt-box])}))

(defn svg-omega-player
  []
  (let [scene (<sub [:omega-scene])]
    (if-not scene
      [:g]
      (player/svg-omega-player (:player scene)))))

(defn svg-omega-rocks
  []
  (let [objs (:rocks (<sub [:omega-scene]))]
    (if-not objs [:g]
            (into [:g] (map rock/svg-omega) (vals objs)))))

(defn svg-player
  "draw the player svg"
  []
  (let [player (<sub [:player])]
    (obj/svg player)))

;; svgs by mode

(defn svg-board-play-mode
  "top svg"
  []
  [:svg
   (svg-board-settings)
   (obj/sub->svgs :bases)
   (obj/sub->svgs :loot)
   (obj/sub->svgs :ships)
   (obj/sub->svgs :particles)
   (obj/sub->svgs :bullets)
   (obj/sub->svgs :rocks)
   (svg-player)
   (svg-omega-player)
   (svg-omega-rocks)
   (obj/sub->svgs :booms)])

(defn svg-board-gameover-mode
  "top svg"
  []
  [:svg
   (svg-board-settings)
   (obj/sub->svgs :bases)
   (obj/sub->svgs :loot)
   (obj/sub->svgs :ships)
   (obj/sub->svgs :particles)
   (obj/sub->svgs :bullets)
   (obj/sub->svgs :rocks)
   (svg-omega-player)
   (svg-omega-rocks)
   (obj/sub->svgs :booms)])

;; views by mode

(defn view-play-mode
  []
  [:div
   (svg-board-play-mode)
   (text-cargo-score)
   (text-omega-count)])

(defn view-gameover-mode
  []
  [:div
   (svg-board-gameover-mode)
   (text-descriptions :gameover)
   (text-cargo-score)
   (text-hiscore)
   (text-omega-count)])

(defn view-pause-mode
  []
  [:div
   (svg-board-play-mode)
   (text-descriptions :pause)
   (text-cargo-score)
   (text-hiscore)
   (text-omega-count)])

(defn view-splash-mode
  []
  [:div
   (svg-board-play-mode)
   (text-descriptions :splash)
   (text-gems)
   (text-pods)
   (text-omega-count)
   (test-omega-desc)
   (text-cargo-score)
   (text-hiscore)])

;; mode->view map

(def mode->svg-board
  {:play view-play-mode
   :pause view-pause-mode
   :splash view-splash-mode
   :gameover view-gameover-mode
   :go-pause view-gameover-mode})

;; main & root view

(defn main-view
  "main container view"
  []
  (let [mode (<sub [:mode])]
    ((mode mode->svg-board))))

(defn render-root
  "render the root of the view"
  [id]
  (r/render
   [main-view]
   (hb/get-element id)))
