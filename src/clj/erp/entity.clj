(ns erp.entity
  (:require [clojure.set :as set :refer (rename-keys)]
            [datomic.api :as d]
            [erp.db :as db :refer (conn)]))

(defn add-user [user]
  (let [user (-> user
                 (rename-keys {:name :user/name
                               :email :user/email
                               :password :user/password})
                 (select-keys [:user/name :user/email :user/password]))]
    @(d/transact (d/db @conn) [user])))
