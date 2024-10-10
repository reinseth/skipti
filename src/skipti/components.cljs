(ns skipti.components
  (:require [phosphor.icons :as i]))

(def icons
  {:plus (i/icon :phosphor.regular/plus)})

(defn size-to-css-value [size]
  (case (or size :md)
    :context "1em"
    :sm 16
    :md 24
    :lg 32))

(defn Icon [name-or-props]
  (let [{:keys [name size style] :as props} (if (keyword? name-or-props)
                                              {:name name-or-props}
                                              name-or-props)]
    (if-let [svg (@i/icons (icons name))]
      (-> svg
          (assoc-in [1 :fill] "currentColor")
          (assoc-in [1 :style] (cond-> {:display "inline-block"
                                        :line-height "1"
                                        :width (size-to-css-value size)
                                        :height (size-to-css-value size)}
                                 style (into style)))
          (update 1 merge (dissoc props :name :size :style)))
      (throw (js/Error. (str "Icon " name " has not been configured"))))))

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
