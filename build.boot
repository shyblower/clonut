(set-env!
 :source-paths #{"src/clonut"}
 :dependencies '[[adzerk/bootlaces "0.1.11" :scope "test"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "1.0.0")
(bootlaces! +version+)

(task-options!
 pom  {:project     'tscheibl/clonut
       :version     +version+
       :description "TO BE WRITTEN"
       :url         "https://github.com/tscheibl/clonut"
       :scm         {:url "https://github.com/tscheibl/clonut.git"}
       :license     {"EPL" "https://github.com/tscheibl/clonut/blob/master/LICENSE"}})
