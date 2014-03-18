;;; Using commute instead of alter

(load-file "00-utils.clj")
(load-file "00-game.clj")

(defn loot-with-commute
  "Looting with one commute.

   Since conjoining an item to the character's inventory is
   commutative, this works and does less locking."
  [from to]
  (dosync
    (when-let [item (first (:items @from))]
      (commute to update-in [:items] conj item)
      (alter from update-in [:items] disj item))))

(defn loot-with-two-commutes
  "Looting with two commutes."
  [from to]
  (dosync
    (when-let [item (first (:items @from))]
      (commute to update-in [:items] conj item)
      (commute from update-in [:items] disj item))))

(play-out-looting loot-with-commute "Looting w/ commute")
(play-out-looting loot-with-two-commutes "Looting w/ 2 commutes (incorrect)")

(shutdown-agents)
