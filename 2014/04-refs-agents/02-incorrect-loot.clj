;;; Two ways to implement looting incorrectly

(load-file "00-utils.clj")
(load-file "00-game.clj")

(defn non-synced-loot
  "Not wrapping the two alters in a transaction.

   It totally gets everything wrong."
  [from to]
  (when-let [item (first (:items @from))]
    (dosync (alter to update-in [:items] conj item))
    (dosync (alter from update-in [:items] disj item))))

(defn read-outside-dosync-loot
  "Reading the items outside dosync.

   Since the access to the items is not synced, two separate threads
   might obtain the same item before starting the transaction."
  [from to]
  (when-let [item (first (:items @from))]
    (dosync
      (alter from update-in [:items] disj item)
      (alter to update-in [:items] conj item))))

(play-out-looting non-synced-loot "Two transactions")
(play-out-looting read-outside-dosync-loot "Read outside dosync")

(shutdown-agents)
