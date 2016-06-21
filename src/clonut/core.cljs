(ns clonut.core
  (:require [cljs.core.async
             :refer [chan pipe put! close!]
             :as async]))

(defn- mutate [action state]
  (or (action state) state))

(defn clonut [& {:keys [action-buffer-size init-state]
                 :or {action-buffer-size 100
                      init-state {}}}]
  (let [state-channel (chan)
        action-channel (chan action-buffer-size)]
    (pipe (async/map mutate [action-channel state-channel]) state-channel)
    (put! state-channel init-state)
    (fn [action-fn] (put! action-channel action-fn))))

(defn add-middleware [clonut! middleware]
  (fn [action-fn]
    (clonut! (middleware action-fn))))

(defn post-middleware [post-fn]
  (fn [action-fn]
    (fn [state]
      (when-let [new-state (action-fn state)]
        (post-fn new-state state)))))

(defn pre-middleware [pre-fn]
  (fn [action-fn]
    (fn [state]
      (when-let [new-state (pre-fn state)]
        (action-fn new-state)))))

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
