(ns cljs-space-rocks.obj.bullet-rock
  "ns for bullet-rock interations"
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
   [cljs-space-rocks.obj.rock :as rock]))

(def dot-number
  "number of particles to make in a rock explosion"
  0)

(defn rock-hit?
  "find a bullet hitting this rock, return nil if none found"
  [rock bullets]
  (not-NONE (sp/select-any [sp/MAP-VALS #(cc-hit? % rock)] bullets)))

(defn interact
  "handle bullet-rock interactions"
  [{:keys [bullets rocks booms particles loot score] :as scene}]
  (loop [bullets bullets
         rocks rocks
         booms booms
         particles particles
         loot loot
         score score
         rock-seq (vals rocks)]
    (let [rock (first rock-seq)
          bullet (rock-hit? rock bullets)]
      (cond
        ;; if no more rocks or no more bullets, then fall out
        (or (not rock) (not (first bullets)))
        (assoc scene
               :bullets bullets :rocks rocks :booms booms
               :loot loot :particles particles :score score)
        ;; if this rock had no collisions then do nothing, but shorten the rock-seq to check the next one
        (not bullet)
        (recur bullets rocks booms particles loot score (rest rock-seq))
        ;; if this rock did have collisions, then handle them
        :else
        (let [seed! (drand/set-seed! (:seed rock))]
          (recur
           (dissoc-fn bullets :id bullet)
           (-> rocks
               (dissoc-fn :id rock)
                ;; add assoc-fn calls with calves
               (assoc-fn-seq :id (rock/make-calves rock)))
           (assoc-fn booms :id (boom/obj->ex rock))
           (assoc-fn-seq particles :id (particle/make-particles dot-number rock))
           ;; gems aren't always produced
           (if (< (drand/drand) ((:mat rock) rock/mat->gem-chance))
             (assoc-fn loot :id (loot/obj->loot rock :gem))
             loot)
           (update score :rock inc)
           (rest rock-seq)))))))
