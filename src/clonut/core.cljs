(ns clonut.core
  (:require [cljs.core.async
             :refer [chan mult tap put!]
             :as async]))

(defn- mutate [action state]
  (action state))

(defn with-state [func & {:keys [action-buffer-size]
                          :or {action-buffer-size 100}}]
  (let [action-channel (chan action-buffer-size)
        state-channel (chan)
        new-state-channel (async/map mutate [action-channel state-channel])
        state-mult (mult new-state-channel)]
    (tap state-mult state-channel)
    (put! new-state-channel
          (func action-channel state-mult))))

(defn action-fn [func & args]
  (fn [state] (apply func state args)))

(defn action [action-channel func]
  (fn [& args] (put! action-channel (apply action-fn func args))))

(defn act-on-jsevent [action-channel handler]
  (fn [& args] (apply (action action-channel handler) args) nil))
