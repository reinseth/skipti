(ns skipti.components
  (:require [phosphor.icons :as i]))

(def icons
  {:arrow-fat-right.fill (i/icon :phosphor.fill/arrow-fat-right)
   :caret-left (i/icon :phosphor.regular/caret-left)
   :caret-right (i/icon :phosphor.regular/caret-right)
   :check-circle.fill (i/icon :phosphor.fill/check-circle)
   :circle (i/icon :phosphor.regular/circle)
   :minus-circle.fill (i/icon :phosphor.fill/minus-circle)
   :plus (i/icon :phosphor.regular/plus)})

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

(defn Pill [{:keys [key label color icon icon-action icon-pos on-click]}]
  (let [icon-pos (or icon-pos :start)
        icon-el (when icon
                  [:div.pill-icon {:role (when icon-action "button")
                                   :tab-index (when icon-action 0)
                                   :on {:click icon-action}}
                   (Icon icon)])]
    [:div.pill
     {:replicant/key key
      :style {:opacity 1
              :transition "opacity 0.25s, background-color 0.25s"}
      :replicant/mounting {:style {:opacity 0}}
      :replicant/unmounting {:style {:opacity 0}}
      :class [(case color
                :strong "pill-strong"
                :medium "pill-medium"
                "pill-soft")
              (when (and icon (= icon-pos :start))
                :pill-icon-start)
              (when (and icon (= icon-pos :end))
                :pill-icon-end)]
      :role (when on-click "button")
      :tab-index (when on-click "0")
      :on {:click on-click}}
     (when (= icon-pos :start)
       icon-el)
     [:span.pill-label label]
     (when (= icon-pos :end)
       icon-el)]))
