(ns cljs-space-rocks.obj.rock
  "ns for rock constants and funtions"
  (:require
   [clojure.string :as str]
   [com.rpl.specter :as sp]
   [helper.fun :as fun :refer [assoc-fn assoc-fn-seq sjoin]]
   [helper.geom :as geom :refer [ra->xy deg->rad]]
   [helper.svg :as svg :refer [css-transform]]
   [helper.color :refer [hsl]]
   [helper.log :as log :refer [clog]]
   [cljs-space-rocks.drand :as drand]
   [cljs-space-rocks.id :as id]
   [cljs-space-rocks.misc :as misc]))

;; constants

(def types
  "types of asteroids"
  {:C "carbonaceous"
   :S "silicaceous"
   :M "metallic"
   :ice "icy"})

(def size->radius
  "map size to radius (px)"
  {:tiny 10
   :small 15
   :medium 21
   :large 28
   :huge 36})

(def size->smaller-size
  "map size to next lowest size"
  {:tiny nil
   :small :tiny
   :medium :small
   :large :medium
   :huge :large})

(def point-n
  "number of points to draw on a rock"
  9)

(def point-dn
  "variability in number of points (range = n +/- dn)"
  1)

(def point-da
  "angular variability of points (degrees)"
  (deg->rad 15))

(def point-dr
  "radial variability of points (fraction)"
  0.25)

(def calf-dva
  "change in angular velocity of calves"
  2)

(def calf-dv
  "change in linear velocity of calves"
  1.5)

(def type->colors
  {:ice {:outer (hsl 220 30 65)
         :stroke (hsl 220 30 30)
         :inner (hsl 220 30 75)}
   :S {:outer (hsl 50 30 50)
       :stroke (hsl 50 30 30)
       :inner (hsl 50 30 60)}
   :C {:outer (hsl 25 40 50)
       :stroke (hsl 25 40 30)
       :inner (hsl 25 40 60)}
   :M {:outer (hsl 10 30 60)
       :stroke (hsl 10 30 30)
       :inner (hsl 10 30 70)}})

(def type->calf-nums
  "use type to get vecs of numbers for rand-nth"
  {:M [1 2 2 2 2 2 3 3]
   :C [1 1 2 2 2 2 2 3]
   :S [1 1 1 2 2 2 2 2]
   :ice [1 1 1 1 2 2 2 2]})

(def type->gem-chance
  "get the gem chance for each rock type"
  {:M 0.2
   :C 0.25
   :S 0.3
   :ice 0.5})

(def spawn-sizes
  "the sizes that things can spawn at, duplicates are more likely"
  [:medium
   :large
   :large
   :large
   :large
   :huge])

(def spawn-types
  "the types that can spawn, duplicates are more likely"
  [:C
   :C
   :C
   :S
   :S
   :S
   :S
   :M
   :ice])

(def inner-scale
  "scale of the inner polygon when drawing"
  0.7)

(def inner-transform
  "the css transform for the inner polygon"
  (sjoin ["scale(" inner-scale ")"]))

;; helpers

(defn get-num-calves
  "get the number of calves"
  [type]
  (drand/dnth (type type->calf-nums)))

(defn get-point
  "Get a single point. Not deterministic!"
  [r a]
  (ra->xy
   (* r (+ (- 1 point-dr) (* 2 (rand point-dr))))
   (+ (- (deg->rad a) point-da) (* 2 (rand point-da)))))

(defn get-points
  "Get the points for a rock.  Not deterministic!"
  [r]
  (let [n (+ (- point-n point-dn) (rand-int (inc point-dn)))
        da (/ 360 n)]
    (for [i (range n)
          :let [a (* i da)]]
      (get-point r a))))

;; model

(defn create
  "create a rock"
  [x y vx vy a va size type seed]
  {:x x :y y :vx vx :vy vy :a a :va va
   :size size :r (get size->radius size 9)
   :seed seed :id (id/get-id) :type type
   :pts (get-points (get size->radius size 9))})

(defn make-spawn
  "make the map for a rock that will spawn on the edge"
  []
  (let [[x y] (misc/drand-edge-point)
        [vx vy] (ra->xy (drand/drand calf-dv) (drand/dangle))
        a (drand/dangle)
        va (drand/dctr calf-dva)
        type (drand/dnth spawn-types)
        size (drand/dnth spawn-sizes)
        seed (drand/drseed)]
    (create x y vx vy a va size type seed)))

(defn initial-rocks
  "get the initial rock-map for game start"
  []
  (drand/set-seed! (drand/rrseed))
  (assoc-fn
   {} :id
   (make-spawn)
   (make-spawn)
   (make-spawn)))

(defn make-calf
  "make one calf from this rock"
  [{:keys [x y vx vy a va r type size] :as rock}]
  (let [dva (drand/dctr calf-dva)
        [ax ay] (ra->xy (drand/drand calf-dv) (drand/dangle))
        [dx dy] (ra->xy (drand/drand r) (drand/dangle))]
    (create (+ x dx) (+ y dy) (+ vx ax) (+ vy ay) a (+ va dva)
            (size size->smaller-size) type (drand/drseed))))

(defn make-calves
  "given a rock, return a seq of calves"
  [{:keys [type size seed] :as rock}]
  (if (= size :tiny) []
      (let [n (get-num-calves type)]
        (repeatedly n #(make-calf rock)))))

;; query

;; manipulation

(defn tick
  "tick a rock"
  [rock]
  (misc/physics rock))

;; view

(defn make-pts-string
  "given a vector of vectors of numbers,
  turn it into a string of space separated numbers"
  [pts]
  (sjoin (map sjoin pts)))

(defn svg
  "polygon svg for the given rock"
  [{:keys [x y a r pts inner-pts type] :as rock}]
  (let [colors (type type->colors)]
    [:g {:transform (css-transform rock)}
     [:polygon
      {:points (make-pts-string pts)
       :fill (:outer colors)
       :stroke "black"}]
     [:polygon
      {:transform inner-transform
       :points (make-pts-string pts)
       :fill (:inner colors)}]]))

(defn svg-omega
  "svg for the rock while time travelling"
  [{:keys [x y a r pts inner-pts type] :as rock}]
  (let [color (hsl "150" "100" "75" "0.5")]
    [:g {:transform (css-transform rock)}
     [:polygon
      {:points (make-pts-string pts)
       :fill color}]
     [:polygon
      {:transform inner-transform
       :points (make-pts-string pts)
       :fill color}]]))
