(ns skipti.match-view
  (:require [skipti.components :refer [Icon Pill]]
            [skipti.playing-time :refer [format-time match-clock players-with-time]]))

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
                         match-clock
                         pitch
                         toggle
                         toggle-label
                         toggle-variant]}]
  [:main
   [:header
    [:h1 [:i18n :match/title]]
    [:pre match-clock]]
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
        players (sort (:match/players state))
        pitch (filter (fn [player]
                        (= :on (second (last (event-map player)))))
                      players)
        bench (remove (set pitch) players)
        started? (= :started (second (last (event-map :match))))
        paused? (= :stopped (second (last (event-map :match))))
        cur-time (:time state)]
    {:bench
     (->> (players-with-time events cur-time bench)
          (sort-by second <)
          (map (fn [[player mins]]
                 {:key player
                  :label player
                  :caption mins
                  :icon {:name :arrow-fat-right.fill
                         :class :color-success}
                  :icon-pos :end
                  :on-click [[:conj-in-v [:match/events] [player :on :current-time]]]})))

     :pitch
     (->> (players-with-time events cur-time pitch)
          (sort-by second >)
          (map (fn [[player mins]]
                 {:key player
                  :label player
                  :caption mins
                  :icon {:name :arrow-fat-left.fill
                         :class :color-danger}
                  :on-click [[:conj-in-v [:match/events] [player :off :current-time]]]})))
     
     :go-back
     [[:assoc-in [:view] :squad]]

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
     (if-not started? :btn-primary :btn-secondary)

     :match-clock
     (format-time (match-clock events cur-time))}))
