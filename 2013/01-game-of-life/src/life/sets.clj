(ns life.sets)

(def empty-board #{})

(defn neighbours
  "Finds the coordinates of the neighbours of a cell."
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn step
  "Yields the next state of the world."
  [cells]
  (set (for [[loc n] (frequencies (mapcat neighbours cells))
             :when (or (= n 3) (and (= n 2) (cells loc)))]
         loc)))

(defn stepper
  "Returns a step function given a topology and birth and survival
   predicates."
  [neighbours birth? survive?]
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))

(def step2 (stepper neighbours #{3} #{2 3}))

;; A hexagonal variant. Note that it does not work with the GUI

(defn hex-neighbours
  [[x y]]
  (for [dx [-1 0 1] dy (if (zero? dx) [-2 2] [-1 1])]
    [(+ dx x) (+ dy y)]))

(def hex-step (stepper hex-neighbours #{2} #{3 4}))

;; Interface functions for life.state

(defn new-world [] empty-board)
(def next-gen step2)
(def alive? contains?)

(defn within? [[x y]]
  true)

(defn change-cell [world cell state]
  ((if state conj disj) world cell))

(def living seq)
