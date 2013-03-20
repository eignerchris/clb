(ns clb.core
  (:require [clj-redis.client :as redis]
            [org.httpkit.client :as http]
            [clojure.string :as str])
  (:use org.httpkit.server))

(defn -main
  "clojure load balancer"
  [& args]
  (def server-list ["127.0.0.1:3000" "127.0.0.1:3000"])
  (def port 9090)
  (def formatted-server-list (map (fn [x] (str "   * " x)) server-list))
  
  (dorun (map println formatted-server-list))

  (defn async-request-handler [ring-request]
    (async-response respond ; Use this callback when ready
      (future
        (def random-server (rand-nth server-list))
        (def scheme (name (:scheme ring-request)))
        (def method (:request-method ring-request))
        (def server-port (:port 3000))
        (def user-agent (:user-agent ring-request))
        (http/get (str scheme "://" random-server) {}
          (fn [{:keys [status headers body error]}] ;; asynchronous handle response
            (future ; Respond from any thread!
              (respond {:status status
                        :headers (dorun (map str headers))
                        :body    body})))))))


  (run-server async-request-handler {:port port})
)