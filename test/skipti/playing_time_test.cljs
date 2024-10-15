(ns skipti.playing-time-test
  (:require [cljs.test :refer-macros [deftest is]]
            [skipti.playing-time :as sut]))

(deftest ->intervals-test
  (is (= (sut/->intervals
          [["Janne" :on 0]
           ["Janne" :off 1]
           ["Janne" :on 2]
           ["Janne" :off 3]
           [:match :started 1]]
          :match
          :stopped
          :started)
         [[nil 1]]))

  (is (= (sut/->intervals
          [["Janne" :on 0]
           ["Janne" :off 1]
           ["Janne" :on 2]
           ["Janne" :off 3]
           [:match :started 3]]
          "Janne"
          :on
          :off)
         [[0 1] [2 3]]))

  (is (= (sut/->intervals
          [["Janne" :on 0]
           ["Janne" :off 1]
           [:match :started 1]
           ["Janne" :on 2]
           ["Janne" :off 3]
           ["Janne" :on 2]
           [:match :stopped 9]]
          "Janne"
          :on
          :off)
         [[0 1] [2 3] [2]]))

  (is (= (sut/->intervals
          [["Janne" :on 0]
           ["Janne" :off 1]
           [:match :started 1]
           ["Janne" :on 2]
           ["Janne" :off 3]
           ["Janne" :on 2]
           [:match :stopped 9]]
          :match
          :stopped
          :started)
         [[nil 1] [9]])))

(deftest interval-intersection-start-test
  (is (false? (sut/interval-intersects-start [0 5] [5 15])))
  (is (true? (sut/interval-intersects-start [0 10] [5 15])))
  (is (true? (sut/interval-intersects-start [5 16] [5 15])))
  (is (true? (sut/interval-intersects-start [nil 15] [10 20])))
  (is (false? (sut/interval-intersects-start [11 21] [10 20]))))

(deftest interval-intersects-end-test
  (is (true? (sut/interval-intersects-end [5 15] [4 14])))
  (is (true? (sut/interval-intersects-end [3 15] [4 14])))
  (is (false? (sut/interval-intersects-end [4 13] [4 14])))
  (is (false? (sut/interval-intersects-end [14 15] [4 14]))))

(deftest interval-intersection-test
  (is (= (sut/interval-intersection [0 10] [5 15])
         [5 10]))
  (is (= (sut/interval-intersection [0 10] [-5 5])
         [0 5]))
  (is (= (sut/interval-intersection [0 10] [1 9])
         [1 9]))
  (is (= (sut/interval-intersection [0 nil] [1 nil])
         [1 nil])))

(deftest player-time-test
  (is (= (sut/player-time
          [["Janne" :on 0]
           [:match :started 1]
           ["Janne" :off 3]
           ["Janne" :on 4]
           [:match :stopped 9]]
          10
          "Janne")
         7))

  (is (= (sut/player-time
          [["Janne" :on 0]
           [:match :started 1]
           ["Janne" :off 3]
           ["Janne" :on 4]]
          10
          "Janne")
         8))

  (is (= (sut/player-time
          [["Janne" :on 0]
           [:match :started 1]
           ["Janne" :off 3]
           ["Janne" :on 4]
           [:match :stopped 5]
           [:match :started 6]
           ["Janne" :off 7]]
          10
          "Janne")
         4)))

(deftest format-time-test
  (is (= (sut/format-time 0)
         "00:00"))
  (is (= (sut/format-time 12321)
         "00:12"))
  (is (= (sut/format-time 105500)
         "01:45"))
  (is (= (sut/format-time (* 61 60000))
         "61:00")))

(deftest match-clock-test
  (is (= (sut/match-clock [[:match :started 0]]
                          40)
         40))
  (is (= (sut/match-clock [[:match :started 0]
                           [:match :stopped 45]]
                          50)
         45))
  (is (= (sut/match-clock [[:match :started 0]
                           [:match :stopped 45]
                           [:match :started 60]]
                          75)
         15)))
