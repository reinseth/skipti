(ns skipti.playing-time
  (:require [cljs.core.match :refer-macros [match]]
            [clojure.math :as math]))

(defn ->intervals [events entity from to]
  (reduce
   (fn [acc [e v t]]
     (if-not (= e entity)
       acc
       (cond
         (= v from)
         (conj acc [t])

         (= v to)
         (let [i (last acc)]
           (if i
             (conj (pop acc) (conj i t))
             (conj acc [nil t])))

         :else
         acc)))
   []
   events))

(defn interval-start [x]
  (or (first x) js/Number.MIN_SAFE_INTEGER))

(defn interval-end [x]
  (or (second x) js/Number.MAX_SAFE_INTEGER))

(defn interval-intersects-start [x y]
  (and (<= (interval-start x) (interval-start y))
       (> (interval-end x) (interval-start y))))

(defn interval-intersects-end [x y]
  (and (< (interval-start x) (interval-end y))
       (>= (interval-end x) (interval-end y))))

(defn interval-contains [x y]
  (and (<= (interval-start x) (interval-start y))
       (>= (interval-end x) (interval-end y))))

(defn interval-intersection [x y]
  (cond
    (interval-contains x y)
    y

    (interval-contains y x)
    x

    (interval-intersects-start y x)
    [(interval-start x) (interval-end y)]

    (interval-intersects-end y x)
    [(interval-start y) (interval-end x)]

    :else
    nil))

;; TODO send in periods og player-intervals
(defn player-time [events cur-time player]
  (let [periods (->intervals events :match :started :stopped)
        player-intervals (->intervals events player :on :off)]
    (max (->> player-intervals
              (mapcat (fn [i]
                        (filter identity (map (fn [p] (interval-intersection i p)) periods))))
              (reduce (fn [acc i]
                        (match i
                          [start end]
                          (+ acc (- end start))

                          [start]
                          (+ acc (- cur-time start))

                          :else
                          acc))
                      0))
         0)))

(defn mins [ms]
  (math/floor (/ ms (* 1000 60))))

(defn secs [ms]
  (math/floor (/ (rem (rem ms 3600000) 60000) 1000)))

(defn format-time [ms]
  (let [ms (max ms 0)
        m (mins ms)
        s (secs ms)]
    (str (if (< m 10) (str "0" m) m)
         ":"
         (if (< s 10) (str "0" s) s))))

(defn players-with-time [events cur-time players]
  (map (juxt identity #(mins (player-time events cur-time %))) players))

(defn match-clock [events cur-time]
  (let [i (last (->intervals events :match :started :stopped))]
    (match i
      [from to]
      (- to from)

      [from]
      (- cur-time from)

      :else
      0)))
