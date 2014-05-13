(def threads (atom []))

(defn run-in-background
  "Runs f in a background thread"
  [f]
  (let [thread (Thread. f)]
    (swap! threads conj thread)
    (.start thread)))

(defmacro in-background
  "Runs the code in a new thread"
  [& code]
  `(run-in-background (fn [] ~@code)))

(defn await-backgound
  "Waits for the reads in the background to finish"
  []
  (doseq [thread @threads]
    (.join thread)))
