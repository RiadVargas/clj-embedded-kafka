(ns clj-embedded-kafka.core-test
  (:require [clojure.test :refer :all]
            [clj-embedded-kafka.core :as core]
            [clj-embedded-kafka.misc :as misc])
  (:import org.apache.kafka.clients.consumer.KafkaConsumer
           [org.apache.kafka.clients.producer KafkaProducer ProducerRecord]
           [org.apache.kafka.common.serialization StringDeserializer StringSerializer]))

(def ^:constant producer-keys [:value-serializer :key-serializer])
(def ^:constant consumer-keys [:key-deserializer :value-deserializer
                               :auto-offset-reset :group-id])

(def ^:private default-client-config
  {:value-serializer   StringSerializer
   :key-serializer     StringSerializer
   :value-deserializer StringDeserializer
   :key-deserializer   StringDeserializer
   :auto-offset-reset  "earliest"
   :group-id           "clj-embedded"})

(defn- create-consumer! [subscription-regex bootstrap-server]
  (let [consumer-config (-> default-client-config
                            (select-keys consumer-keys)
                            (merge {:bootstrap-servers bootstrap-server}))]
    (doto (-> consumer-config misc/edn->kafka-config KafkaConsumer.)
      (.subscribe [subscription-regex]))))

(defn- create-producer! [bootstrap-server]
  (-> default-client-config
      (select-keys producer-keys)
      (merge {:bootstrap-servers bootstrap-server})
      misc/edn->kafka-config
      KafkaProducer.))

(deftest embedded-kafka
  (testing "with default configs"
    (core/with-embedded-kafka {} {}
      (let [producer (create-producer! "127.0.0.1:9999")
            consumer (create-consumer! "topic" "127.0.0.1:9999")]
        (try
          (is (= ["42" "21"]
                 (do (.poll consumer 100)
                     (.send producer (ProducerRecord. "topic" "42"))
                     (.send producer (ProducerRecord. "topic" "21"))
                     (map #(.value %) (.poll consumer 100)))))
          (finally
            (.close producer)
            (.close consumer)))))))
