(ns erp.session
  (:refer-clojure :exclude [get])
  (:require-macros [jayq.macros :refer (let-ajax)])
  (:require jayq.core

            [erp.util :refer (api-path)]))


(def *session* (atom {:user {:nickname "isaac"}}))

(defn get
  ([key]
   (clojure.core/get @*session* key))
  ([key not-found]
   (clojure.core/get @*session* key not-found)))

(defn login-do [did-login-fn]
  (let-ajax [session (api-path "/sessions/new")]
    (reset! *session* session)
    did-login-fn))
