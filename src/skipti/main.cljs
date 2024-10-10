(ns skipti.main
  (:require [cljs.core.match :refer-macros [match]]
            [clojure.edn :as edn]
            [clojure.walk :as walk]
            [replicant.dom :as replicant]
            [skipti.squad-view :refer [prep-squad-view SquadView]])
  (:import [goog.async Debouncer]))

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
                          (render state)
                          (write-to-local-storage-debounced state)))
  (replicant/set-dispatch! handle-dom-event)
  (render))

