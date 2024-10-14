(ns skipti.match-view
  (:require [skipti.components :refer [Icon Pill]]))

(def messages
  {:en
   #:match
   {:bench "Bench"
    :new-period "New period"
    :pause "Pause"
    :pitch "Pitch"
    :start "Start"
    :title "Match"}

   :nb
   #:match
   {:bench "Benk"
    :new-period "Ny omgang"
    :pause "Pause"
    :pitch "Bane"
    :start "Start"
    :title "Kamp"}})

(defn MatchView [{:keys [bench
                         go-back
                         pitch
                         toggle
                         toggle-label
                         toggle-variant]}]
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
    [:button.btn {:on {:click go-back}} (Icon :caret-left)]
    [:button.btn.grow {:class toggle-variant
                       :on {:click toggle}}
     toggle-label]]])

(defn prep-match-view [state]
  (let [events (:match/events state [])
        event-map (group-by first events)
        players (:match/players state)
        started? (= :started (second (last (event-map :match))))
        paused? (= :stopped (second (last (event-map :match))))]
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
     []

     :toggle
     [[:conj-in-v [:match/events] [:match (if started? :stopped :started) :current-time]]]

     :toggle-label
     (cond
       started?
       [:i18n :match/pause]

       paused?
       [:i18n :match/new-period]

       :else
       [:i18n :match/start])

     :toggle-variant
     (if-not started? :btn-primary :btn-secondary)}))
