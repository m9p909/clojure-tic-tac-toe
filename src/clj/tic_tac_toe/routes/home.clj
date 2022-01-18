(ns tic-tac-toe.routes.home
  (:require
    [tic-tac-toe.layout :as layout]
    [clojure.java.io :as io]
    [tic-tac-toe.middleware :as middleware]
    [ring.util.response]
    [ring.util.http-response :as response]
    [tic-tac-toe.game :refer [create-game play-x play-o get-game playable? get-player-team]]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn create-game-endpoint [request]
  (response/created (create-game)))

(defn play-endpoint [request]
  (let [body (:body request)
        {id        :id
         player-id :player
         play      :play} body
        game (get-game id)
        team (get-player-team game player-id)]
    (cond
      (nil? game) (response/bad-request {:message "no game found"})
      (not (playable? id player-id play team)) (response/bad-request {:message "not a valid move"})
      :else (response/ok (cond
                           (= team :x) (play-x id play)
                           (= team :o) (play-o id play)
                           )))))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/create-game" {:post create-game-endpoint}]
   ["/play" {:post play-endpoint}]])


