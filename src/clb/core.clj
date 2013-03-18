(ns clb.core
  (:require [clj-redis.client :as redis]
            [org.httpkit.client :as http])
  (:use org.httpkit.server))

(defn -main
  "clojure load balancer"
  [& args]
  (println "Starting clojure load balancer...")
  (println "Load balancing across:")
  
  ; setup server list
  ; TODO: move to config file
  (def server-list ["127.0.0.1" "127.0.0.1"])
  (def port 9090)
  (def formatted-server-list (map (fn [x] (str "   * " x)) server-list))
  
  (dorun (map println formatted-server-list))

  (defn async-request-handler [ring-request]
  (def new-request ring-request)
  (def random-server (rand-nth server-list))
  
  (println 
    (get ring-request :request-method) 
    (get ring-request :remote-addr) ">" random-server)
  
  (assoc new-request :server-name random-server)
  (assoc new-request :server-port 80)

  ; {:remote-addr 127.0.0.1, :scheme :http, :request-method :get, :query-string nil, :content-type nil, :uri /, :server-name 127.0.0.1, :headers {accept */*, host 127.0.0.1:9090, user-agent curl/7.24.0 (x86_64-apple-darwin12.0) libcurl/7.24.0 OpenSSL/0.9.8r zlib/1.2.5}, :content-length 0, :server-port 9090, :character-encoding utf8, :body nil}
  (def http-method-call (load-string "http/get"))

  (http-method-call "http://www.google.com")

  ; send response to async-response method below
  (async-response respond ; Use this callback when ready
    (future ; Respond from any thread!
      (respond {:status 200
                :headers {"Content-Type" "text/plain"}
                :body    "This response was delivered asynchronously!"}))))

  (run-server async-request-handler {:port port}) ; Ring server
)