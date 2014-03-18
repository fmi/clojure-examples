;;; A proper way to do looting

(load-file "00-utils.clj")
(load-file "00-game.clj")

(defn loot
  "Steals one item from a character and transfers it to another.
   Proper version"
  [from to]
  (dosync
    (when-let [item (first (:items @from))]
      (alter to update-in [:items] conj item)
      (alter from update-in [:items] disj item))))

(play-out-looting loot "Correct looting")

(shutdown-agents)
