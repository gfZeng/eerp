(ns erp.util
  (:require [clojure.string :as string]
            [clojure.data :as data]
            #?@(:clj
                [[jayq.macros :refer (let-ajax)]])))


(def ^:dynamic *form-opts* nil)

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

#?(:clj
   (do
     (defmacro let-api [bindings & body]
       `(let-ajax ~(vec (map-indexed (fn [i x]
                                       (if (zero? (rem i 2))
                                         x
                                         (update x :url api-path)))
                                     bindings))
          ~@body))

     (defmacro form [opts & fields]
       `(binding [*form-opts* (let [opts# ~opts]
                                (if (map? opts#)
                                  opts#
                                  {:for opts#}))]
          (add-watch (:for *form-opts*) :update-fields-values
                     (let [fields# (:>fields *form-opts*)]
                       (fn [r# k# ov# nv#]
                         (let [diff-v# (first (data/diff nv# ov#))]
                           (doseq [f# fields#]
                             (when-let [value# (get-in diff-v# (:for f#))]
                               (-> (js/document.getElementById (:id f#))
                                   .-value
                                   (set! ((:> f#) value#)))))))))
          [:form ~@fields])))

   :cljs
   (do
     (let [counter (atom -1)]
       (defn field-id []
         (str "field-" (swap! counter inc))))

     (defn field
       ([opts]
        (field opts nil))
       ([opts attrs]
        (let [opts (if (map? opts) opts {:for opts})]
          (let [data (:for *form-opts*)
                attrs (-> attrs
                          (assoc
                           :id  (or (:id attrs) (field-id))
                           :value (get-in @data (:for opts))
                           :on-change
                           (fn [e]
                             (swap! data assoc-in (:for opts)
                                    ((or (:< opts)
                                         identity)
                                     (.. e -target -value)))
                             (println ">>>>>>" @data))))]
            (when (:> opts)
              (swap! data update-in [:>fields] conj (assoc opts :id (:id attrs))))
            [:input attrs]))))))
