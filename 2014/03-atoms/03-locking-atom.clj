(load-file "00-utils.clj")

(def counter (atom 0))

(defn move-up [n]
  (println "I'm trying to increment something" n)
  (in-background
    (swap! counter inc))
  (Thread/sleep 1000)
  (inc n))

(swap! counter move-up)

(println @counter)
