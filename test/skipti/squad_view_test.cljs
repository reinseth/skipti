(ns skipti.squad-view-test
  (:require [skipti.squad-view :as sut]
            [cljs.test :refer-macros [deftest is testing]]))

(deftest prep-squad-view-test
  (testing "add player action"
    (is (= (:add-player (sut/prep-squad-view {:squad/input "Margaret"}))
           [[:assoc-in [:squad/players] #{"Margaret"}]
            [:assoc-in [:squad/input] ""]]))
    (is (= (:add-player (sut/prep-squad-view {:squad/players #{"Jenny" "Margaret"}
                                              :squad/input "Margaret"}))
           [[:assoc-in [:squad/players] #{"Jenny" "Margaret"}]
            [:assoc-in [:squad/input] ""]])))

  (testing "add player action for empty input"
    (is (= (:add-player (sut/prep-squad-view {}))
           []))
    (is (= (:add-player (sut/prep-squad-view {:squad/input " "}))
           [])))

  (testing "toggle player selection actions"
    (let [actions (->> (sut/prep-squad-view
                        {:squad/players #{"Anton" "Peter"}
                         :match/players #{"Peter"}})
                       :players
                       (map (juxt :key :on-click)))]
      (is (= actions
             [["Anton" [[:assoc-in [:match/players] #{"Anton" "Peter"}]]]
              ["Peter" [[:assoc-in [:match/players] #{}]]]]))))

  (testing "new match action"
    (is (nil? (:new-match (sut/prep-squad-view {}))))
    (is (= (:new-match (sut/prep-squad-view {:match/players #{"Jenny"}}))
           [[:assoc-in [:view] :match]
            [:assoc-in [:match/events] []]])))

  (testing "back to match"
    (is (nil? (:back-to-match (sut/prep-squad-view {}))))
    (is (= (:back-to-match (sut/prep-squad-view {:match/events []}))
           [[:assoc-in [:view] :match]]))))
