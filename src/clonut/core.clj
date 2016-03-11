(ns clonut.core)

(defmacro defpostmiddleware [fn-name args state oldstate & body]
  `(defn ~fn-name [~@args]
     (post-middleware (fn [~state ~oldstate] ~@body))))
