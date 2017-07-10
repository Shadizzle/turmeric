(defproject turmeric "1.1.0"
  :description "Deferred execution, with dependencies. For Clojure."
  :url "https://github.com/shadizzle/turmeric"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]]

  :profiles {:dev {:source-paths ["src" "dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]]}})
