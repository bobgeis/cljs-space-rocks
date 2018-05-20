(ns helper.rf
  "re-frame helpers"
  (:require
    [cljs.pprint :as pp :refer [pprint]]
    [re-frame.core :as rf]
    [re-frame.db :as rfdb]))


(defn spy
  "pprint app db"
  []
  (-> @rfdb/app-db
    pprint
    identity))

(defn <sub
  "another way to subscribe, for use in views
  so that @(rf/subscribe [:model]) becomes (<sub [:model])"
  [vector]
  @(rf/subscribe vector))

(defn >evt
  "another way to dispatch for use in views,
  so that #(rf/dispatch [:init]) because (>evt [:init])"
  [vector]
  (fn [] (rf/dispatch vector)))