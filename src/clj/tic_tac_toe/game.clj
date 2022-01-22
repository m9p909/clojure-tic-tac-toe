(ns tic-tac-toe.game)

(def games (atom {}))

(defn reset-games []
  (swap! games (fn [_] {})))

(defn- create-new-game []
  {:public  {:game      [[0 0 0] [0 0 0] [0 0 0]]
             :id        (rand-int 1000000)
             :next-turn :x}
   :private {
             :players {
                       :x (rand-int 100000)
                       :o nil
                       }
             }
   })


(defn- get-public [game]
  (:public game))


(defn create-game []
  (let [game (create-new-game)]
    (assoc
      (get
        @(get
           (swap! games
                  (fn [prev]
                    (assoc prev (:id (get-public game)) (atom game))))
           (:id (get-public game)))

        :public)
      :x
      (get-in game [:private :players :x]))))



(defn- get-game-full [id]
  (get @games id))

(defn get-game [id]
  (let [game (get-game-full id)]
    (if game
      (get-public @game)
      nil)))

(defn join-game [id]
  (let [o-path [:private :players :o]
        game (get-game-full id)
        o (if game (get-in @game o-path) "game not found")
        new-id (rand-int 100000)]
    (cond
      (nil? o)
      (let [new-game (swap!
                       (get-game-full id)
                       (fn [prev]
                         (assoc-in prev o-path new-id)))]
        (-> new-game :public (assoc :o (get-in new-game o-path))))

      :else nil)))

(defn- play [id [i j] value next-turn]
  (let [game (get-game-full id)]
    (if (= (:next-turn (get-public @game)) value)
      (get-public
        (swap! game
               (fn [prev]
                 (assoc-in
                   (assoc-in
                     prev [:public :game i j]
                     value)
                   [:public :next-turn]
                   next-turn)))))))

(defn play-x [id [i j]]
  (play id [i j] :x :o))

(defn play-o [id [i j]]
  (play id [i j] :o :x))

(defn get-player-team [game-id player-id]
  (get
    (clojure.set/map-invert
      (get-in @(get-game-full game-id) [:private :players]))
    player-id))

; Conditions

; Checks if a move is valid from a specific player
(defn playable?
  [{id        :id
    player-id :player-id
    [i j]     :loc
    value     :value}]
  (let [game @(get-game-full id)]
    (and (= (get-in game [:public :next-turn]) (keyword value))
         (= (get-in game [:private :players (keyword value)]) player-id)
         (= 0 (get-in game [:public :game i j])))))





