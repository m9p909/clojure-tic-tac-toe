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

(defn get-player-team [game id]
  (get
    (clojure.set/map-invert
      (get-in game [:private :players]))
    id))

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

(defn get-game [id]
  (get-public @(get @games id)))

(defn- get-game-full [id]
  (get @games id))


(defn join-game [id]
  (let [o-path [:private :players :o]
        o (get-in @(get @games id) o-path)
        new-id (rand-int 100000)]
    (cond
      (nil? o) (get-in
                 (swap!
                   (get @games id)
                   (fn [prev]
                     (assoc-in prev o-path new-id))) o-path)
      :else nil))
  )

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



