(ns erp.view
  (:require [noir.session :as session]
            [hiccup.core :refer (html)]
            [hiccup.def :refer (defhtml)]
            [hiccup.page :refer (doctype include-js include-css)]
            [hiccup.element :refer (javascript-tag link-to)]

            [erp.util :as util :refer (static-path api-path)]))


(defhtml index-page []
  (doctype :html5)
  [:html
   [:head
    [:title "ConSerp"]
    (apply include-css
           (map static-path ["/css/bootstrap.min.css"
                             "/css/bootstrap-theme.min.css"
                             "/css/font-awesome.min.css"
                             "/css/sass.css"]))]
   [:body
    #_[:div.canvas
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
            [:span.caret]]
           [:ul.dropdown-menu
            [:li [:a "设置"]]
            [:li [:a {:href "/api/v1/sessions" :data-method "DELETE"} "退出"]]]]
          [:li [:i.fa.fa-user.fa-lg.btn]])]]]
     [:section
      [:aside "Siderbar"]
      [:article "Content"]]]
    (apply include-js (map static-path ["/js/app.js"]))]])
