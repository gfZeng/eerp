
(set-env!
 :source-paths    #{"sass" "src/cljs" "src/clj"}
 :resource-paths  #{"resources"}
 :target-path     "target"
 :dependencies '[[adzerk/boot-cljs          "1.7.228-1"   :scope "test"]
                 [mathias/boot-sassc "0.1.5" :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.0"      :scope "test"]
                 [adzerk/boot-reload        "0.4.5"      :scope "test"]
                 [pandeiro/boot-http        "0.7.1-SNAPSHOT"      :scope "test"]
                 [com.cemerick/piggieback "0.2.1" :scope "test"]     ;; needed by bREPL
                 [weasel "0.7.0" :scope "test"]                      ;; needed by bREPL
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"]    ;; needed by bREPL

                 ;;; clj
                 [org.clojure/core.incubator "0.1.3"]
                 [clj-util "1.0.0"]
                 [pandect  "0.5.4"]
                 [org.clojure/core.async "0.2.374"]
                 [com.taoensso/timbre "4.2.1"]
                 [clj-time "0.11.0"]
                 [mount "0.1.8"]
                 [com.taoensso/carmine "2.12.2"]

                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-anti-forgery "1.0.0"]
                 [ring-middleware-format "0.7.0"]
                 [liberator "0.13"]
                 [lib-noir "0.9.9"]
                 [hiccup "1.0.5"]

                 ;;; datomic
                 [com.datomic/datomic-pro "0.9.5344"]
                 [org.postgresql/postgresql "9.4.1207"]

                 ;;; cljs
                 [org.clojure/clojurescript "1.7.228"]
                 [datascript "0.15.0"]
                 [rum "0.6.0"]
                 [secretary "1.2.3"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [cljsjs/jquery "2.1.4-0"]
                 [cljsjs/bootstrap "3.3.6-0"]
                 [jayq "2.5.4"]

                 ;;; css
                 [org.martinklepsch/boot-garden "1.3.0-0" :scope "test"]
                 ])
(require
 '[boot.task :refer :all]
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[org.martinklepsch.boot-garden :refer [garden]]
 '[mathias.boot-sassc  :refer [sass]])

(boot.core/load-data-readers!)

(deftask build []
  (comp (speak)
     (cljs)
     #_(garden :styles-var 'erp.styles/screen
               :output-to "css/garden.css")
     (sass :output-dir "css")
     (target)))

(deftask run []
  (comp (server :method 'erp.handler/run-server :port 3000)
     (watch)
     (cljs-repl)
     (reload)
     (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                 garden {:pretty-print false}
                 sass   {:output-style "compressed"})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none :source-map true}
                 reload {:on-jsload 'erp.app/init}
                 sass   {:line-numbers true
                         :source-maps  true})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
     (run)))


