(ns cljs-space-rocks.obj.loot-player
  "ns for loot-player interactions"
  (:require
   [com.rpl.specter :as sp]
   [helper.geom :as geom :refer [cc-hit?]]
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq dissoc-fn]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.misc :as misc :refer [not-NONE]]
   [cljs-space-rocks.obj.loot :as loot]))

;; helpers

(defn inc-cargo
  [cargo item]
  (update cargo (:type item) inc))

;; interact!

(defn interact
  "given a scene, interact the loot and the player"
  [{:keys [loot player cargo] :as scene}]
  (loop [cargo cargo
         loot loot loot-seq
         (seq (vals loot))]
    (let [item (first loot-seq)]
      (cond
        ;; if no more items, then we're done
        (not item) (assoc scene :cargo cargo :loot loot)
        ;; if the player didn't hit the item, then continue
        (not (cc-hit? player item)) (recur cargo loot (rest loot-seq))
        ;; otherwise update things
        :else
        (recur (inc-cargo cargo item)
               (dissoc-fn loot :id item)
               (rest loot-seq))))))
