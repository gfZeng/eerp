(ns erp.session)

(defmacro with-login [& body]
  `(erp.session/login-do
    (fn [] ~@body)))
