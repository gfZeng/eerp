(ns erp.util
  (:require [clojure.string :as string]))

(defn api-path
  ([] "/api/v1")
  ([& subs]
   (string/replace
    (->> subs
         (map name)
         (string/join "/")
         (str (api-path) "/"))
    (re-pattern (str "(" (api-path) "/){2}"))
    "$1")))

(def static-path identity)
