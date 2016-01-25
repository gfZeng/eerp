(ns erp.db
  (:require [clojure.java.io :as io]
            [clojure.core.strint :refer (<<)]
            [datomic.api :as d]))

(def uri "datomic:dev://localhost:4334/erp")

(defonce conn
  (delay
   (try
     (println "init db ...")
     (if (d/create-database uri)
       (let [c (d/connect uri)]
         (doseq [tx-data (-> (io/resource "erp/schema.edn")
                             slurp read-string)]
           @(d/transact c tx-data))
         c)
       (d/connect uri))
     (catch Throwable t
       (.printStackTrace t)
       (d/delete-database uri)))))

(defn ent-id [entity]
  (cond
    (number? entity) entity
    (associative? entity)
      (:db/id entity)
    (or (vector? entity)
        (keyword? entity))
      (d/entid (d/db @conn) entity)
    :else (throw (IllegalArgumentException. (<< "Entity: ~{entity}")))) )

(defn transact
  ([tx-data] (transact nil tx-data))
  ([user tx-data]
   (let [user-id (ent-id user)]
     (d/transact
      @conn
      (cons {:db/id (d/tempid :db.part/tx)
             :tx/commiter user-id}
            tx-data)))))

;; (do (ns-unmap *ns* 'conn) @conn)
