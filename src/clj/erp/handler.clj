(ns erp.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [erp.ctrl :as ctrl]
            [erp.util :as util]
            [erp.view :as view]
            [noir.util.middleware :refer (app-handler)]
            [ring.adapter.jetty :refer (run-jetty)]
            [ring.middleware.defaults :refer (site-defaults)]
            [ring.middleware.format :refer (wrap-restful-format)]
            [ring.middleware.session.cookie :refer (cookie-store)]
            [taoensso.timbre :refer (log info debug warn)]))


(defonce SERVER (atom nil))

(defroutes page-routes
  (GET "/" _ (view/index-page))
  (route/resources "/" {:root nil}))

(defroutes public-api-routes
  (POST "/sessions" _ ctrl/login)
  (POST "/users" _ ctrl/register))

(require '[tools.ring.helper :refer (wrap-debug)])
(def app
  (app-handler
   [page-routes (context (util/api-path) _ (routes public-api-routes))]
   :session-options {:cookie-name "erp"
                     :store (cookie-store)}
   :ring-defaults (dissoc-in site-defaults [:security :anti-forgery])
   :middleware [wrap-debug #(wrap-restful-format % :formats [:edn :json-kw])]))

(defn run-server [& {:as opts}]
  (when @SERVER
    (info "stopping server ...")
    (.stop @SERVER))
  (reset! SERVER (run-jetty #'app opts)))
