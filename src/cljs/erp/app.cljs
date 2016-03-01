(ns erp.app
  (:require cljsjs.bootstrap
            cljsjs.jquery
            [erp.session :as session :refer-macros (with-login)]
            [rum.core :as rum :refer-macros (defc)]
            [secretary.core :as secretary :refer-macros (defroute)]
            [tools.util :as utl]
            [tools.history :as h :refer-macros (on-navigate)]

            [erp.util :as util :refer (api-path field) :refer-macros (form)]

            [erp.session :as session :refer ($session) :refer-macros (with-login)]
            [clojure.string :as string]))

(secretary/set-config! :prefix "#!")

(def $main-view (atom (constantly [:div "You are lost ..."])))


(defc login-view []
  (form
   $session
   {:class "login-form"}
   [:div.form-group
    [:label {:for "email"} "Email"]
    (field [:user :user/email]
           {:id "email" :class "form-control"
            :placeholder "Email"})]
   [:div.form-group
    [:label {:for "password"} "Password"]
    (field [:user :user/password]
           {:id "password" :class "form-control"
            :type "password"
            :placeholder "Password"})]
   [:div.form-group
    [:a.btn.btn-primary
     {:type "submit"
      :on-click #(do (*flush-in*) (session/with-login))} "Register"]]))


(defroute "/" []
  (reset! $main-view (fn [] [:div "damn shitoff"])))

(defroute login-uri "/sessions/new" []
  (reset! $main-view login-view))

(defc main-view  < rum/reactive []
  [:.main-view
   ((rum/react $main-view))])

(defc header []
  [:header {:class "navbar navbar-inverse navbar-fixed-top", :role "navigation"}
   [:div.container
    [:div {:class "navbar-header"}
     [:a {:class "navbar-brand", :href "#"}
      [:img {:alt "ConSerp", :src ""}]]]
    [:ul {:class "nav navbar-nav"}
     [:li
      [:a {:href "#!resources/0/!list"} "表管理"]]]
    [:ul {:class "nav navbar-nav navbar-right"}
     (if-let [user (session/get :user)]
       [:li.dropdown
        [:a.dropdown-toggle
         {:href "#" :data-toggle "dropdown"
          :role "button" :aria-haspopup "true"}
         (or (:nickname (session/get :user))
             (:username (session/get :user)))
         [:span.fa.fa-user.fa-lg.btn]]
        [:ul.dropdown-menu
         [:li [:a "设置"]]
         [:li [:a {:href (login-uri) :data-method "DELETE"} "退出"]]]]
       [:li [:a.fa.fa-user.fa-lg.btn {:href (login-uri)}]])]]])

(defc canvas []
  [:.canvas
   (header)
   (main-view)])

(defn render
  []
  (rum/mount (canvas) (utl/q "#canvas")))

(defn init []
  (enable-console-print!)
  (secretary/dispatch! js/location.hash)
  (on-navigate (secretary/dispatch! js/location.hash))
  (render))
