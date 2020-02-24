(ns clj-embedded-kafka.misc-test
  (:require [clj-embedded-kafka.misc :as misc]
            [clojure.test :refer :all])
  (:import java.lang.System))

(deftest tmp-dir-path
  (with-redefs [misc/system-tmpdir (fn [] "/garbage/")
                misc/now->number (fn [] 100)]
    (testing "it points to the right temporary directory"
      (is (= (-> "zookeeper" misc/tmp-dir-path .getPath)
             "/garbage/clj-kafka-unit/zookeeper/100")))))

(deftest edn->kafka-config
  (testing "transform dashed-keyword into dotted-string"
    (is (= (misc/edn->kafka-config {:zookeeper-connect "127.0.0.1:2181"})
           {"zookeeper.connect" "127.0.0.1:2181"}))))
