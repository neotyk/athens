(ns athens.presence-client
  "Athens Presence Client"
  (:require
    [re-frame.core :as rf]))

;; TODO make part of configuration
(def ws-url "ws://localhost:7890/ws")


(defn client-message-handler
  [event]
  (let [data (-> event
                 .-data
                 js/JSON.parse
                 (js->clj :keywordize-keys true))]
    (js/console.log "WS Client <-:" (pr-str data))
    (when (get-in data [:presence :editing])
      (rf/dispatch [:presence/new-editor data]))))


(declare ws)
(declare connect-to-presence)


(defn client-open-handler
  [event]
  (js/console.log "WS Client Connected:" event)
  (.send (.-target event) (-> {:username @(rf/subscribe [:user])}
                              clj->js
                              js/JSON.stringify)))


(defn client-close-handler
  [event]
  (js/console.log "WS Client Disconnected:" event)
  (doto (.-target event)
    (.removeEventListener "message" client-message-handler)
    (.removeEventListener "close" client-close-handler)
    (.removeEventListener "open" client-open-handler))
  (reset! ws nil)
  (js/setTimeout #(reset! ws (connect-to-presence ws-url))
                 3000))


(defn connect-to-presence
  [url]
  (js/console.log "WS Client Connecting:" url)
  (when url
    (doto (js/WebSocket. url)
      (.addEventListener "message" client-message-handler)
      (.addEventListener "close" client-close-handler)
      (.addEventListener "open" client-open-handler))))


(def ws (atom (connect-to-presence ws-url)))


(defn publish-editing
  [username uid]
  (let [conn     @ws
        pub-edit {:editing uid}]
    (js/console.log "publish-editing" uid conn)
    (if (and conn
             (= (.-OPEN js/WebSocket) (.-readyState conn)))

      (do
        (js/console.log "sending")
        (.send conn (-> pub-edit
                        clj->js
                        js/JSON.stringify)))

      (js/console.warn "Can't publish, WS Closed."
                       (.-readyState conn)
                       (.-readyState @ws)))))
