(ns dev
  (:require
    [athens.server]
    [clojure.tools.namespace.repl :as tn]
    [mount.core :as mount]))


(defn start
  []
  (mount/start #'athens.server/athens-server))


(defn stop
  []
  (mount/stop))


(defn refresh
  []
  (stop)
  (tn/refresh))


(defn refresh-all
  []
  (stop)
  (tn/refresh-all))


(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)


(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  (tn/refresh :after 'dev/go))


(mount/in-clj-mode)
