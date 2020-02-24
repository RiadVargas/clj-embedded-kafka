(ns clj-embedded-kafka.core
  (:require [clojure.tools.logging :as logging]
            [com.stuartsierra.component :as component]
            [clj-embedded-kafka.components.zookeeper :as zookeeper-com]
            [clj-embedded-kafka.components.kafka :as kafka-com]))

(defmacro with-embedded-kafka
  "Start and kill Zookeeper and Kafka after evaluating body"
  [zk-overrides kafka-overrides & body]
  `(let [system-map# (component/system-map
                      :zookeeper (zookeeper-com/new-zookeeper ~zk-overrides)
                      :kafka     (component/using (kafka-com/new-kafka ~kafka-overrides) [:zookeeper]))
         system# (component/start-system system-map#)]
     (try
       ~@body
       (catch Exception e#
         (logging/error e#))
       (finally
         (component/stop-system system#)))))
