(ns skipti.components)

(defn Pill [{:keys [key label on-click]}]
  [:div.pill
   {:replicant/key key
    :style {:opacity 1
            :transition "all 0.25s"}
    :replicant/mounting {:style {:opacity 0}}
    :replicant/unmounting {:style {:opacity 0}}
    :role (when on-click "button")
    :tab-index (when on-click "0")
    :on {:click on-click}}
   [:span.pill-label label]])
