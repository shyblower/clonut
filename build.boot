(set-env!
  :source-paths #{"src"}
  :dependencies '[[adzerk/bootlaces "0.1.13" :scope "test"]
                  [org.clojure/core.async "0.2.374"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "2.0.0")
(bootlaces! +version+)

(task-options!
  pom  {:project     'tscheibl/clonut
        :version     +version+
        :description "A core.async based, action triggered state loop for clojurescript"
        :url         "https://github.com/tscheibl/clonut"
        :scm         {:url "https://github.com/tscheibl/clonut.git"}
        :license     {"EPL" "https://github.com/tscheibl/clonut/blob/master/LICENSE"}}
  target {:dir ["target"]})
