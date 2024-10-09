(ns skipti.main
  (:require [cljs.core.match :refer-macros [match]]
            [clojure.walk :as walk]
            [replicant.dom :as replicant]
            [skipti.squad-view :refer [prep-squad-view SquadView]]))

(defonce store (atom {}))

(defn interpolate-dom-values [dom-event action]
  (walk/postwalk
   (fn [x]
     (case x
       :dom-event.target/value
       (some-> dom-event .-target .-value)

       x))
   action))

(defn handle-action [state action]
  (apply js/console.log action)
  (match action
    [:assoc-in ks v]
    (assoc-in state ks v)))

(defn handle-actions [actions]
  (swap! store (fn [state]
                 (->> actions
                      (filter identity)
                      (reduce handle-action state)))))

(defn handle-dom-event [{:replicant/keys [^js js-event]} actions]
  (.preventDefault js-event)
  (.stopPropagation js-event)
  (when actions
    (handle-actions (map #(interpolate-dom-values js-event %) actions))))

(defn render
  ([]
   (render @store))
  ([state]
   (replicant/render
    (js/document.getElementById "root")
    (SquadView (prep-squad-view state)))))

(defn ^:export init []
  (add-watch store ::me (fn [_ _ _ state]
                          (render state)))
  (replicant/set-dispatch! handle-dom-event)
  (render))

