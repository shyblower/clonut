(ns clonut.core)

(defmacro defpostmiddleware [mw-name args state oldstate & body]
  `(defn ~mw-name [~@args]
     (post-middleware (fn [~state ~oldstate] ~@body))))

(defmacro defhandlers [handlers-name handlers]
  `(def ~handlers-name (fn
                         ([clonut!#] (handlers clonut!# ~handlers))
                         ([clonut!# middleware#] (handlers clonut!# middleware# ~handlers)))))
