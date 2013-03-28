;;; This program illustrates errors in agents.
;;;
;;; Just follow the commentary.

(defn poke-agent
  "Sends an action to an agent and reports if send raises an exception."
  [a]
  (try
    (send a identity)
    (catch Exception e
      (printf "> Caught exception: %s\n" (.getMessage e))
      (printf "> Error is: %s\n"(.getMessage (agent-error a))))))

;; Create an agent
(def dummy (agent :initial))

;; Send 10 actions. Each action raises a different error message. Each
;; action does a sleep so all ten actions can be scheduled before the
;; agent fails
(println "Sending 10 actions...")
(dotimes [n 10]
  (send-off dummy (fn [_]
                (Thread/sleep 50)
                (throw (RuntimeException. (str "Error #" n))))))

;; Wait for the first action to fail
(Thread/sleep 100)

;; Try to send another action and get an exception
(println "Attempting to send an action to the agent...")
(poke-agent dummy)

;; Trying again will result to the same exception
(println "Attempting to send another action to the agent...")
(poke-agent dummy)

;; Restart the agent with a new value
(restart-agent dummy :restart-value)

;; Wait for the agent to fail again
(Thread/sleep 100)

;; Trying to send an action will result to the exception of the
;; second action sent (Error #1), becuase the agent resumed, but
;; it still had queued actions that raise exceptions
(println "Attempting to send action after restart...")
(poke-agent dummy)

;; Restart the agent, clearing the actions
(restart-agent dummy :restart-2-value :clear-actions true)

;; Wait for a bit...
(Thread/sleep 100)

;; ...and send another action that will succeed.
(poke-agent dummy)

;; Wait for the agent to complete
(await dummy)

;; Print its final value,
(printf "Final value: %s\n" @dummy)

;; and exit.
(shutdown-agents)
