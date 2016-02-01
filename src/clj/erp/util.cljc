(ns erp.util
  (:require [clojure.string :as string]))

(defn api-path
  ([] "/api/v1")
  ([& subs]
   (->
    (->> subs
         (map name)
         (string/join "/")
         (str (api-path) "/"))
    (string/replace
     (re-pattern (str "(" (api-path) "/){2}"))
     "$1")
    (string/replace
     "//" "/"))))

(def static-path identity)
