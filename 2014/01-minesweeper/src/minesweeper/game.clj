(ns minesweeper.game
  (:require [clojure.pprint :refer [pprint]]))

(defn random-board
  "Generates wXh board with n mines on random positions and [x][y] access."
  [w h n]
  (let [board (->> "_"
                (repeat h) (vec)
                (repeat w) (vec))
        mines (->> (for [i (range) :while (< i w)
                         j (range) :while (< j h)]
                     [i j])
                   (shuffle)
                   (take n)
                   (set))]
    (with-meta board {:mines mines})))

(defn neighbors
  "Returns lazy seq from cell neighbors' coordinates."
  [board [x y]]
  (for [cx [1 0 -1]
        cy [1 0 -1]
        :let  [pos [(+ x cx) (+ y cy)]]
        :when (not= 0 cx cy)
        :when (get-in board pos)]
    pos))

(defn mines-around
  "Returns the number of mines around a cell."
  [board cell]
  (->> (neighbors board cell)
    (filter (:mines (meta board)))
    (count)))

(defn more-flags?
  "Returns true if maximum number of mines on the board is
  not exceeded, else false."
  [board]
  (->> board
    (reduce concat)
    (filter #{"!"})
    (count)
    (>= (-> (meta board)
            (:mines)
            (count)))))

(defn state
  "Returns current board state."
  [board]
  (let [cells (reduce concat board)]
    (cond
      (some #{"x"} cells) :loss
      (some #{"_"} cells) :in-progress
      :else :victory)))

(defn transition
  "Returns new board state based on applying specified action on the
  cell. Unsupported actions will be ignored. New actions can be applied
  only if board state is :in-progress."
  [board action cell]
  (when (= (state board) :in-progress)
    (let [state (get-in board cell)
          mines (:mines (meta board))
          mines-near (mines-around board cell)]
      (case action
        :click
        (if (= state "_")
          (cond
            (mines cell)
            (assoc-in board cell "x")

            (pos? mines-near)
            (assoc-in board cell (str mines-near))

            (zero? mines-near)
            (reduce #(transition %1 :click %2)
              (assoc-in board cell "#")
              (neighbors board cell)))
          board)

        :flag
        (case state
          "!"
          (assoc-in board cell "_")

          "_"
          (if (more-flags? board)
            (assoc-in board cell "!")
            board)

          board)))))

;; ============================================================================

(defn print-board
  [board]
  (->> board
       (apply mapv vector)
       (pprint))
  board)

#_(-> (random-board 9 5 5)
    (transition :flag [3 3])
    (transition :flag [1 3])
    (transition :click [1 3])
    (transition :click [2 2])
    (transition :click [2 2])
    (print-board)
    (state))
