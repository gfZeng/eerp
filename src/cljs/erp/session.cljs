(ns erp.session
  (:refer-clojure :exclude [get])
  (:require jayq.core
            [erp.util :refer-macros (let-api)]))


(def $session (atom nil))

(defn get
  ([key]
   (clojure.core/get @$session key))
  ([key not-found]
   (clojure.core/get @$session key not-found)))

(defn login-do [did-login-fn]
  (let-api [session {:url "sessions"
                     :data {:user (:user @$session)}
                     :contentType "application/edn"
                     :type "POST"}]
    (reset! $session (merge @$session session))
    did-login-fn))
