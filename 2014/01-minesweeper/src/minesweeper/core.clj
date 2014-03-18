(ns minesweeper.core
  (:require [minesweeper.ui :as ui])
  (:gen-class))

(defn -main
  [& args]
  (ui/start))
