(ns tic-tac-toe.handler-test
  (:require
    [clojure.test :refer :all]
    [ring.mock.request :refer :all]
    [tic-tac-toe.handler :refer :all]
    [tic-tac-toe.middleware.formats :as formats]
    [muuntaja.core :as m]
    [mount.core :as mount]))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'tic-tac-toe.config/env
                 #'tic-tac-toe.handler/app-routes)
    (f)))

(deftest test-app
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response))))))

(deftest example-game
 (testing "x creates game"
   (let [response ((app) (request :post "/create-game"))]
     (transit)))
 (testing "o joins the game"
   (let [response ((app) (request :post "/join-game?id="+ ))])))
