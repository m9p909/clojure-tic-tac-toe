(ns tic-tac-toe.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [tic-tac-toe.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[tic-tac-toe started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[tic-tac-toe has shut down successfully]=-"))
   :middleware wrap-dev})
