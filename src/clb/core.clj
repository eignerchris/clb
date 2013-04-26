(ns clb.core
  (:require [clj-redis.client :as redis]
            [org.httpkit.client :as http]
            [clojure.string :as str]
            [cheshire.core :refer :all])
  (:use org.httpkit.server))

(defn -main
  "clojure load balancer"
  [& args]
  (def config (parse-string (slurp "config.json") (fn [k] (keyword k))))
  (def servers (:servers config))
  (def formatted-server-list (map (fn [x] (str "   * " x)) servers))
  
  (dorun (map println formatted-server-list))

  (defn async-request-handler [ring-request]
    (async-response respond ; Use this callback when ready
      (future
        ; (println ring-request)
        (def random-server (rand-nth servers))
        (def remote-addr (:remote-addr ring-request))
        (def scheme (name (:scheme ring-request)))
        (def method (:request-method ring-request))
        (def user-agent (:user-agent ring-request))
        (def query-string (:query-string ring-request))
        (def uri (:uri ring-request))

        (def rerouted-uri (str scheme "://" random-server uri "?" query-string))
        (def options {})

        (defn lb-handler [{:keys [status headers body error]}]
          (future ; Respond from any thread!
            (println (str (clojure.string/upper-case method) " " rerouted-uri))
            (respond {:status status
                      :headers (dorun (map str headers))
                      :body    body})))

        (cond (= method :get (http/get rerouted-uri options lb-handler))
              (= method :post (http/post rerouted-uri options lb-handler))))))
  


  (run-server async-request-handler {:port (:port config)})
)