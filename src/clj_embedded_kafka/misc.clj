(ns clj-embedded-kafka.misc
  (:require [clojure.java.io :refer [file]]
            [clojure.string :as str])
  (:import org.apache.commons.io.FileUtils
           java.time.LocalDateTime))

(defn- system-tmpdir []
  (System/getProperty "java.io.tmpdir"))

(defn- now->number []
  (-> (LocalDateTime/now)
      hash
      (mod Integer/MAX_VALUE)))

(defn tmp-dir-path [kind]
  (->> [(system-tmpdir) "clj-kafka-unit" kind (now->number)]
       (str/join "/")
       file))

(defn clean-tmp-path! [kind]
  (let [root-tmp-dir (-> (system-tmpdir)
                         (str "/clj-kafka-unit/" kind)
                         file)]
    (FileUtils/deleteDirectory root-tmp-dir)))

(defn edn->kafka-config [config]
  (->> config
       (map (fn [[k v]] (vector (-> k name (str/replace "-" ".")) v)))
       (into {})))
