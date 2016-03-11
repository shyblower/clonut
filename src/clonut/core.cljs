(ns clonut.core
  (:require [cljs.core.async
             :refer [chan pipe put! close!]
             :as async]
            [shyblower.helpers :refer [swapped-partial]]))

(defn- mutate [action state]
  (or (action state) state))

(defn clonut [init-state & {:keys [action-buffer-size init-state middleware]
                            :or {action-buffer-size 100
                                 init-state {}
                                 middleware identity}}]
  (let [state-channel (chan)
        action-channel (chan action-buffer-size)]
    (pipe (async/map mutate [action-channel state-channel]) state-channel)
    (put! state-channel init-state)
    {:state-channel state-channel
     :action-channel action-channel
     :middleware middleware}))

(defn shutdown! [{:keys [state-channel action-channel]}]
  (close! action-channel)
  (close! state-channel))

(defn react! [{:keys [action-channel middleware]} action-fn]
  (put! action-channel (middleware action-fn)))

(defn post-middleware [post-fn]
  (fn [action-fn]
    (when-let [new-state (action-fn state)]
      (post-fn new-state))))

(defn pre-middleware [pre-fn]
  (fn [action-fn]
    (when-let [new-state (pre-fn state)]
      (action-fn new-state))))

(defn action-fn [f & args]
  (apply swapped-partial f args))

(defn action [f]
  (fn [& args] (react! (apply action-fn f args))))
