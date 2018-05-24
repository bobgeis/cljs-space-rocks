(ns cljs-space-rocks.drand
  "ns for deterministic random number generation")

;; deterministic random numbers
;; credit: https://gist.github.com/blixt/f17b47c62508be59987b

(def BIG 2147483646)
(def SMALL 16807)
(def seed-atom (atom 0))

(def BIG-MOD "used for new seeds" 2147483647)
(def BIGGEST "9007199254740991" js/Number.MAX_SAFE_INTEGER)

(defn next-int
  "given a seed int, n, get the next seed int"
  [n]
  (mod (* n SMALL) BIG))

(defn to-float
  "turn the seed int into a float [0,1)"
  [n]
  (/ (- n 1) BIG))

(defn set-seed!
  "set the current seed"
  [n] (reset! seed-atom n))

(defn get-seed
  "get the current seed"
  [] @seed-atom)

(def peek-seed "peek at the current seed" get-seed)

(defn next-seed
  "go to the next seed"
  [] (swap! seed-atom next-int))

(defn drand
  "[] = get a random number [0,1).
   [max] = get a random number [0,max).
   [min max] = get a random number [min,max)"
  ([] (to-float (next-seed)))
  ([max] (* max (drand)))
  ([min max] (+ min (drand (- max min)))))

(defn dint
  "[max] = get a random int [0,max).
   [min max] = get a random int [min,max]."
  ([max] (js/Math.floor (drand max)))
  ([min max] (+ min (dint (- (inc max) min)))))

(defn dnth
  "get a random value from a coll that supports nth"
  [coll] (nth coll (dint (count coll))))

(defn dctr
  "[] = drand but [-1,+1).
   [spread] = drand but [-spread,+spread).
   [start spread] = drand but between start +/- spread."
  ([] (* 2 (- (drand) 0.5)))
  ([spread] (* spread (dctr)))
  ([start spread] (+ start (dctr spread))))

(defn dangle
  "get a random angle in degrees"
  []
  (drand 360))

(defn drseed
  "get a new seed"
  [] (last (repeatedly (dint 5 15) next-seed)))

;; non-deterministic rand helpers

(defn rrand
  "like rand but allows two args to get [min,max)."
  ([] (rand))
  ([max] (rand max))
  ([min max] (+ min (rand (- max min)))))

(defn rint
  "like rand-int but allows two args to get [min,max]."
  ([max] (rand-int max))
  ([min max] (+ min (rand-int (- (inc max) min)))))

(defn rctr
  "[] = get a random number [-1,+1).
   [spread] = get a random number [-spread,+spread).
   [start spread] = get a random number between start +/- spread."
  ([] (rrand -1 1))
  ([spread] (rrand (- spread) spread))
  ([start spread] (+ start (rctr spread))))

(defn rangle
  "get a random angle [0,360)."
  [] (rand 360))

(defn rrseed
  "get a really random seed"
  []
  (rand-int BIG-MOD))
