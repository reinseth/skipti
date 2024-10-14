(ns skipti.match-view-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [skipti.match-view :as sut]))

(deftest prep-match-view-test

  (testing "toggle - start match"
    (let [result (sut/prep-match-view {})]
      (is (= (:toggle result)
             [[:conj-in-v [:match/events] [:match :started :current-time]]]))
      (is (= (:toggle-label result)
             [:i18n :match/start]))
      (is (= (:toggle-variant result)
             :btn-primary))))

  (testing "toggle - pause match"
    (let [result (sut/prep-match-view {:match/events [[:match :started 1]]})]
      (is (= (:toggle result)
             [[:conj-in-v [:match/events] [:match :stopped :current-time]]]))
      (is (= (:toggle-label result)
             [:i18n :match/pause]))
      (is (= (:toggle-variant result)
             :btn-secondary))))

  (testing "toggle - new period"
    (let [result (sut/prep-match-view {:match/events [[:match :started 1]
                                                      [:match :stopped 2]]})]
      (is (= (:toggle result)
             [[:conj-in-v [:match/events] [:match :started :current-time]]]))
      (is (= (:toggle-label result)
             [:i18n :match/new-period]))
      (is (= (:toggle-variant result)
             :btn-primary)))))
