(ns clj-embedded-kafka.components.kafka
  (:require [clj-embedded-kafka.misc :as misc]
            [clojure.string :as str]
            [com.stuartsierra.component :as component])
  (:import java.util.Properties
           kafka.server.KafkaServerStartable))

(defn- default-config [{{:keys [port]} :config}]
  {:message-send-max-retries         "5"
   :log-dir                          (-> "kafka" misc/tmp-dir-path .getPath)
   :group-id                         "consumer"
   :zookeeper-connect                (format "127.0.0.1:%d" (or port 2181))
   :auto-create-topics-enable        "true"
   :retry-backoff-ms                 "500"
   :log-flush-interval-messages      "1"
   :bootstrap-servers                "127.0.0.1:9999"
   :offsets-topic-replication-factor "1"
   :auto-offset-reset                "earliest"
   :auto-commit-enable               "false"
   :listeners                        "PLAINTEXT://127.0.0.1:9999"
   :max-poll-records                 "1"
   :broker-id                        "0"
   :zookeeper-port                   (str (or port 2181))})

(defn- start-kafka! [kafka-config]
  (let [server-properties (Properties.)
        config (misc/edn->kafka-config kafka-config)]
    (run! (fn [[k v]] (.setProperty server-properties k v)) config)
    (doto (KafkaServerStartable/fromProps server-properties)
      (.startup))))

(defrecord Kafka [zookeeper overrides]
  component/Lifecycle
  (start [this]
    (let [default-config (default-config (:zookeeper this))
          config (merge default-config overrides)]
      (assoc this
             :server (start-kafka! config)
             :config default-config)))
  (stop [{:keys [server] :as this}]
    (do (.shutdown server)
        (.awaitShutdown server)
        (misc/clean-tmp-path! "kafka")
        (dissoc this :server :config))))

(defn new-kafka
  ([] (new-kafka {}))
  ([overrides]
   (map->Kafka {:overrides overrides})))
