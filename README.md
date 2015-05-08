<img src="https://tscheibl.github.io/clonut/logo.png" alt="Clonut logo" align="right" />
<br/>
<br/>
# clonut
A core.async based, action triggered state loop for Clojurescript - similar to Clojure agents.
## Motivation
After reading of David Nolen's [Om](https://github.com/omcljs/om), I wanted to experiment with Facebooks' [React](https://facebook.github.io/react/) using Clojurescript. But, though beeing a well engineered piece of software, Om merely wraps Javascript OOP in Clojurescript OOP, of course with improvements, like optimizations based on immutable data, but still exposes a, for my taste, much too complex API. I also disliked Om's method of making its virtual DOM components access parts of the applictaion state using its 'Cursor' paradigm because I ultimately don't believe that the hierarchical structure of an application state must in any way reflect the hierarchy of its presentation (view) or parts of it.
So I looked at [Reagent](https://reagent-project.github.io/) which was more to my likes as it is simpler and uses a more clojuresque API style ... but it still was not exactly what I was looking for.
Then almost accidently I stumbled over Luke VanderHart's [Quiescent](https://github.com/levand/quiescent) which made my search for the holy grail of client side web development come a screeching halt. Please care to read Quiescent's [rationale](https://github.com/levand/quiescent#rationale) to find out why.

Since Quiescent does, in contrast to Om and Reagent, NOT manage your application state and maintain control over the render/re-render cycle, what I actually consider a feature than a lack, I could implement that in a way that would fit my needs. What I didn't want was to use 'add-watch' on an atom since that smells to much like AOP which I'm done with. I want to decide, exactly when I offer my state to the renderer and not have a 'magic' watcher do it for me.

So this small library is what I have come up with. Because of the wonderful [core.async](https://github.com/clojure/core.async) channel abstraction it became very small.
Since I have realized that this kind of looping state around in an asynchronous channel construct could be of general use, I have decoupled it from Quiescent related code.

I am also aware that this smells a bit of Clojure's agents, but since, to my knowledge, agents are not available in Clojurescript and the use of core.async channels allows for some amazingly easy tricks, I think it could be useful to the community which is why I put it here as open source.

## A quick example for the impatient
coming soon ...

## API reference
coming soon ...

## Todo
* make this code also available as a Clojure library by using CLJX
