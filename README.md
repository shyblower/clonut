<img src="https://shyblower.github.io/clonut/logo.png" alt="Clonut logo" align="right" />
<br/>
<br/>
# clonut
A core.async based, action triggered state loop for Clojurescript - similar to Clojure agents.
## Motivation
For the reasons outlined [here](https://github.com/levand/quiescent#rationale) I currently prefer [Quiescent](https://github.com/levand/quiescent) over [Om](https://github.com/omcljs/om) and [Reagent](https://reagent-project.github.io/) for writing [React](https://facebook.github.io/react/) based single page web apps in Clojurescript.

Since Quiescent does, in contrast to Om and Reagent, not manage your application state and does not control the render/re-render cycle, what I actually consider a feature than a lack, I can implement that in a way that fits my needs. What I do not want is to use 'add-watch' on an atom since it obscures control flow like AOP. Also I want to let the user of the toolkit decide, when to push state to the renderer.

So this library is what I have come up with.
Because of the wonderful [core.async](https://github.com/clojure/core.async) channel abstraction it is tiny in terms of code size.
Since I have realized that this kind of looping state around in an asynchronous channel construct, could be of general use, I have decoupled it from Quiescent related code.

I am also aware that this smells a bit of Clojure's agents, but since, to my knowledge, agents are not implemented in Clojurescript and the use of core.async channels allows for some amazing "tricks", I thought this could be useful for the community.

## A quick example for the impatient
One of the manifold ways to use this
```clojure
(ns clonut.example.core
  (:require [quiescent.core :as q]
            [quiescent.dom :as d]
            [clonut.core :refer [with-state act-on-jsevent]]
            [clonut.renderq :as renderq]
            [cljs.core.async :refer [tap put!]]))

(q/defcomponent TitleButton
  [title onClick]
  (d/button {:className "title-button"
              :style {:color "green"}
              :onClick onClick}
             title))

(q/defcomponent Root
  [{:keys [titles selected-title]}
   & {:keys [onTitleClick]}]
  (d/div {}
         (d/h1 {:className "title"
                  :style {:color "blue"}}
                 (titles selected-title))
         (apply d/div {}
                (map-indexed
                 (fn [i t] (TitleButton t #(onTitleClick i))) titles))))

(defn setTitle [state title-index]
  (assoc state :selected-title title-index))

(defn event-handlers [action-channel]
  (let [act #(act-on-jsevent action-channel %)]
    [:onTitleClick (act setTitle)]))

(def initial-state
  {:titles ["Foo" "Bar"]
   :selected-title 0})

(defn render-fn [action-channel]
  (fn [state]
    (q/render (apply Root state (event-handlers action-channel))
              (.getElementById js/document "view"))))

(defn app [action-channel state-mult]
  (tap state-mult (renderq/most-recent (render-fn action-channel)))
  initial-state)

(defn main []
  (with-state app))

(main)

```

## Architecture
coming soon ...

## API reference
coming soon ...

## Todo
* make this code also available as a Clojure library by using CLJX

