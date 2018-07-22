(ns cljs-space-rocks.text
  "ns for text components"
  (:require
   [helper.color :refer [rgb hsl]]
   [cljs-space-rocks.misc :as misc]))


;; omega-13 related

(defn omega-count
  "display the seconds count of the omega-13"
  [count full?]
  [:p
   {:style {:color (if full? "#00FF00" "#FF0000")
            :z-index 2 :position "absolute" :font-size "large"
            :top (- misc/game-height 50) :left misc/center-x
            :transform "translate(-50%,0)"}}
   (str "\u03A9-" count)])

;; description text appears in the splash/pause

(defn desc-controls
  "how do we fly this thing?"
  []
  [:div
   {:style {:z-index 2 :position "absolute"
            :top 350 :left misc/center-x
            :transform "translate(-50%,0)"
            :font-size "small" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:p [:strong "Controls"]]
   [:p "Arrows to move"]
   [:p "Space to fire"]
   [:p "Z to use \u03A9-13"]
   [:p "P to pause"]
   [:p "Enter to play!"]])

(defn desc-splash
  "splash game description"
  []
  [:div
   {:style {:z-index 2 :position "absolute"
            :top 130 :left misc/center-x
            :transform "translate(-50%,0)"
            :font-size "small" :padding 5
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
  []
  [:div
   {:style {:z-index 2 :position "absolute"
            :top 100 :left misc/center-x
            :transform "translate(-50%,0)"
            :font-size "small" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:p [:strong "Paused"]]
   [:p "Goals:"]
   [:p "Bust rocks"]
   [:p "Collect gems"]
  ;  [:p "Protect ships"]
  ;  [:p "Rescue pods"]
])

(defn desc-gameover
  "description for when the player has crashed"
  []
  [:div
   {:style {:z-index 2 :position "absolute"
            :top 100 :left misc/center-x
            :transform "translate(-50%,0)"
            :font-size "small" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
   [:p [:strong "You exploded!"]]
   [:p "If you have \u03A9-13, there is hope;"]
   [:p "hold 'Z' to go back 13 seconds!"]
   [:p "Otherwise, press 'Enter' to restart"]])

(defn desc-pods
  "description of lifepod objective"
  []
  [:div
   {:style {:z-index 2 :position "absolute"
            :top 185 :left 650
            :width "150px"
            :transform "translate(-50%,0)"
            :font-size "small" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
  ;  [:p "When a ship explodes, there may be escape pods."]
  ;  [:p "If you can, pick up the escape pods,"]
  ;  [:p "and bring them to the hospital station."]
   [:p "When a ship explodes, there may be life pods."]
   [:p "Bring any pods you catch to this hospital"]])

(defn desc-gems
  "description of the gem objective"
  []
  [:div
   {:style {:z-index 2 :position "absolute"
            :top 345 :left 150
            :width "150px"
            :transform "translate(-50%,0)"
            :font-size "small" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px"
            :text-align "center"}}
  ;  [:p "When a rock breaks, it may leave small gems"]
  ;  [:p "Try to pick some of them up, and then"]
  ;  [:p "drop them off at the refinery base here."]
   [:p "When a rock breaks, it may leave small gems."]
   [:p "Bring any gems you find to this refinery"]])

(def mode-desc
  {:pause desc-pause
   :gameover desc-gameover
   :go-pause desc-gameover
   :splash desc-splash})

(defn descriptions
  "game description component"
  [mode]
  (let [desc-main (mode mode-desc)]
    [:div
     (desc-main)
     (desc-controls)]))

(defn go-descriptions
  "game description component"
  []
  [:div
   (desc-gameover)])

;; score related

(defn score
  "current score"
  [{:keys [gem pod ship rock]}]
  [:div
   {:style {:z-index 2 :position "absolute"
            :top (- misc/game-height 80) :left 10
            ; :transform "translate(-50%,0)"
            :font-size "10px" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px" :white-space "pre"
            :text-align "center"}}
   [:strong "Score"] [:br]
   (str "Rocks:  " rock) [:br]
  ;  (str "Ships:  " ship) [:br] ;; add these back as they are implemented
   (str "Gems:  " gem) [:br]
  ;  (str "Pods:  " pod)
])

(defn hiscore
  "hiscore"
  [{:keys [gem pod ship rock] :or {gem 0 pod 0 ship 0 rock 0}}]
  [:div
   {:style {:z-index 2 :position "absolute"
            :top 10 :left 10
            ; :transform "translate(-50%,0)"
            :font-size "Small" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px" :white-space "pre"
            :text-align "center"}}
   [:p [:strong "High Scores"]]
   [:p (str "Most rocks busted:  " rock)]
  ;  [:p (str "Most ships protected:  " ship)]
   [:p (str "Most gems delivered:  " gem)]
  ;  [:p (str "Most pods rescued:  " pod)]
])

(defn cargo
  "current ship cargo"
  [{:keys [gem pod]}]
  [:div
   {:style {:z-index 2 :position "absolute"
            :top (- misc/game-height 130) :left 10
                       ; :transform "translate(-50%,0)"
            :font-size "10px" :padding 5
            :background-color (rgb 0 0 50 0.5)
            :border-radius "10px" :white-space "pre"
            :text-align "center"}}
   [:strong "Cargo"] [:br]
   (str "Gems:  " gem) [:br]
  ;  (str "Pods:  " pod)
])
