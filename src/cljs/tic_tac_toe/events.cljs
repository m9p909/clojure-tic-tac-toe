(ns tic-tac-toe.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [cognitect.transit :as transit]
    [tic-tac-toe.ajax :refer [as-transit]]))

;;dispatchers

(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax.core/transit-response-format)
                  :on-success      [:set-docs]}}))

(rf/reg-event-db
  :set-game
  (fn [db event]
    (assoc db :game (merge (:game db) (get event 1)))))

(rf/reg-event-fx
  :start-game
  (fn [_ [_ new-game]]
    {:fx [[:dispatch [:set-game new-game]]
          [:dispatch [:common/navigate! :game]]
          [:dispatch [:game/game-clock]]]}))

(rf/reg-event-fx
  :game/update
  (fn [_ [_ id]]
    {:http-xhrio (as-transit {:method     :get
                              :params     {:id id}
                              :uri        "/get-game"
                              :on-success [:set-game]})}))
(rf/reg-event-fx
  :game/game-clock
  (fn [{db :db} _]
    (if (:game db)
      (let [game (:game db)]
        (js/setTimeout #(rf/dispatch [:game/game-clock]) 1000)
        (println game)
        {:dispatch [:game/update (:id game)]})
      {})))

(rf/reg-event-db
  :game/clean
  (fn [db event]
    (dissoc db :game)))

(rf/reg-event-fx
  :game/create-game
  (fn [_ _]
    {:http-xhrio (as-transit {:method     :post
                              :body       {}
                              :uri        "/create-game"
                              :on-success [:start-game]})}))
(rf/reg-event-fx
  :game/join-game
  (fn [_ [_ id]]
    {:http-xhrio (as-transit {:method     :post
                              :params     {:id id}
                              :uri        "/join-game"
                              :on-success [:start-game]})}))

(rf/reg-event-fx
  :game/play
  (fn [{db :db} [_ play]]
    (let [game (:game db)
          id (:id game)
          player-id (if (nil? (:x game)) (:o game) (:x game))]
      {:http-xhrio (as-transit {:method     :post
                                :params     {:id        id
                                             :player-id player-id
                                             :play      play}
                                :uri        "/play"
                                :on-success [:set-game]})})))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {:dispatch [:game/clean]}))

;;subscriptions

(rf/reg-sub
  :common/route
  (fn [db _]
    (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))

(rf/reg-sub
  :game/game
  (fn [db _]
    (:game db)))
