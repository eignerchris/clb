(defproject clb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-redis "0.0.12"]
                 [http-kit "2.0.0-RC4"]
                 [cheshire "5.1.1"]]
  :main clb.core)