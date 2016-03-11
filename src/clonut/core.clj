(ns clonut.core)

(defmacro defpostmiddleware [mw-name args state oldstate & body]
  `(defn ~mw-name [~@args]
     (post-middleware (fn [~state ~oldstate] ~@body))))

(defmacro defhandlers [handlers-name clonut! handlers]
  `(def ~handlers-name (handlers ~clonut! ~handlers)))
