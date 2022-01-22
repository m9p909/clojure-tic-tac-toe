(ns tic-tac-toe.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [tic-tac-toe.ajax :as ajax]
    [tic-tac-toe.events]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as string])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href  uri
    :class (when (= page @(rf/subscribe [:common/page-id])) :is-active)}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-info>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "tic-tac-toe"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click    #(swap! expanded? not)
                  :class       (when @expanded? :is-active)}
                 [:span] [:span] [:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Home" :home]
                 [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(def id (r/atom 0))
(defn home-page []
  [:section.section>div.container>div.content
   [:div
    [:h1 "Play Tic Tac Toe!"]]
   [:button.button
    {:on-click #(rf/dispatch [:game/create-game])} "create game"
    ]
   [:div
    [:br]
    [:h2 "Join a game!"]
    [:form {:on-submit (fn [event] (.preventDefault event)
                         (rf/dispatch [:game/join-game (int @id)]))}
     [:input.input {:type      :number
                    :value     @id
                    :on-change (fn [evt] (let [value (-> evt .-target .-value)]
                                           (reset! id value)
                                           ))}]
     [:button.button {:type :submit} "Join Game"]]]])

(defn game []
  (let [game @(rf/subscribe [:game/game])]
    [:section.section>div.container>div.content
     [:div
      [:h1 (str "Welcome Player " (if (not (nil? (:x game))) "X" "O"))]
      [:h2 (str "room code is " (:id game))]
      (if game
        (map-indexed (fn [i row]
                       [:div {:key (str "row " i)}
                        (map-indexed (fn [j cell]
                                       [:div.button
                                        {:key      (str "col " j)
                                         :on-click #(rf/dispatch [:game/play [i j]])
                                         :style {:width "50px"
                                                 :height "50px"}}
                                        (cond
                                          (= cell :x) "X"
                                          (= cell :o) "O"
                                          :else "")])
                                     row)])
                     (:game game))
        [:button.button.is-loading])
      ]])
  )

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name :home
           :view #'home-page
           :controllers [{:start (fn [_]  (rf/dispatch [:game/clean]))}]
           }]
     ["/about" {:name :about
                :view #'about-page}]
     ["/game" {:name :game
               :view #'game}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
