(set-env!
  :source-paths #{"src"}
  :dependencies '[[adzerk/bootlaces "0.1.13" :scope "test"]
                  [org.clojure/core.async "0.2.385"]
                  [shyblower/helpers "1.0.0"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "2.2.0")
(bootlaces! +version+)

(task-options!
  pom  {:project     'shyblower/clonut
        :version     +version+
        :description "A core.async based, action triggered state loop for ClojureScript"
        :url         "https://github.com/shyblower/clonut"
        :scm         {:url "https://github.com/shyblower/clonut.git"}
        :license     {"EPL" "https://github.com/shyblower/clonut/blob/master/LICENSE"}}
  target {:dir ["target"]})
