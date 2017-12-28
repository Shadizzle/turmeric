(defproject turmeric "1.1.1"
  :description "Deferred execution, with dependencies. For Clojure."
  :url "https://github.com/shadizzle/turmeric"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/spec.alpha "0.1.143"]]

  :profiles {:dev {:source-paths ["src" "spec" "dev"]
                   :dependencies [[org.clojure/clojure "1.9.0"]
                                  [org.clojure/test.check "0.9.0"]
                                  [org.clojure/tools.namespace "0.2.11"]]}})
