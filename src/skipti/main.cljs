(ns skipti.main
  (:require [replicant.dom :as replicant]))

(defn render []
  (replicant/render
   (js/document.getElementById "root")
   [:div "Skipti!"]))

(defn ^:export init []
  (render))
