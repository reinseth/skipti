(ns skipti.squad-view
  (:require [clojure.string :as str]))

(defn SquadView [{:keys [add-player
                         input
                         players]}]
  [:main
   [:header
    [:h1 "Squad"]]
   [:section
    (for [player players]
      [:div {:key player} player])
    [:form {:on {:submit add-player}}
     [:input.input input]
     [:button {:type "submit"} "+"]]]])

(defn prep-squad-view [state]
  (let [squad (into #{} (:squad/players state))]
    {:players
     (sort squad)

     :input
     {:type "text"
      :placeholder "Add player..."
      :value (:squad/input state)
      :on {:input [[:assoc-in [:squad/input] :dom-event.target/value]]}}
     
     :add-player
     (let [input (str/trim (:squad/input state ""))]
       (if (str/blank? input)
         []
         [[:assoc-in [:squad/players] (conj squad input)]
          [:assoc-in [:squad/input] ""]]))}))
