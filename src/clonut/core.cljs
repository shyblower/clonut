(ns clonut.core
  (:require [cljs.core.async
             :refer [chan pipe put! close! <! >! poll!]
             :as async])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn- mutate [action state]
  (or (action state) state))

(defn clonut [& {:keys [action-buffer-size init-state]
                 :or {action-buffer-size 100
                      init-state {}}}]
  (let [state-channel (chan)
        action-channel (chan action-buffer-size)
        result-channel (chan 1)]
    (pipe result-channel state-channel)
    (go-loop []
      (>! result-channel
          (loop [state (mutate (<! action-channel) (<! state-channel))]
            (if-let [next-action (poll! action-channel)]
              (recur (mutate next-action state))
              state)))
      (recur))
    (put! state-channel init-state)
    (fn [action-fn] (put! action-channel action-fn))))

(defn middleware [middleware-fn]
  (fn [clonut!]
    (fn [action-fn]
      (clonut! (middleware-fn action-fn)))))

(defn pre-middleware [middleware-fn]
  (middleware
    (fn [action-fn]
      (fn [state]
        (action-fn (middleware-fn state))))))

(defn post-middleware [middleware-fn]
  (middleware
    (fn [action-fn]
      (fn [state]
        (middleware-fn (action-fn state) state)))))

(defn action-fn [f & args]
  (fn [state] (apply f state args)))

(defn action
  ([clonut! f]
   (fn [& args] (clonut! (apply action-fn f args))))
  ([clonut! middleware f]
   (fn [& args] (clonut! (middleware (apply action-fn f args))))))

(defn ^private actions_ [curried-action actions-map]
  (reduce-kv #(assoc %1 %2 (curried-action %3)) {} actions-map))

(defn actions
  ([clonut! actions-map]
   (actions_ #(action clonut! %) actions-map))
  ([clonut! middleware actions-map]
   (actions_ #(action clonut! middleware %) actions-map)))
