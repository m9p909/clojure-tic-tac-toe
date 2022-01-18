(ns tic-tac-toe.game-test
  (:require [clojure.test :refer :all]
            [tic-tac-toe.game :refer [get-game play-x play-o create-game reset-games games join-game]]))


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

(deftest join-game-test
          (testing "user can join game once"
            (let [game (create-game)]
              (join-game (:id game))
              (nil? (join-game (:id game)))))

          (testing "user gets id on join game"
            (let [game (create-game)]
              (int? (:id (join-game (:id game)))))))

(deftest play-x
          (testing "x user can play"
            (let [game (create-game)
                  user-id (:x game)
                  game-id (:id game)]
              (play-x game-id [0 0])
              (= :x (get-in (get-game game-id) [:game 0 0])) ))


