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
