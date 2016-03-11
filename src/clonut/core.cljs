(ns clonut.core
  (:require [cljs.core.async
             :refer [chan pipe put! close!]
             :as async]
            [shyblower.helpers :refer [swapped-partial]]))

(defn- mutate [action state]
  (or (action state) state))

(defn clonut [init-state & {:keys [action-buffer-size]
                             :or {action-buffer-size 100}}]
  (let [state-channel (chan)
        action-channel (chan action-buffer-size)]
    (pipe (async/map mutate [action-channel state-channel]) state-channel)
    (put! state-channel init-state)
    [state-channel action-channel]))

(defn shutdown! [[state-channel action-channel]]
  (close! action-channel)
  (close! state-channel))

(defn react! [[_ action-channel] action-fn]
  (put! action-channel action-fn))

(defn wrap-middleware
  ([middleware]
   (wrap-middleware middleware react!))
  ([middleware react-fn]
   (fn [clonut action-fn]
     (react-fn clonut
               (fn [state]
                 (middleware state action-fn))))))

(defn create-post-middleware [post-fn]
  (fn react-middleware [state action-fn]
    (when-let [new-state (action-fn state)]
      (post-fn new-state))))

(defn add-post-middleware
  ([post-fn]
   (wrap-middleware (create-post-middleware post-fn)))
  ([post-fn react-fn]
   (wrap-middleware (create-post-middleware post-fn) react-fn)))

(defn action-fn [f & args]
  (apply swapped-partial f args))

(defn action [react-fn f]
  (fn [& args] (react-fn (apply action-fn f args))))
