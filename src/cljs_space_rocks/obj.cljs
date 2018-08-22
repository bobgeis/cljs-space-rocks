(ns cljs-space-rocks.obj
  "ns for functions on object maps"
  (:require
   [com.rpl.specter :as sp]
   [helper.fun :as fun :refer [floor outside?]]
   [helper.color :refer [rgb hsl]]
   [helper.rf :as hr :refer [<sub >evt spy]]
   [helper.log :refer [clog]]
   [helper.geom :as geom :refer [max-degrees ra->xy deg->rad]]
   [cljs-space-rocks.misc :as misc]
   [cljs-space-rocks.drand :as drand]))

;; constants

(def max-x misc/xt-box)
(def max-y misc/yt-box)

;; query

(defn kill?
  "an object should be removed if its life is 0"
  [obj]
  (= 0 (:life obj)))

;; manipulation

;; drag

(defn apply-drag
  "apply velocity drag"
  [v d]
  (* v (- 1 (or d 0))))

;; physics

(defn physics
  "move an object using velocity, acceleration and drag"
  [{:keys [x vx y vy a va acc drag clamp] :as obj}]
  (let [[ax ay] (if acc (ra->xy acc (deg->rad a)) [0 0])
        edge (if clamp fun/clamp fun/wrap)]
    (assoc obj
           :x (edge (+ x vx ax) max-x)
           :y (edge (+ y vy ay) max-y)
           :a (fun/wrap (+ a va) max-degrees)
           :vx (apply-drag (+ vx ax) drag)
           :vy (apply-drag (+ vy ay) drag))))

(defn physics!
  "as physics but takes and returns a transient"
  [{:keys [x vx y vy a va acc drag clamp] :or {:a 0 :va 0 :drag 0 :clamp false} :as obj}]
  (let [[ax ay] (if (and acc a) (ra->xy acc (deg->rad a)) [0 0])
        edge (if clamp fun/clamp fun/wrap)]
    (assoc! obj
            :x (edge (+ x vx ax) max-x)
            :y (edge (+ y vy ay) max-y)
            :a (fun/wrap (+ a va) max-degrees)
            :vx (apply-drag (+ vx ax) drag)
            :vy (apply-drag (+ vy ay) drag))))


;; update

(defmulti tick "update an object map by one tick" :type)
(defmethod tick :default [obj]
  (physics obj))

(defn tick-all
  "tick all the objects in a collection map"
  [objs]
  (sp/transform [sp/MAP-VALS] tick objs))

;; view

(defmulti svg "get the svg for one game object" :type)
(defmethod svg :default [& args] (clog ["default svg" args]) [:g])

(defn fizzbuzz-svg
  "draw the svg multiple times for objects at the edges and corners"
  [obj]
  (if-not (:fizzbuzz obj) (svg obj)
          (if-let [objs (misc/get-replica-objs obj)]
            (into [:g] (map svg) objs)
            (svg obj))))

(defn sub->svgs
  "given a reg-sub key, get a coll of objects and return their svg group"
  [kw]
  (let [objs (<sub [kw])]
    (into [:g] (map fizzbuzz-svg) (vals objs))))
