(defproject riadvargas/clj-embedded-kafka "0.1.0-SNAPSHOT"
  :description "Kafka and Zookeeper embedded for unit testing"
  :url "https://github.com/RiadVargas/clj-embedded-kafka"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.apache.kafka/kafka_2.11 "2.4.0"]
                 [org.clojure/tools.logging "0.6.0"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [commons-io/commons-io "2.4"]
                 [clojure-interop/apache-commons-io "1.0.0"]
                 [com.stuartsierra/component "0.4.0"]]
  :repl-options {:init-ns clj-embedded-kafka.core})
