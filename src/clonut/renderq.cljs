(ns clonut.renderq
  (:require [cljs.core.async :refer [chan sliding-buffer <!]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn most-recent [render-fn]
  (let [render-channel (chan (sliding-buffer 1))]
    (go-loop []
             (render-fn (<! render-channel))
             (recur))
    render-channel))
