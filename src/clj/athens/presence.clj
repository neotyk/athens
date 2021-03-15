(ns athens.presence
  "Athens Presence Server"
  (:require
    [clojure.data.json :as json]
    [compojure.core :as compojure]
    [compojure.route :as route]
    [org.httpkit.server :as server]))


(defonce clients (atom {}))


(defn- now
  []
  (quot (System/currentTimeMillis) 1000))


(let [max-id (atom 0)]
  (defn next-id
    []
    (swap! max-id inc)))


(defonce all-presence (ref []))


(defn open-handler
  [ch]
  (println ch "connected")
  (swap! clients assoc ch true))


(defn receive-handler
  [ch msg]
  (println ch "msg:" msg)
  (let [data (json/read-json msg)]
    (println "decoded msg:" (pr-str data))
    (when (:presence data)
      (let [data (merge data {:time (now)
                              :id   (next-id)})]
        (dosync
          (let [all-presence* (conj @all-presence data)
                total         (count all-presence*)]
            (if (> total 100) ;; NOTE: better way of cleanup, time based maybe? hold presence for 1 minutes?
              (ref-set all-presence (vec (drop (- total 100) all-presence*)))
              (ref-set all-presence all-presence*))))))
    ;; NOTE naive implementation, better use `mult`, `tap` & `untap`
    (doseq [client (keys @clients)]
      ;; send all, client will filter them
      (server/send! client (json/json-str (last @all-presence))))))


(defn close-handler
  [ch status]
  (swap! clients dissoc ch)
  (println ch "closed, status" status))


(defn presence-handler
  [req]
  (server/as-channel req
                     {:on-open    #'open-handler
                      :on-receive #'receive-handler
                      :on-close   #'close-handler}))


(compojure/defroutes routes
                     (compojure/GET "/ws" [] presence-handler)
                     (route/files "" {:root "resources/static"})
                     (route/not-found "<p>Page not found.</p>"))
