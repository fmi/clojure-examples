;;; Illustrates how an agent sequences its messages
;;;
;;; There is a sleep in the log function to illustrate that the call to
;;; println with "Sent everything..." happens independently of executing
;;; the messages

(def logger (agent 1))

(defn log [from message]
  (send logger (fn [msg-id]
                 (Thread/sleep 5)
                 (printf "[%02d] %s: %s\n" msg-id from message)
                 (inc msg-id))))

(log :alpha "About to start working...")
(log :alpha "Getting ready...")
(log :alpha "Just started...")
(log :alpha "Working heavily now!...")
(log :alpha "Done!")

(println "Sent everything to the agent")

;; We can use await, but we don't know about that yet

(Thread/sleep 1000)
(shutdown-agents)
