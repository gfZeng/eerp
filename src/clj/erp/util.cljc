(ns erp.util
  (:require [clojure.string :as string]
            [clojure.data :as data]
            #?@(:clj
                [[jayq.macros :refer (let-ajax)]])))


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
       `(binding [*form-opts* (atom
                               (let [opts# ~opts]
                                 (if (instance? ~'cljs.core/Atom opts#)
                                   {:for opts#}
                                   opts#)))]
          (let [form-opts# *form-opts*
                ~'*form-data* (:for @form-opts#)
                ~'*flush-in* (fn
                               ([] (~'erp.util/form-flush-in @form-opts#))
                               ([f#] (~'erp.util/form-flush-in @form-opts# f#)))]
            (add-watch ~'*form-data* :update-fields-values
                       (fn [r# k# ov# nv#]
                         (let [diff-v# (first (data/diff nv# ov#))]
                           (doseq [f# (:>fields @form-opts#)]
                             (when-let [value# (get-in diff-v# (:for f#))]
                               (-> (js/document.getElementById (:id f#))
                                   .-value
                                   (set! ((or (:> f#) identity) value#))))))))
            [:form ~@fields]))))

   :cljs
   (do
     (def ^:dynamic *form-opts* nil)

     (defn form-flush-in
       ([form-opts]
        (run! #(form-flush-in form-opts %) (:>fields form-opts)))
       ([form-opts f]
        (swap! (:for form-opts) assoc-in (:for f)
               ((or (:< f) identity)
                (-> (:id f)
                    js/document.getElementById
                    .-value)))))

     (let [counter (atom -1)
           field-id (fn []
                      (str "field-" (swap! counter inc)))]
       (defn field
         ([opts]
          (field opts nil))
         ([opts attrs]
          (let [opts (if (map? opts) opts {:for opts})]
            (let [data (:for @*form-opts*)
                  auto-flush-in? (:auto-flush-in? opts (:auto-flush-in? @*form-opts*))
                  attrs (-> attrs
                            (assoc
                             :id  (or (:id attrs) (field-id))
                             :value (get-in @data (:for opts))
                             :on-change
                             (fn [e]
                               (when auto-flush-in?
                                 (swap! data assoc-in (:for opts)
                                        ((or (:< opts)
                                             identity)
                                         (.. e -target -value))))
                               (when-let [f (:on-change attrs)]
                                 (f e)))))]
              (swap! *form-opts* update :>fields conj
                     (assoc opts :id (:id attrs)))
              [:input attrs])))))))
