(ns clonut.core)

(defmacro defmiddleware [mw-name args action-fn & body]
  `(defn ~mw-name [~@args]
     (middleware (fn [~action-fn] ~@body))))

(defmacro defpremiddleware [mw-name args state & body]
  `(defn ~mw-name [~@args]
     (post-middleware (fn [~state] ~@body))))

(defmacro defpostmiddleware [mw-name args state oldstate & body]
  `(defn ~mw-name [~@args]
     (post-middleware (fn [~state ~oldstate] ~@body))))

(defmacro defactions [actions-name actions-map]
  `(def ~actions-name (fn
                         ([clonut!#] (actions clonut!# ~actions-map))
                         ([clonut!# middleware#] (actions clonut!# middleware# ~actions-map)))))
