(ns life.indexless)

(defn empty-board
  "Creates an empty board with the given dimensions. The board
   is represented as a vector of vectors of the same size."
  [w h]
  (vec (repeat w (vec (repeat h nil)))))

(defn window
  "Returns a lazy sequence of 3-item windows centered
   around each item of coll, padded as necessary with
   pad or nil."
  ([coll] (window nil coll))
  ([pad coll]
   (partition 3 1 (concat [pad] coll [pad]))))

(defn cell-block
  "Creates sequences of 3x3 windows from a triple of 3
   sequences."
  [[left mid right]]
  (window (map vector left mid right)))

(defn liveness
  "Returns the liveness (nil or :on) of the center cell for
   the next step."
  [block]
  (let [[_ [_ center _] _] block]
    (case (- (count (filter #{:on} (apply concat block)))
             (if (= :on center) 1 0))
      2 center
      3 :on
      nil)))

(defn step-row
  "Yields the next state of the center row."
  [rows-triple]
  (vec (map liveness (cell-block rows-triple))))

(defn step
  "Yields the next state of the board."
  [board]
  (vec (map step-row (window (repeat nil) board))))

;; Interface functions for life.state

(def world-size 20)
(defn new-world [] (empty-board world-size world-size))
(def next-gen step)
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
