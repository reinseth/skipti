(ns skipti.match-view
  (:require [skipti.components :refer [Icon Pill]]))

(def messages
  {:en
   #:match
   {:bench "Bench"
    :pitch "Pitch"
    :title "Match"}

   :nb
   #:match
   {:bench "Benk"
    :pitch "Bane"
    :title "Kamp"}})

(defn MatchView [{:keys [bench
                         go-back
                         pitch]}]
  [:main
   [:header [:i18n :match/title]]
   [:div.split
    [:section.col
     [:div [:i18n :match/bench]]
     (for [player bench]
       (Pill player))]
    [:section.col.col-soft
     [:div [:i18n :match/pitch]]
     (for [player pitch]
       (Pill player))]]
   [:footer
    [:button.btn {:on {:click go-back}} (Icon :caret-left)]]])

(defn prep-match-view [state]
  (let [players (:match/players state)]
    {:bench
     (->> (sort players)
          (map (fn [player]
                 {:key player
                  :label player
                  :icon {:name :arrow-fat-right.fill
                         :class :color-success}
                  :icon-pos :end})))

     :go-back
     [[:assoc-in [:view] :squad]]

     :pitch
     []}))
