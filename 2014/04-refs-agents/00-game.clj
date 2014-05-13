(use '[clojure.set :as set])

(defn character
  "Creates a character."
  [name & {:as opts}]
  (ref (merge {:name name :items #{} :health 100}
              opts)))

(defn play-out-looting
  "Simulates a Frodo and Gandalf looting smaug concurrently. Takes
   a loot function in order to try multiple things"
  [loot-fn scenario]
  (let [hoard (set (range 100))
        smaug (character "Smaug" :health 500 :strength 400 :items hoard)
        gandalf (character "Bilbo" :health 100 :strength 100)
        bilbo (character "Gandalf" :health 75 :mana 750)]
    (dotimes [_ 2] 
      (in-background (while (loot-fn smaug bilbo)))
      (in-background (while (loot-fn smaug gandalf))))

    (await-backgound)

    (println scenario)
    (println "=========================")
    (printf "Items in Bilbo: %s\n" (-> @bilbo :items count))
    (printf "Items in Gandalf: %s\n" (-> @gandalf :items count))
    (printf "Shared items: %s\n\n" (count (set/intersection (:items @bilbo)
                                                            (:items @gandalf))))))
