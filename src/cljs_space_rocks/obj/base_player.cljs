(ns cljs-space-rocks.obj.base-player
  "ns for base-player interactions"
  (:require
   [com.rpl.specter :as sp]
   [helper.geom :as geom :refer [cc-hit?]]
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq dissoc-fn]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.misc :as misc :refer [not-NONE]]
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
        (let [type (:type base)]
          (recur (assoc cargo type 0)
                 (update score type #(+ % (type cargo)))
                 (assoc-fn bases :id (assoc base :glow base/glow-max))
                 (rest base-seq)))))))

