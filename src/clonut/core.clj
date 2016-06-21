(ns clonut.core)

(defmacro defpostmiddleware [mw-name args state oldstate & body]
  `(defn ~mw-name [~@args]
     (post-middleware (fn [~state ~oldstate] ~@body))))

(defmacro defactions [actions-name actions-map]
  `(def ~actions-name (fn
                         ([clonut!#] (actions clonut!# ~actions-map))
                         ([clonut!# middleware#] (actions clonut!# middleware# ~actions-map)))))
