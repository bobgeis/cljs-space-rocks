(ns cljs-space-rocks.view
  "ns for view code"
  (:require
   [reagent.core :as r]
   [clojure.string :as string]
   [helper.log :refer [clog]]
   [helper.rf :as hr :refer [<sub >evt]]
   [helper.fun :as hf :refer [sjoin]]
   [helper.browser :as hb]
   [cljs-space-rocks.model :as mod]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.text :as text]
   [cljs-space-rocks.obj.player :as player]
   [cljs-space-rocks.obj.base :as base]
   [cljs-space-rocks.obj.boom :as boom]
   [cljs-space-rocks.obj.bullet :as bullet]
   [cljs-space-rocks.obj.loot :as loot]
   [cljs-space-rocks.obj.particle :as particle]
   [cljs-space-rocks.obj.rock :as rock]))


;; text

(defn text-omega-count
  "text of the omega-13 count"
  []
  (let [count (<sub [:omega-seconds])
        full? (<sub [:omega-full?])]
    (text/omega-count count full?)))

(defn text-score
  []
  (let [score (<sub [:score])]
    (text/score score)))

(defn text-hiscore
  []
  (let [hiscore (<sub [:hiscore])]
    (text/hiscore hiscore)))

(defn text-cargo
  []
  (let [cargo (<sub [:cargo])]
    (text/cargo cargo)))

;; svgs

(defn svg-board-setting
  []
  (let [[w h] (<sub [:win-size])]
    {:style {:width w
             :height h
             :background "url(img/stars.jpg) no-repeat center"
             :background-size "cover"}
     :view-box (sjoin [0 0 misc/xt-box misc/yt-box])}))


(defn svg-objs
  "turn the map of objs into an svg group"
  [objs svg]
  (into [:g] (map svg) (vals objs)))

(defn svg-bases
  "draw the base svgs"
  []
  (let [bases (<sub [:bases])]
    (svg-objs bases base/svg)))

(defn svg-booms
  "draw the boom svgs"
  []
  (let [booms (<sub [:booms])]
    (svg-objs booms boom/svg)))

(defn svg-bullets
  "draw the bullet svgs"
  []
  (let [bullets (<sub [:bullets])]
    (svg-objs bullets bullet/svg)))

(defn svg-loot
  "draw the bullet svgs"
  []
  (let [objs (<sub [:loot])]
    (svg-objs objs loot/svg)))

(defn svg-particles
  "draw the rock svgs"
  []
  (let [particles (<sub [:particles])]
    (svg-objs particles particle/svg)))

(defn svg-rocks
  "draw the rock svgs"
  []
  (let [rocks (<sub [:rocks])]
    (svg-objs rocks rock/svg)))

(defn svg-omega-player
  "draw where the player will go if omega-13 is triggered"
  []
  (let [trigger (<sub [:omega-trigger])
        player (<sub [:omega-player])]
    (if trigger
      (player/svg-omega-player player (/ trigger misc/omega-13-countdown))
      [:g])))

(defn svg-omega-rocks
  "draw where the rocks will be if omega-13 is triggered"
  []
  (let [trigger (<sub [:omega-trigger])
        rocks (<sub [:omega-rocks])]
    (if trigger
      (svg-objs rocks rock/svg-omega)
      [:g])))

(defn svg-player
  "draw the player svg"
  []
  (let [player (<sub [:player])]
    (player/svg-player player)))

;; svgs by mode

(defn svg-board-play-mode
  "top svg"
  []
  [:svg
   (svg-board-setting)
   (svg-bases)
   (svg-loot)
   (svg-particles)
   (svg-bullets)
   (svg-rocks)
   (svg-player)
   (svg-omega-player)
   (svg-omega-rocks)
   (svg-booms)])

(defn svg-board-gameover-mode
  "top svg"
  []
  [:svg
   (svg-board-setting)
   (svg-bases)
   (svg-loot)
   (svg-particles)
   (svg-bullets)
   (svg-rocks)
   (svg-omega-player)
   (svg-omega-rocks)
   (svg-booms)])

;; views by mode

(defn view-play-mode
  []
  [:div
   (svg-board-play-mode)
   (text-cargo)
   (text-score)
   (text-omega-count)])

(defn view-gameover-mode
  []
  [:div
   (svg-board-gameover-mode)
   (text/descriptions :gameover)
   (text-score)
   (text-hiscore)
   (text-omega-count)])

(defn view-pause-mode
  []
  [:div
   (svg-board-play-mode)
   (text/descriptions :pause)
   (text-cargo)
   (text-score)
   (text-hiscore)
   (text-omega-count)])

(defn view-splash-mode
  []
  [:div
   (svg-board-play-mode)
   (text/descriptions :splash)
   (text/desc-gems)
  ;  (text/desc-pods)
   (text-omega-count)
  ;  (text-cargo)
  ;  (text-score)
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
