(ns clonut.core)

(defmacro action! [[<action> state-symbol] & body]
  `(cljs.core.async/put! ~<action> (fn [~state-symbol] ~@body)))

(defmacro def-action [fn-symbol args bindings & body]
  `(defn ~fn-symbol ~args
     (action! ~bindings ~@body)))
