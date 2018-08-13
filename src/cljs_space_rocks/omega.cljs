(ns cljs-space-rocks.omega
  "ns for data and functions related to the omega-13 device"
  (:require
   [helper.log :refer [clog]]
   [helper.fun :as fun :refer [assoc-fn floor]]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.obj.player :as player]))

;; constants and helpers

(def max-seconds
  "maximum number of seconds the omega-13 can take you back"
  13)

(def min-seconds
  "minimum number of seconds before the omega-13 becomes available"
  2)

(def ticks-per-second
  "60 frames per second is what we'd like"
  60)

(def speed-up
  "how much faster should going back be than going forward"
  5)

(def scenes-per-second
  "how many scenes should be saved every second"
  (floor (/ ticks-per-second speed-up)))

(def max-scenes
  "max number of omega-scenes that should get saved"
  (* scenes-per-second max-seconds))

(def min-scenes
  "minimum number of omega-scenes before time travel should be available"
  (* scenes-per-second min-seconds))

(defn trim-back
  "put the first n items of q into a new #queue"
  [q n]
  (into [] (take n q)))

(defn trim-front
  "put the last n items of q into a new #queue"
  [q n]
  (into [] (take-last n q)))

(defn mode-change
  "how should the mode change on omega triggering?"
  [mode]
  (get {:gameover :play :go-pause :pause} mode mode))

(defn current-second
  "given the current timeline scene offset, how many seconds are left in the timeline?"
  [current]
  (if current (floor (/ current scenes-per-second))))

;; model

(def initial-omega
  "an initial omega-13"
  {:timeline [] ;; stores scenes of the past
   :trigger false ;; should we trigger?
   :current nil ;; offset of the current timeline scene, if activated, else nil
})

;; query

(defn too-few?
  "are there too many scenes? (should be unavailable to player)"
  [{timeline :timeline}]
  (< (count timeline) min-scenes))

(defn too-many?
  "are there too many scenes? (some should be trimmed)"
  [{timeline :timeline}]
  (> (count timeline) max-scenes))

(defn trigger?
  "has the trigger been set?"
  [omega]
  (and (:trigger omega) (:current omega)))

;; manipulation

(defn start
  "start charging omega"
  [{:keys [timeline] :as omega}]
  (if (too-few? omega) omega
      (assoc omega :current (dec (count timeline)))))

(defn stop
  "stop charging omega and abort time travel"
  [{:as omega}]
  (assoc omega :current nil :trigger false))

(defn set-trigger
  "set trigger to true"
  [omega]
  (assoc omega :trigger true))

(defn current-scene
  "returns the scene corresponding to current, or nil if there isn't one"
  [{:keys [timeline current] :as omega}]
  (cond
    (not current) nil
    (>= current (count timeline)) nil
    :else (nth timeline current)))

(defn trigger-omega
  "return the new omega map if this one is triggered."
  [{:keys [timeline current] :as omega}]
  (assoc omega
         :current nil
         :trigger false
         :timeline (trim-back timeline current)))

;; update

(defn tick-current
  "maybe tick current"
  [{:keys [timeline current trigger] :as omega}]
  (cond-> omega
    trigger (assoc :trigger false)
    (> current 0) (update :current dec)))

(defn tick-timeline
  "maybe conj the new scene onto timeline.
  should only be used in play mode"
  [{:keys [timeline current] :as omega} {tick :tick :as scene}]
  (if-not (= 0 (mod tick scenes-per-second)) omega
          (assoc omega
                 :timeline
                 (cond-> timeline
                   (not current) (trim-front max-scenes)
                   :always (conj scene)))))

;; db queries

(defn current-scene-db
  "what is the current timeline scene"
  [db]
  (current-scene (:omega db)))

(defn timeline-count-db
  "how many scenes are stored"
  [db]
  (count (:timeline (:omega db))))

(defn timeline-seconds-db
  "how many seconds of scenes are stored"
  [db]
  (floor (/ (timeline-count-db db) scenes-per-second)))

;; db manipulation

(defn start-db
  "begin time travel"
  [db]
  (update db :omega start))

(defn stop-db
  "abort time travel"
  [db]
  (update db :omega stop))

(defn set-trigger-db
  "set to trigger on the next tick"
  [db]
  (update db :omega set-trigger))

(defn trigger-db
  "check if the omega should trigger right now, and handle it if so"
  [{mode :mode omega :omega {player :player} :scene :as db}]
  (assoc db
         :mode (mode-change mode)
         :scene (update (current-scene omega) :player #(player/merge-control % player))
         :omega (trigger-omega omega)))

;; db update

(defn tick-current-db
  "update the omega current value"
  [db]
  (update db :omega tick-current))

(defn tick-or-trigger-db
  "tick current or trigger as necessary"
  [db]
  (if (trigger? (:omega db)) (trigger-db db)
      (tick-current-db db)))

(defn tick-timeline-db
  "maybe add the current scene to the timeline (for play mode only)"
  [{scene :scene :as db}]
  (update db :omega #(tick-timeline % scene)))

