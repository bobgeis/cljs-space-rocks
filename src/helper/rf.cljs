(ns helper.rf
  "re-frame helpers"
  (:require
   [cljs.pprint :as pp :refer [pprint]]
   [re-frame.core :as rf]
   [re-frame.db :as rfdb]
   [helper.log :refer [clog]]))


(defn spy
  "pprint app db"
  ([]
   (clog @rfdb/app-db))
  ([arg]
   (spy)
   arg))

(def disp
  "dispatch an event"
  rf/dispatch)

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

(defn basic-sub
  "register a simple getter subscription, expects a keyword and an option vector of keywords"
  ([kw kpath]
   (rf/reg-sub kw (fn [db _] (get-in db kpath))))
  ([kw]
   (rf/reg-sub kw (fn [db _] (kw db)))))
