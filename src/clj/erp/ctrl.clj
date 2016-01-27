(ns erp.ctrl
  (:require [tools.compojure :refer (defapi)]

            [erp.entity :as entity]))


(defapi login
  [{:as user
    :keys [user/name user/password]}]
  (clojure.pprint/pprint *req*)
  (println ">>>>>>>>" name password))

(defapi register
  [user]
  (entity/add-user user))
