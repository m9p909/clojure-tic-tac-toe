(ns tic-tac-toe.game)

(def games (atom {}))

(defn reset-games []
  (swap! games (fn [_] {})))

(defn create-new-game []
  {:public  {:game      [[0 0 0] [0 0 0] [0 0 0]]
             :id        (rand-int 1000000)
             :next-turn :x}
   :private {
             :players {
                       :x (rand-int 100000)
                       :o (rand-int 100000)
                       }
             }
   })

(defn get-player-team [game id]
  (get
    (clojure.set/map-invert
      (get-in game [:private :players]))
    id))

(defn get-public [game]
  (:public game))

(defn create-game []
  (let [game (create-new-game)]
    (assoc
      (get-in
        (swap! games
               (fn [prev]
                 (assoc prev (:id (get-public game)) game)))
        [(:id (get-public game)) :public])
      :x
      (get-in game [:private :players :x]))))

(defn get-game [id]
  (get-public (get @games id)))

(defn play [id [i j] value]
  (let [game (get-game id)]
    (if (= (:next-turn @game) value)
      (get-public
        (swap! game
               (fn [prev]
                 (update-in
                   (update-in
                     prev [:public :game i j]
                     (fn [_] value))
                   [:public :next-turn]
                   #(:x))))))))

(defn play-x [id [i j]]
  (play id [i j] :x))

(defn play-o [id [i j]]
  (play id [i j] :o))

; Conditions
; next turn = player
; i and j are not played yet
(defn playable? [id player-id [i j] value]
  (let [game @(get-game id)]
    (and (= (get-in game [:public :next-turn]) (keyword value))
         (= (get-in game [:private :players (keyword value)]) player-id)
         (= 0 (get-in game [:public :game i j])))))



