(ns life.indexed)

(defn empty-board
  "Creates an empty board with the given dimensions. The board
   is represented as a vector of vectors of the same size."
  [w h]
  (vec (repeat w (vec (repeat h nil)))))

(defn populate
  "Adds living-cells to board."
  [board living-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates :on))
          board
          living-cells))

(defn neighbours
  "Finds the coordinates of the neighbours of a cell."
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn count-neighbours
  "Counts the number of alive neighbours of a cell"
  [board cell]
  (count (filter #(get-in board %) (neighbours cell))))

(defn step
  "Finds the next generation of cells, using a hand-rolled loop in a
   pretty much imperative fashion."
  [board]
  (let [w (count board)
        h (count (first board))]
    (loop [new-board board x 0 y 0]
      (cond (>= x w) new-board
            (>= y h) (recur new-board (inc x) 0)
            :else
              (let [new-liveness
                     (case (count-neighbours board [x y])
                       2 (get-in board [x y])
                       3 :on
                       nil)]
                (recur (assoc-in new-board [x y] new-liveness) x (inc y)))))))

(defn step2
  "Finds the next generation of cells, using nested reduce calls instead
   of loop."
  [board]
  (let [w (count board)
        h (count (first board))]
    (reduce
      (fn [new-board x]
        (reduce
          (fn [new-board y]
            (let [new-liveness
                  (case (count-neighbours board [x y])
                    2 (get-in board [x y])
                    3 :on
                    nil)]
              (assoc-in new-board [x y] new-liveness)))
          new-board (range h)))
      board (range w))))

(defn step3
  "Finds the next generation of cells, using a single reduce."
  [board]
  (let [w (count board)
        h (count (first board))]
    (reduce
      (fn [new-board [x y]]
        (let [new-liveness
              (case (count-neighbours board [x y])
                2 (get-in board [x y])
                3 :on
                nil)]
          (assoc-in new-board [x y] new-liveness)))
      board (for [x (range h) y (range w)] [x y]))))

;; Interface functions for life.state

(def world-size 20)
(defn new-world [] (empty-board world-size world-size))
(def next-gen step3)
(def alive? get-in)

(defn within? [[x y]]
  (and (< -1 x world-size)
       (< -1 y world-size)))

(defn change-cell [world cell state]
  (assoc-in world cell (if state :on nil)))

(defn living [world]
  (for [x (range (count world))
        y (range (count (first world)))
        :when (get-in world [x y])]
    [x y]))
