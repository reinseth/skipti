(ns skipti.locale)

(def locale-aliases
  {:no :nb})

(defn normalize-langs [langs]
  (->> langs
       (map #(re-find #"^[^-]+" %))
       (map keyword)
       (map #(get locale-aliases % %))))

(defn browser-languages []
  (cond
    (> (count js/navigator.languages) 0)
    (js->clj js/navigator.languages)

    js/navigator.language
    [js/navigator.language]

    js/navigator.userLanguage
    [js/navigator.userLanguage]

    :else
    []))

