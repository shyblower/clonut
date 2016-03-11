(ns clonut.core
  (:require [cljs.core.async
             :refer [chan pipe put! close!]
             :as async]))

(defn- mutate [action state]
  (or (action state) state))

(defn clonut [& {:keys [action-buffer-size init-state middleware]
                 :or {action-buffer-size 100
                      init-state {}}}]
  (let [state-channel (chan)
        action-channel (chan action-buffer-size)]
    (pipe (async/map mutate [action-channel state-channel]) state-channel)
    (put! state-channel init-state)
    (fn [action-fn] (put! action-channel action-fn))))

(defn wrap-middleware [clonut! middleware]
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

(defn action [f & args]
  (fn [state] (apply f state args)))

(defn handler [clonut! f]
  (fn [& args] (clonut! (apply action f args))))

(defn handlers [clonut! handlers]
  (let [handler #(handler clonut! %)]
    (reduce-kv #(assoc %1 %2 (handler %3)) {} handlers)))
