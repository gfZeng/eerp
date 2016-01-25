(ns erp.handler
  (:require [ring.adapter.jetty :refer (run-jetty)]
            [ring.middleware.session.cookie :refer (cookie-store)]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [noir.util.middleware :refer (app-handler)]
            [taoensso.timbre :refer (log info debug warn)]

            [erp.view :as view]
            [erp.ctrl :as ctrl]))


(defonce SERVER (atom nil))

(defroutes page-routes
  (GET "/" _ (view/index-page))
  (route/resources "/" {:root nil}))

(defroutes public-api-routes
  (POST "/sessions/new" _ ctrl/login))

(def app
  (app-handler
   [page-routes]
   :session-options {:cookie-name "erp"
                     :store (cookie-store)}))

(defn run-server [& {:as opts}]
  (when @SERVER
    (info "stopping server ...")
    (.stop @SERVER))
  (reset! SERVER (run-jetty #'app opts)))
