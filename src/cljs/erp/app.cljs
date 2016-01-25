(ns erp.app
  (:require cljsjs.jquery
            cljsjs.bootstrap
            [rum.core :as rum :refer-macros (defc)]

            [erp.session :as session]))

(defc main-view []
  )

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
         [:li [:a {:href "/api/v1/sessions" :data-method "DELETE"} "退出"]]]]
       [:li [:a.fa.fa-user.fa-lg.btn]])]]])

(defc canvas []
  [:.canvas
   (header)
   (main-view)])

(defn render
  []
  (rum/mount (canvas) js/document.body))

(defn init []
  (enable-console-print!)
  (render))
