(ns erp.entity
  (:require [clojure.set :as set :refer (rename-keys)]
            [datomic.api :as d]
            [erp.db :as db :refer (conn)]))

(defn by-ident [ident]
  (d/entity (d/db @conn) ident))

(defn attr [ident attr]
  (get ident (by-ident ident)))

(defn add-user [user]
  @(d/transact (d/db @conn)
               [(select-keys user [:db.ref/name :user/email :user/password])]))
