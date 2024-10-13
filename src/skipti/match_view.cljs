(ns skipti.match-view
  (:require [skipti.components :refer [Icon]]))

(def messages
  {:en
   #:match
   {:title "Match"}

   :nb
   #:match
   {:title "Kamp"}})

(defn MatchView [{:keys [go-back]}]
  [:main
   [:header [:i18n :match/title]]
   [:div.col]
   [:footer
    [:button.btn {:on {:click go-back}} (Icon :caret-left)]]])

(defn prep-match-view [state]
  {:go-back
   [[:assoc-in [:view] :squad]]})
