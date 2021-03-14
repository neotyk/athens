(ns athens.server
  (:gen-class)
  (:require
    [athens.presence :as presence]
    [mount.core :as mount :refer [defstate]]
    [org.httpkit.server :as hk]))


(defn args->sever-config
  [args]
  {:port (Integer/parseInt
           (or (first args)
               (System/getenv "PORT")
               "7890"))
   :join? false})


(defn- wrap-request-logging
  [handler]
  (fn [{:keys [request-method uri] :as req}]
    (let [resp (handler req)]
      (println (name request-method) (:status resp)
               (if-let [qs (:query-string req)]
                 (str uri "?" qs) uri))
      resp)))


(defn start-server
  [server-config]
  (when-let [server (hk/run-server (-> #'presence/routes
                                       wrap-request-logging)
                                   server-config)]
    (println (format "Server has started! http://localhost:%d"
                     (:port server-config)))
    server))


(defstate athens-server
  :start (start-server (args->sever-config
                         (mount/args)))
  :stop  (athens-server :timeout 100))


(defn parse-args
  [args]
  ;; parse args here to return what's needed
  args)


(defn -main
  [& args]
  (-> args
      parse-args
      mount/start-with-args))
