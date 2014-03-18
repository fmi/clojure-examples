(ns life.core
  (:use [life.gui :only (start-game)])
  (gen-class :main true))

(defn -main []
  (start-game)
  nil)
