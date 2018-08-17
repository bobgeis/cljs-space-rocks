(ns cljs-space-rocks.text
  "ns for text components"
  (:require
   [helper.color :refer [rgb hsl]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.misc :as misc]))


;; helpers

;; omega-13 related

(defn omega-count
  "display the seconds count of the omega-13"
  [count [w h] omega-left]
  [:p
   {:style {:color (cond
                     (> count 12) "#00FF99"
                     (> count 10) "#00FF55"
                     (> count 8) "#00FF00"
                     (> count 6) "#55FF00"
                     (> count 4) "#AAFF00"
                     (> count 2) "#FFFF00"
                     :else "#FF0000")
            :position "absolute" :font-size "15px"
            :top (- h 20) :left (/ w 2)
            :transform "translate(-50%,-100%)"}}
   (str "\u03A9-" count)
   (if omega-left
     [:span
      {:style {:color (hsl 150 100 75 0.75)}}
      (str " -> \u03A9-" omega-left)])])

;; description text appears in the splash/pause

(defn desc-omega
  "describe the omega-13"
  [[w h]]
  [:div
   {:style {:position "absolute"
            :top (- h 25) :left (/ w 2)
            :transform "translate(-50%,-100%)"
            :font-size "10px" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   "This is your Omega-13 charge.  Use it to go back in time.  When it's not red, hold the Z key!"])

(defn desc-pods
  "description of lifepod objective"
  [[w h]]
  [:div
   {:style {:position "absolute"
            :top (/ h 2) :left (- w 20)
            :width "150px"
            :transform "translate(-100%,-50%)"
            :font-size "10px" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:p "When a ship explodes, there may be life pods."]
   [:p "Bring any pods you catch to the hospital station in the upper right."]])

(defn desc-gems
  "description of the gem objective"
  [[w h]]
  [:div
   {:style {:position "absolute"
            :top (/ h 2) :left 30
            :width "150px"
            :transform "translate(0,-50%)"
            :font-size "10px" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:p "When a rock breaks, it may leave small gems."]
   [:p "Bring any gems you find to the refinery station in the lower left."]])

(defn desc-controls
  "how do we fly this thing?"
  [[w h]]
  [:div
   {:style {:position "absolute"
            :top (+ 25 (/ h 2)) :left (/ w 2)
            :transform "translate(-50%,0)"
            :font-size "10px" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:strong "Controls:"] [:br] [:br]
   [:strong "Arrow "] " keys to move" [:br]
   [:strong "Space"] " to fire" [:br]
   [:strong "Z"] " to use the \u03A9-13" [:br]
   [:strong "X"] " to abort the \u03A9-13" [:br]
   [:strong "P"] " to pause" [:br]
   [:strong "Enter"] " to play!" [:br]])

(defn desc-splash
  "splash game description"
  [[w h]]
  [:div
   {:style {:position "absolute"
            :top (- (/ h 2) 25) :left (/ w 2)
            :transform "translate(-50%,-100%)"
            :font-size "12px" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:p [:strong [:a {:href "https://github.com/bobgeis/cljs-space-rocks"
                     :style {:color "#FFFFFF"}}
                 "Lookout! Space Rocks!"]]]
   [:p "Some hooligans are dumping space rocks."]
   [:p "Luckily, you're here :)"]
   [:p "Bust the rocks so ships can travel safely"]
   [:p "Good luck!"]])

(defn desc-pause
  "description shown when paused"
  [[w h]]
  [:div
   {:style {:position "absolute"
            :top (- (/ h 2) 25) :left (/ w 2)
            :transform "translate(-50%,-100%)"
            :font-size "10px" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:strong "Paused"] [:br] [:br]
   "Objectives:" [:br]
   "Bust rocks" [:br]
   "Protect ships" [:br]
   "Collect gems" [:br]
   "Rescue pods"])

(defn desc-gameover
  "description for when the player has crashed"
  [[w h]]
  [:div
   {:style {:position "absolute"
            :top (- (/ h 2) 25) :left (/ w 2)
            :transform "translate(-50%,-100%)"
            :font-size "12px" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:p [:strong "Oh no! You exploded!"]]
   [:p "But if you have enough \u03A9 charge, then there is still hope."]
   [:p "Hold the 'Z' key to go back in time, and release it to jump to that moment!"]
   [:p "Otherwise, press 'Enter' to restart."]])

(def mode-desc
  {:pause desc-pause
   :gameover desc-gameover
   :go-pause desc-gameover
   :splash desc-splash})

(defn descriptions
  "game description component"
  [mode size]
  (let [desc-main (mode mode-desc)]
    [:div
     (desc-main size)
     (desc-controls size)]))

;; score related

(defn hiscore
  "hiscore"
  [{:keys [gem pod ship rock] :or {gem 0 pod 0 ship 0 rock 0}} [w h]]
  [:div
   {:style {:position "absolute"
            :top 10 :left 10
            :font-size "Small" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:p [:strong "High Scores"]]
   [:p (str "Most rocks busted:  " rock)]
   [:p (str "Most ships protected:  " ship)]
   [:p (str "Most gems delivered:  " gem)]
   [:p (str "Most pods rescued:  " pod)]])

(defn show-score
  "current score"
  [{:keys [gem pod ship rock]}]
  [:div
   {:style {:padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:strong "Score"] [:br]
   (str "Rocks:  " rock) [:br]
   (str "Ships:  " ship) [:br]
   (str "Gems:  " gem) [:br]
   (str "Pods:  " pod)])

(defn show-cargo
  "current ship cargo"
  [{:keys [gem pod]}]
  [:div
   {:style {:padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:strong "Cargo"] [:br]
   (str "Gems:  " gem) [:br]
   (str "Pods:  " pod)])

(defn cargo-score
  [cargo score [w h]]
  [:div
   {:style {:position "absolute"
            :top (- h 20) :left 10
            :font-size "10px"
            :transform "translate(0,-100%)"}}
   (show-cargo cargo)
   [:br]
   (show-score score)])
