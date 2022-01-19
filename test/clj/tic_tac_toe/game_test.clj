(ns tic-tac-toe.game-test
  (:require [clojure.test :refer :all]
            [tic-tac-toe.game :refer :all]))


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

(defn get-at [game [i j]]
  (get-in game [:game i j]))

(deftest play-test
  (let [game (create-game)
        user-id (:x game)
        game-id (:id game)]

    (testing "x user can play"
      (play-x game-id [0 0])
      (is
        (= :x (get-at (get-game game-id) [0 0]))))

    (testing "then o can play"
      (play-o game-id [0 1])
      (= :o (get-at (get-game game-id) [0 1])))))

(deftest playable?-test
  (let [game (create-game)
        id (:id game)
        o-id (join-game id)]
    (testing "playable x"
      (is
        (playable? {:id        id
                    :player-id (:x game)
                    :loc       [0 0]
                    :value     :x})))
    (testing "not playable o"
      (is (not (playable? {:id        id
                           :player-id o-id
                           :loc       [0 0]
                           :value     :o}))))
    (play-x id [0 0])
    (testing "not playable on an already taken spot"
      (is (not (playable? {:id        id
                           :player-id o-id
                           :loc       [0 0]
                           :value     :o}))))
    (testing "is playable o"
      (is (playable? {:id        id
                      :player-id o-id
                      :loc       [0 1]
                      :value     :o})))
    (testing "not playable x"
      (is (not (playable? {:id        id
                           :player-id (:x game)
                           :loc       [0 1]
                           :value     :x}))))))


