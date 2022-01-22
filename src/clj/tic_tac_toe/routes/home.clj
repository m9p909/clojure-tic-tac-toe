(ns tic-tac-toe.routes.home
  (:require
    [tic-tac-toe.layout :as layout]
    [clojure.java.io :as io]
    [tic-tac-toe.middleware :as middleware]
    [ring.util.response]
    [ring.util.http-response :as response]
    [tic-tac-toe.game :refer [create-game play-x play-o get-game playable? join-game
                              get-player-team]]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn create-game-endpoint [request]
  (let [game (create-game)]
    (response/created (str "/get-game?id=" (:id game)) game)))

(def last-request (atom {}))
(defn play-endpoint [request]
  (let [params (:params request)
        {id        :id
         player-id :player-id
         play      :play} params
        game (get-game id)
        team (get-player-team id player-id)]
    (println {:id id :player-id player-id :loc play :value team})
    (cond
      (nil? game) (response/not-found {:message "no game found"})
      (not (playable? {:id id :player-id player-id :loc play :value team})) (response/bad-request {:message "not a valid move"})
      :else (response/ok (cond
                           (= team :x) (play-x id play)
                           (= team :o) (play-o id play)
                           )))))

(defn get-game-endpoint [request]
  (swap! last-request (fn [prev] request))
  (let [game (get-game (read-string (get-in request [:params :id])))]
    (cond
      (nil? game) (response/not-found)
      :else (response/ok game))))

(defn join-game-endpoint [request]
  (let [{id :id} (:body-params request)
        join-attempt (join-game id)]
    (if (-> join-attempt nil? not)
      (response/ok join-attempt)
      (response/not-found {:message "The game does not exist or it has already been joined"}))))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/create-game" {:post create-game-endpoint}]
   ["/play" {:post play-endpoint}]
   ["/get-game" {:get get-game-endpoint}]
   ["/join-game" {:post join-game-endpoint}]])


