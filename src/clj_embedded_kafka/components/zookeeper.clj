(ns clj-embedded-kafka.components.zookeeper
  (:require [clj-embedded-kafka.misc :as misc]
            [clojure.tools.logging :as logging]
            [com.stuartsierra.component :as component])
  (:import java.net.InetSocketAddress
           [org.apache.zookeeper.server NIOServerCnxnFactory ZooKeeperServer]))

(defn- zookeeper-config [overrides]
  (merge {:data-dir  (misc/tmp-dir-path "zookeeper")
          :log-dir   (misc/tmp-dir-path "zookeeper")
          :tick-time 3000
          :port      2181
          :max-cc    10}
         overrides))

(defn- start-zookeeper! [zk-config]
   (let [{:keys [data-dir log-dir tick-time port max-cc]} zk-config
         zk-server (ZooKeeperServer. data-dir log-dir tick-time)]
     (try
       (doto (NIOServerCnxnFactory.)
         (.configure (InetSocketAddress. port) max-cc)
         (.startup zk-server))
       (catch Exception e
         (logging/error e "Something went wrong when trying to start ZooKeeper...")))))

(defrecord Zookeeper [overrides]
  component/Lifecycle
  (start [this]
    (let [zk-config (zookeeper-config overrides)]
      (assoc this
             :server (start-zookeeper! zk-config)
             :config zk-config)))
  (stop [{:keys [server] :as this}]
    (do (.shutdown server)
        (misc/clean-tmp-path! "zookeeper")
        (dissoc this :server :config))))

(defn new-zookeeper
  ([] (new-zookeeper {}))
  ([overrides]
   (map->Zookeeper {:overrides overrides})))
