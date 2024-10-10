(ns skipti.squad-view
  (:require [clojure.string :as str]
            [skipti.components :refer [Pill]]))

(defn SquadView [{:keys [add-player
                         empty-input?
                         input
                         players]}]
  [:main
   [:header
    [:h1 "Squad"]]
   [:section.col
    (for [player players]
      (Pill player))
    [:form.new-player-form {:class (when-not empty-input? "filled")
                            :on {:submit add-player}}
     [:input.input input]
     [:button {:type "submit"} "+"]]]])

(defn prep-squad-view [state]
  (let [squad (into #{} (:squad/players state))]
    {:players
     (->> (sort squad)
          (map (fn [x] {:key x :label x})))

     :input
     {:type "text"
      :placeholder "Add player..."
      :value (:squad/input state)
      :on {:input [[:assoc-in [:squad/input] :dom-event.target/value]]}}

     :empty-input?
     (str/blank? (:squad/input state))
     
     :add-player
     (let [input (str/trim (:squad/input state ""))]
       (if (str/blank? input)
         []
         [[:assoc-in [:squad/players] (conj squad input)]
          [:assoc-in [:squad/input] ""]]))}))
