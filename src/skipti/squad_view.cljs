(ns skipti.squad-view
  (:require [clojure.string :as str]
            [skipti.components :refer [Icon Pill]]))

(def messages
  {:en
   #:squad
   {:edit "Edit"
    :input.placeholder "Add player..."
    :match "Match"
    :new-match "New match"
    :title "Select team"
    :title-selected [:fn/str "Select team ({{:n}})"]}

   :nb
   #:squad
   {:edit "Rediger"
    :input.placeholder "Legg til spiller..."
    :match "Kamp"
    :new-match "Ny kamp"
    :title "Velg lag"
    :title-selected [:fn/str "Velg lag ({{:n}})"]}})

(defn SquadView [{:keys [add-player
                         back-to-match
                         edit-squad
                         empty-input?
                         input
                         new-match
                         players
                         title]}]
  [:main
   [:header
    [:h1 title]
    [:button.link-btn {:on {:click edit-squad}} [:i18n :squad/edit]]]
   [:section.col
    (for [player players]
      (Pill player))
    [:form.new-player-form {:class (when-not empty-input? "filled")
                            :on {:submit add-player}}
     [:input.input input]
     [:button {:type "submit"} (Icon :plus)]]]
   (when (or new-match back-to-match)
     [:footer
      (when new-match
        [:button.btn {:class (if back-to-match "btn-secondary" "btn-primary grow")
                      :on {:click new-match}}
         [:span [:i18n :squad/new-match]]
         (when-not back-to-match (Icon :caret-right))])
      (when back-to-match
        [:button.btn.btn-primary.grow {:on {:click back-to-match}}
         [:span [:i18n :squad/match]]
         (Icon :caret-right)])])])

(defn prep-squad-view [state]
  (let [squad (into #{} (:squad/players state))
        players (into #{} (:match/players state))]
    {:players
     (->> (sort squad)
          (map (fn [x]
                 {:key x
                  :label x
                  :color (when (players x) :medium)
                  :icon (if (players x)
                          {:name :check-circle.fill
                           :class :color-success}
                          {:name :circle
                           :class :subtle-md})
                  :icon-pos :end
                  :on-click
                  [[:assoc-in [:match/players] (if (players x)
                                                 (disj players x)
                                                 (conj players x))]]})))

     :input
     {:type "text"
      :placeholder [:i18n :squad/input.placeholder]
      :value (:squad/input state)
      :on {:input [[:assoc-in [:squad/input] :dom-event.target/value]]}}

     :empty-input?
     (str/blank? (:squad/input state))
     
     :add-player
     (let [input (str/trim (:squad/input state ""))]
       (if (str/blank? input)
         []
         [[:assoc-in [:squad/players] (conj squad input)]
          [:assoc-in [:squad/input] ""]]))

     :edit-squad
     [[:assoc-in [:view] :edit-squad]]

     :title
     (if (seq players)
       [:i18n :squad/title-selected {:n (count players)}]
       [:i18n :squad/title])

     :new-match
     (when (seq players)
       [[:assoc-in [:view] :match]
        [:assoc-in [:match/events] []]])

     :back-to-match
     (when (:match/events state)
       [[:assoc-in [:view] :match]])}))
