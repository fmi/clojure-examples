(ns minesweeper.ui
  (:require [minesweeper.game :as g]
            [clojure.string :refer [split]]))

(defn new-board
  "Creates a new board."
  []
  (let [x 9]
    (g/random-board x x x)))

(defn print-board
  "Pretty print board."
  [board]
  (newline)
  (->> board
       (count)
       (range 0)
       (map #(str " " % " "))
       (apply println "  " ))
  (->> board
       (apply mapv vector)
       (map-indexed prn)
       (doall))
  (newline)
  (println "-------------------------------------")
  board)

(defn run
  "Performs board transition with provided arguments and
  prints the new board."
  [board cmd args]
  (-> board
      (g/transition cmd (->> (mapv read-string args)
                             (reverse)))
      (print-board)))

(defn start
  "Starts console UI. This function will block the current thread. Type
  `exit` to quit the repl and `help` to get help."
  []
  (println "================================")
  (println "            Welcome             ")
  (println "================================")
  (loop [board (print-board (new-board))]
    (case (g/state board)
      :loss
      (println "YOU LOSE!")

      :victory
      (println "YOU WIN!")

      :in-progress
      (let [[cmd & args] (split (read-line) #"\s")]
        (case (keyword cmd)
          :exit
          (println "Bye for now!")

          :restart
          (do
            (do
              (println "-------------------------------------")
              (println "             New game:               ")
              (println "-------------------------------------")
              (println "-------------------------------------"))
            (-> (new-board)
                (print-board)
                (recur)))

          :board
          (do (print-board board)
            (recur board))

          :help
          (do
            (println "-------------------------------------")
            (println "               Usage:                ")
            (println "-------------------------------------")
            (println "  `click [x] [y]` - click on cell    ")
            (println "  `flag [x] [y]`  - set flag on cell ")
            (println "  `restart`       - restart game     ")
            (println "  `help`          - see this text    ")
            (println "  `board`         - show board       ")
            (println "  `exit`          - quit game        ")
            (println "-------------------------------------")
            (recur board))

          :flag
          (-> board
              (run :flag args)
              (recur))

          :click
          (-> board
              (run :click args)
              (recur))

          (do (println "Unknown command: " cmd)
            (recur board)))))))

;; ============================================================================
;; The history

;; (def history
;;   (atom []))
;;
;; (swap! history conj board)

;; ============================================================================
;; The future

;; (defn die? [board cell]
;;   (-> board
;;       (transition :click cell)
;;       (nil?)))

;; ============================================================================
;; The storage

;; (def storage (atom {:name 10}))
;; (swap! storage assoc :name 10)

;; ============================================================================
