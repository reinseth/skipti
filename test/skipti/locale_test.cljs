(ns skipti.locale-test
  (:require [skipti.locale :as sut]
            [cljs.test :refer-macros [deftest is]]))

(deftest normlize-langs-test
  (is (= (sut/normalize-langs ["no" "nb" "en" "en-US"])
         [:nb :nb :en :en])))

