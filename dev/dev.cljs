(ns dev
  (:require [skipti.main :refer [render]]))

(defn ^:dev/after-load re-render []
  (js/console.log "re-render")
  (render))
