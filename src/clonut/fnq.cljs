(ns clonut.fnq
  (:require [cljs.core.async :refer [chan sliding-buffer <! put!]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn most-recent []
  (let [fn-channel (chan (sliding-buffer 1))]
    (go-loop []
             ((<! fn-channel))
             (recur))
    (fn [f] (put! fn-channel f))))
