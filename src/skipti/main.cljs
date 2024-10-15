(ns skipti.main
  (:require [cljs.core.match :refer-macros [match]]
            [clojure.edn :as edn]
            [clojure.walk :as walk]
            [m1p.core :as m1p]
            [replicant.dom :as replicant]
            [skipti.edit-squad-view
             :as edit-squad
             :refer [EditSquadView prep-edit-squad-view]]
            [skipti.locale :refer [browser-languages normalize-langs]]
            [skipti.match-view :as match :refer [MatchView prep-match-view]]
            [skipti.squad-view :as squad :refer [prep-squad-view SquadView]])
  (:import (goog.async Debouncer)))

;; Source: https://martinklepsch.org/posts/simple-debouncing-in-clojurescript.html
(defn debounce [f interval]
  (let [dbnc (Debouncer. f interval)]
    ;; We use apply here to support functions of various arities
    (fn [& args] (.apply (.-fire dbnc) dbnc (to-array args)))))

(defn write-to-local-storage [state]
  (js/localStorage.setItem "skipti" state))

(def write-to-local-storage-debounced (debounce write-to-local-storage 500))

(defn read-from-local-storage []
  (edn/read-string (or (js/localStorage.getItem "skipti") "{}")))

(defonce store (atom (read-from-local-storage)))

(def dictionaries
  (let [messages [squad/messages
                  edit-squad/messages
                  match/messages]]
    {:en (m1p/prepare-dictionary (map :en messages))
     :nb (m1p/prepare-dictionary (map :nb messages))}))

(defn render-view [state]
  (case (:view state)
    :edit-squad (EditSquadView (prep-edit-squad-view state))
    :match (MatchView (prep-match-view state))
    (SquadView (prep-squad-view state))))

(def supported-locales (set (keys dictionaries)))

(defn interpolate-dom-values [dom-event action]
  (walk/postwalk
   (fn [x]
     (case x
       :dom-event.target/value
       (some-> dom-event .-target .-value)

       x))
   action))

(defn interpolate-action-data [action]
  (walk/postwalk
   (fn [x]
     (case x
       :current-time
       (js/Date.now)

       x))
   action))

(defn conj-v [coll x]
  (if (vector? coll)
    (conj coll x)
    (conj (vec coll) x)))

(defn handle-action [state action]
  (apply js/console.log action)
  (match action
    [:assoc-in ks v]
    (assoc-in state ks v)

    [:conj-in-v ks v]
    (update-in state ks conj-v v)))

(defn handle-actions [actions]
  (swap! store (fn [state]
                 (->> actions
                      (filter identity)
                      (map interpolate-action-data)
                      (reduce handle-action state)))))

(defn handle-dom-event [{:replicant/keys [^js js-event]} actions]
  (.preventDefault js-event)
  (.stopPropagation js-event)
  (when actions
    (handle-actions (map #(interpolate-dom-values js-event %) actions))))

(defn lookup-locale [saved-locale]
  (or (supported-locales saved-locale)
      (first (filter supported-locales
                     (normalize-langs (browser-languages))))
      :en))

(defn render
  ([]
   (render @store))
  ([state]
   (replicant/render
    (js/document.getElementById "root")
    (m1p/interpolate (render-view state)
                     {:dictionaries
                      {:i18n (dictionaries (lookup-locale (:locale state)))}}))))

(defn ^:export init []
  (add-watch store ::me (fn [_ _ _ state]
                          (render state)
                          (write-to-local-storage-debounced state)))
  (replicant/set-dispatch! handle-dom-event)
  (js/setInterval (fn [] (swap! store assoc :time (js/Date.now))) 1000)
  (render))

