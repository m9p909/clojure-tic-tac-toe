(ns tic-tac-toe.app
  (:require [tic-tac-toe.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
