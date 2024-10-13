(ns skipti.edit-squad-view-test
  (:require [skipti.edit-squad-view :as sut]
            [cljs.test :refer-macros [deftest is testing]]))

(deftest prep-edit-squad-view-test
  (testing "remove player"
    (let [remove-actions (->> (sut/prep-edit-squad-view {:squad/players #{"Maggie" "Inga"}
                                                         :match/players #{"Maggie"}})
                              :players
                              (map (juxt :key :icon-action)))]
      (is (= remove-actions
             [["Inga" [[:assoc-in [:squad/players] #{"Maggie"}]
                       [:assoc-in [:match/players] #{"Maggie"}]]]
              ["Maggie" [[:assoc-in [:squad/players] #{"Inga"}]
                         [:assoc-in [:match/players] #{}]]]])))))
