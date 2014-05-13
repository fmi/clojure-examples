;;; This example illustrates that actions sent within a transaction are sent
;;; only once, even if the transaction is retried.

(load-file "00-utils.clj")
(load-file "00-game.clj")

(def tries (atom 0))
(def logger (agent 1))

(defn log
  "Logs a single message and increases the log counter."
  [msg-id actor message]
  (printf "[%03d] %s: %s\n" msg-id actor message)
  (inc msg-id))

(def smaug (character "Smaug" :health 500 :strength 400 :items (set (range 30))))
(def gandalf (character "Bilbo" :health 100 :strength 100))
(def bilbo (character "Gandalf" :health 75 :mana 750))

(defn loot
  "Steals one item from a character and transfers it to another.
   Proper version"
  [from to]
  (dosync
    (when-let [item (first (:items @from))]
      (swap! tries inc)
      (alter to update-in [:items] conj item)
      (alter from update-in [:items] disj item)
      (send-off logger log (:name @to) (str "Took item " item)))))

(in-background (while (loot smaug bilbo)))
(in-background (while (loot smaug gandalf)))
(await-backgound)

(await logger)
(printf "Tries: %s\n" @tries)

(println "=========================")
(printf "Items in Bilbo: %s\n" (-> @bilbo :items count))
(printf "Items in Gandalf: %s\n" (-> @gandalf :items count))
(printf "Shared items: %s\n\n" (count (set/intersection (:items @bilbo)
                                                        (:items @gandalf))))

(shutdown-agents)
