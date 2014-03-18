(ns life.state
  (use [life.logic :only (next-gen new-world alive? change-cell living within?)]))

(def ^:private history (ref [(new-world)]))
(def ^:private current (ref 0))

(defn living-cells []
  (dosync (living (get (ensure history) (ensure current)))))

(defn cell-alive? [cell]
  ((living-cells) cell))

(defn toggle-cell! [cell]
  (when (within? cell)
    (dosync
      (let [generation (ensure current)
            world ((ensure history) generation)
            new-state (not (alive? world cell))
            new-world (change-cell world cell new-state)]
        (alter history #(conj (vec (subvec % 0 generation)) new-world))
        (ref-set current generation)))))

(defn next-generation! []
  (dosync
    (when (= (inc (ensure current)) (count (ensure history)))
      (alter history #(conj % (next-gen (last %)))))
    (alter current inc))
  nil)

(defn previous-generation! []
  (dosync (when (> (ensure current) 0)
            (alter current dec)))
  nil)

(defn current-generation []
  (inc @current))

(defn generation-count []
  (count @history))
