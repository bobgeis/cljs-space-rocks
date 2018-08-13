(ns cljs-space-rocks.obj.player-rock
  "ns for player-rock interactions"
  (:require
   [com.rpl.specter :as sp]
   [helper.geom :as geom :refer [cc-hit?]]
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq dissoc-fn]]
   [helper.log :refer [clog]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.misc :as misc :refer [not-NONE]]
   [cljs-space-rocks.obj.boom :as boom]
   [cljs-space-rocks.obj.loot :as loot]
   [cljs-space-rocks.obj.particle :as particle]
   [cljs-space-rocks.obj.player :as player]
   [cljs-space-rocks.obj.rock :as rock]))

(defn player-hit?
  "has any rock collided with the player?"
  [player rocks]
  (not-NONE (sp/select-any [sp/MAP-VALS #(cc-hit? % player)] rocks)))

(def dot-number
  "number of particles to make in a play explosion"
  60)

(defn kill-player
  "the player has been hit, update the scene as necessary"
  [{:keys [player cargo booms particles loot scene-effects] :as scene}]
  (-> scene
      (dissoc :player)
      (assoc
       :booms (assoc-fn booms :id
                        (boom/obj->ex player))
       :particles (assoc-fn-seq particles :id
                                (particle/make-particles dot-number player))
       :loot (-> loot
                 (assoc-fn-seq
                  :id
                  (repeatedly (:gem cargo) #(loot/obj->loot player :gem)))
                 (assoc-fn-seq
                  :id
                  (repeatedly (inc (:pod cargo)) #(loot/obj->loot player :pod))))
       :scene-effects (assoc scene-effects
                             :change-mode "gameover"))))

(defn interact
  [{:keys [player rocks] :as scene}]
  (if (player-hit? player rocks)
    (kill-player scene)
    scene))
