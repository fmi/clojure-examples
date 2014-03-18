;;; Illustrates the difference between send and send-off.
;;;
;;; Starts 30 agents and sends a function to each that just sleeps for a
;;; second. It does it once with send and once with send-off.
;;;
;;; If the computer has N processors, the version with send starts running the
;;; first N+2 functions and waits until they finish, before it starts the next
;;; N+2, because the send thread pool has a limit of N+2. In contrast, the
;;; send-off version starts all at once.
;;;
;;; The result is that the send-off version completes in almost one second,
;;; while the send version takes more time (depending on the number of
;;; processors).

(defn exercise-agents [send-fn]
  (let [agents (map #(agent %) (range 30))]
    (doseq [a agents]
      (send-fn a (fn [_] (Thread/sleep 1000))))
    (doseq [a agents]
      (await a))))

(println "Using send...")
(time (exercise-agents send))

(println "Using send-off...")
(time (exercise-agents send-off))

;; The output on my machine (quadcore with hyperthreading, i.e. 8 processors).
;;
;;   → clj 09-send-and-send-off.clj
;;   Using send...
;;   "Elapsed time: 3021.713 msecs"
;;   Using send-off...
;;   "Elapsed time: 1008.306 msecs"

(shutdown-agents)
