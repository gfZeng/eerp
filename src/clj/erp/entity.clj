(ns erp.entity
  (:require [clojure.set :as set :refer (rename-keys)]
            [datomic.api :as d]
            [erp.db :as db :refer (conn)]))

(defn by-ident [ident]
  (d/entity (d/db @conn) ident))

(defn attr [ident attr]
  (get ident (by-ident ident)))

(defn add-user [user]
  (let [user (-> user
                 (select-keys [:user/name :user/email :user/password])
                 (assoc :db/id (d/tempid :user)))]
    {:db/id (-> @(d/transact @conn [user])
                :tempids
                :first
                :val)}))
