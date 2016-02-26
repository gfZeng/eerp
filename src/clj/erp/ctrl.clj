(ns erp.ctrl
  (:require [tools.compojure :refer (defapi dispatch)]

            [erp.entity :as entity]

            [pandect.algo.md5 :refer (md5)]))


(def ERROR-MSG
  {:password {:incrorrect "password incrorrect"
              :invalid-character "password include invalid character"}})

(defn fail [keys]
  (throw
   (ex-info (get-in ERROR-MSG keys) {})))

(defapi login
  [{:as user
    :keys [user/email user/password]}]
  (let [u (entity/by-ident [:user/email email])]
    (if (= (md5 password) (:user/password u))
      {:user (select-keys u [:db/id :user/email :user/name])}
      (fail [:password :incrorrect]))))

(defapi register
  [user]
  (entity/add-user
   (update user :user/password md5))
  (dispatch login :user user))
