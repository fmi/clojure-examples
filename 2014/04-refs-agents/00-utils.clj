(defn wait-futures
  "Waits for a sequence of futures to complete."
  [& futures]
  (doseq [f futures]
    @f))

(defn dofutures
  "Takes a number n and a function and spawns n future, each calling
   the passed function. It waits for all the futures to get realized
   and returns. Useful for testing concurent behaviors."
  [n & funcs]
  (let [futures (doall (for [_ (range n)
                             func funcs]
                         (future (func))))]
    (apply wait-futures futures)))

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
