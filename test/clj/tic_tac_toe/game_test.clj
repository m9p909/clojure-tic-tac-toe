(ns tic-tac-toe.game-test
  (:require [clojure.test :refer :all]
            [tic-tac-toe.game :refer [get-game play create-game reset-games games]]))


(deftest create-game-test

  (testing "creates game"
    (is
      (not
        (nil?
          (get-game (:id (create-game)))))))

  (testing "games are created"
    (reset-games)
    (let [num-games 5]
      (vec
        (for [_ (take num-games (range))]
          (create-game)))
      (is (= num-games (count @games)))))

  (testing "created game includes game and x id"
    (let [game (create-game)]
      (is (= [[0 0 0] [0 0 0] [0 0 0]] (:game game)))
      (is (int? (:x game)))
      (is (int? (:id game)))
      (is (= :x (:next-turn game))))))


