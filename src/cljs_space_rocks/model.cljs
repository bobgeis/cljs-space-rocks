(ns cljs-space-rocks.model
  "ns for game model manipulation fns"
  (:require
    [re-frame.core :as rf]
    [com.rpl.specter :as sp]
    [helper.log :refer [jlog clog]]
    [helper.rf :refer [spy]]
    [helper.browser :as hb]))

(defn init-app-state
  "initialize the app state"
  [db ls-data]
  (clog "initializing app state")
  {:db {}})

