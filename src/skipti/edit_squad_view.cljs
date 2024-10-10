(ns skipti.edit-squad-view
  (:require [skipti.components :refer [Pill]]))

(def messages
  {:en
   #:edit-squad
   {:finish "Finish"
    :title "Edit squad"}

   :nb
   #:edit-squad
   {:finish "Ferdig"
    :title "Rediger tropp"}})

(defn EditSquadView [{:keys [finish-edit players]}]
  [:main
   [:header
    [:h1 [:i18n :edit-squad/title]]
    [:button.link-btn {:on {:click finish-edit}} [:i18n :edit-squad/finish]]]
   [:section.col
    (for [player players]
      (Pill player))]])

(defn prep-edit-squad-view [state]
  (let [squad (into #{} (:squad/players state))]
    {:players
     (->> (sort squad)
          (map (fn [x] {:key x :label x})))

     :finish-edit
     [[:assoc-in [:view] :squad]]}))
