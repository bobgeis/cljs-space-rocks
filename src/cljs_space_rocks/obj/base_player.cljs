(ns cljs-space-rocks.obj.base-player
  "ns for base-player interactions"
  (:require
   [helper.geom :as geom :refer [cc-hit?]]
   [helper.fun :as fun :refer [assoc-fn]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.obj.base :as base]
   [cljs-space-rocks.obj.player :as player]))

;; constants and helpers

;; query

;; manipulation

;; interact!

(defn interact
  [{:keys [player bases cargo score] :as scene}]
  (loop [cargo cargo
         score score
         bases bases
         base-seq (seq (vals bases))]
    (let [base (first base-seq)]
      (cond
        ;; base-case escape
        (not base) (assoc scene :cargo cargo :score score :bases bases)
        ;; if they aren't near, then continue
        (not (cc-hit? player base)) (recur cargo score bases (rest base-seq))
        ;; if they are near, then drop off cargo
        :else
        (let [res (:res base)]
          (recur (assoc cargo res 0)
                 (update score res #(+ % (res cargo)))
                 (assoc-fn bases :id (assoc base :glow base/glow-max))
                 (rest base-seq)))))))

