(ns cljs-space-rocks.obj.base-player
  "ns for base-player interactions"
  (:require
   [helper.geom :as geom :refer [cc-hit?]]
   [helper.fun :as fun :refer [assoc-fn]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.obj.base :as base]
   [cljs-space-rocks.obj.player :as player]))

;; constants and helpers

(def res->color
  "determine what bullet color buff comes from a resource"
  {:gem :gold
   :pod :red})

(def amt->ticks
  {0 0
   1 200
   2 400
   3 600
   4 800
   5 1000
   6 1200
   7 1400
   8 1600
   9 1800
   10 2000
   11 2100
   12 2200
   13 2300
   14 2400
   15 2500
   16 2600
   17 2700
   18 2800
   19 2900
   20 3000})

(defn powerup-ticks
  "get the number of ticks the powerup should last, given the amount of loot delivered"
  [amt old-ticks]
  (max old-ticks (get amt->ticks amt 3000)))

;; query

;; manipulation

;; interact!

(defn interact
  [{:keys [player bases cargo score] :as scene}]
  (loop [player player
         cargo cargo
         score score
         bases bases
         base-seq (seq (vals bases))]
    (let [base (first base-seq)]
      (cond
        ;; base-case escape
        (not base) (assoc scene :player player :cargo cargo :score score :bases bases)
        ;; if they aren't near, then continue
        (not (cc-hit? player base)) (recur player cargo score bases (rest base-seq))
        ;; if they are near, then drop off cargo
        :else
        (let [res (:res base)
              amt (res cargo)]
          (recur (if (> amt 0)
                   (assoc player
                          :bullet-type (res res->color)
                          :powerup-ticks (powerup-ticks amt (:powerup-ticks player)))
                   player)
                 (assoc cargo res 0)
                 (update score res #(+ % amt))
                 (assoc-fn bases :id (assoc base :glow base/glow-max))
                 (rest base-seq)))))))

