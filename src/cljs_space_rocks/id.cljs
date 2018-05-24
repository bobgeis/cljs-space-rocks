(ns cljs-space-rocks.id
  "ns for id functions")


(defn get-number-wheel
  "Given an atom with an int in it, create a number wheel function.
  Calling the returned function will increment the atom and return
  the new value which can be used as a unique id#.
  This will stop working when Number.MAX_SAFE_INTEGER is reached,
  which is about 9*10^15.  If that is ever reached, it can probably
  be fixed by a simple change to use *two* ints, but I don't expect
  it to come up."
  ([ticker-atom]
   (fn []
     (swap! ticker-atom inc))))

(def misc-ticker-atom (atom 0))

(def get-id
  "generic number wheel for ids. no args."
  (get-number-wheel misc-ticker-atom))
