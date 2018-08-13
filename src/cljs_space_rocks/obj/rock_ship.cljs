(ns cljs-space-rocks.obj.rock-ship
  "ns for interactions between ships and rocks and the edges"
  (:require
   [com.rpl.specter :as sp]
   [helper.geom :as geom :refer [cc-hit?]]
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq dissoc-fn]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.misc :as misc :refer [not-NONE]]
   [cljs-space-rocks.obj :as obj]
   [cljs-space-rocks.obj.boom :as boom]
   [cljs-space-rocks.obj.loot :as loot]
   [cljs-space-rocks.obj.ship :as ship]
   [cljs-space-rocks.obj.rock :as rock]))

(defn ship-hit?
  "determine if this ship has hit any rocks"
  [ship rocks]
  (not-NONE (sp/select-any [sp/MAP-VALS #(cc-hit? % ship)] rocks)))

(defn ship-on-edge?
  "determine if this ship has reached the edge and should jump"
  [{x :x y :y}]
  (cond
    (<= x 0) true
    (>= x obj/max-x) true
    (<= y 0) true
    (>= y obj/max-y) true
    :else false))

(defn interact
  "given a scene, handle rock-ship interactions and consequences"
  [{:keys [ships rocks loot booms score] :as scene}]
  ;; bind the things that can change
  (loop [ships ships
         loot loot
         booms booms
         score score
         ship-seq (vals ships)]
    (let [ship (first ship-seq)]
      (cond
        ;; if no ships, then we're done
        (not ship)
        (assoc scene :ships ships :loot loot :booms booms :score score)
        ;; if the ship was off the map, then it should jump out
        (ship-on-edge? ship)
        (recur (dissoc-fn ships :id ship)
               loot
               (assoc-fn booms :id (boom/obj->out ship {:fizzbuzz false}))
               (update score :ship inc)
               (rest ship-seq))
        ;; if a ship was hit, then handle consequences (dissoc ship, add explosion and loot) and recur
        (ship-hit? ship rocks)
        (recur (dissoc-fn ships :id ship)
               (assoc-fn-seq loot :id (ship/make-loot ship))
               (assoc-fn booms :id (boom/obj->ex ship))
               score
               (rest ship-seq))
        ;; otherwise, continue through the rest of ship-seq
        :else
        (recur ships loot booms score (rest ship-seq))))))
