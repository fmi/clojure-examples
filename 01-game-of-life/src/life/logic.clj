(ns life.logic)

;;; A placeholder file to use in demoing the different
;;; possible Game of life implementations. Fill this in
;;; properly in order to have your own implementation
;;; working.

(defn new-world
  "Creates a new, empty world. It abstracts the representation
   away."
  []
  #{})

(defn next-gen
  "Calculates the next generation of the world."
  [world]
  world)

(defn alive?
  "Tells whether a cell is alive in the given world. Used by
   the GUI to determine how to handle adding and removing of
   cells."
  [world cell]
  false)

(defn within?
  "Returns whether the given cell is within the boundaries of
   the world. Used by the GUI to determine whether it should
   attempt to modify the world when a cell is clicked."
  [[x y]]
  true)

(defn change-cell
  "Returns a new world with the given cell modified. Called by
   the GUI when a cell is clicked"
  [world cell state]
  world)

(defn living
  "Returns a sequence of all living cells. Used by the GUI to
   render the world."
  [world]
  [])
