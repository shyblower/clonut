(ns clonut.core
  (:require [cljs.core.async :refer [chan sliding-buffer tap mult pipe put! <!]
                             :as async]
            [shyblower.helpers :refer [log-exception]]
            [cljs.pprint :refer [pprint]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn- act [action state]
  ; catch exceptions in action functions to make debugging easier
  (try (action state)
       (catch js/Error e
         (log-exception e)
         state)))

(defn- reactor [& {:keys [pre cmd-buf]}
                  :or {cmd-buf 10}]
  (let [<action> (chan cmd-buf)
        <in> (chan)
        <out> (chan)]
    (if pre
      (let [<pre-in> (chan)
            <pre-out> (pre <pre-in> <action>)]
        (go-loop []
          (let [action (<! <action>)]
            (put! <pre-in> (<! <in>))
            (put! <out> (act action (<! <pre-out>))))
          (recur)))
      (go-loop []
        (put! <out> (act (<! <action>) (<! <in>)))
        (recur)))
    [<action> <in> <out>]))

(defn sink [sink-fn & {:keys [buf]}]
  (let [<in> (chan buf)]
    (go-loop []
      (sink-fn (<! <in>))
      (recur))
    <in>))

(defn sliding-sink [sink-fn]
  (sink sink-fn :buf (sliding-buffer 1)))

(defn clonut [state & {:keys [pre post cmd-buf]}]
  (let [[<action> <in> <out>] (reactor :pre pre :cmd-buf cmd-buf)]
    (pipe (if post (post <out> <action>) <out>)
          <in>)
    (put! <in> state)
    <action>))

(defn pipe-mult [a-mult]
  (let [<out> (chan)]
    (tap a-mult <out>)
    <out>))

(defn tap-into [<in> taps]
  (let [<out> (chan)
        hub (mult <in>)]
    (run! #(tap hub %) taps)
    (tap hub <out>)
    <out>))

(defn action [f & args]
  (fn [state] (apply f state args)))

(defn act! [<action> f & args]
  (put! <action> (apply action f args)))
