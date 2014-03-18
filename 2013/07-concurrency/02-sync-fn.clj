;;; Ilustrates using a promise in order to synchronize callback functions.

;; We have a callback function that performs an addition in the background and
;; invokes a callback.
(defn future-addition
  [arg1 arg2 callback-fn]
  (future (callback-fn (+ arg1 arg2))))

;; This is how we invoke it.
(future-addition 2 3
  (fn [n]
    (printf "Async result: %s\n" n)))

;; sync-fn transforms an async function to a synch function.
(defn sync-fn
  [async-fn]
  (fn [& args]
    (let [result (promise)]
      (apply async-fn (conj (vec args) #(deliver result %)))
      @result)))

;; This is how we use sync-fn
(printf "Sync result: %s\n"
        ((sync-fn future-addition) 2 3))

;; The result is:
;;
;;   → clj 02-sync-fn.clj
;;   Async result: 5
;;   Sync result: 5
;;
;; Although the output is not necessarily in that order.

(shutdown-agents)
