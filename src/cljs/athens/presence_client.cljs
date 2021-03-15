(ns athens.presence-client
  (:require
    [re-frame.core :as rf]))

;; TODO make part of configuration
(def ws-url "ws://localhost:7890/ws")


(defn client-message-handler
  [event]
  (let [data (js->clj (js/JSON.parse (.-data event))
                      :keywordize-keys true)]
    (js/console.log "WS Client <-:" (pr-str data))
    (when (get-in data [:presence :editing])
      (rf/dispatch [:presence/new-editor data]))))


(def ws
  (when ws-url
    (doto (js/WebSocket. ws-url)
      (.addEventListener "message" client-message-handler))))


(defn publish-editing
  [username uid]
  (js/console.log "publish-editing" (pr-str {:username username
                                             :uid uid}))
  (when (and ws
             (= (.-OPEN ws) (.-readyState ws)))
    (.send ws (js/JSON.stringify (clj->js {:presence {:editor  username
                                                      :editing uid}})))))
