;;; Illustrates different threads sending messages to an agent.
;;; The order of which the events in each thread are sent is preserved.

(load-file "00-utils.clj")

(def logger (agent 1))

(defn log [msg-id from message]
  (printf "[%02d] %s: %s\n" msg-id (str from) message)
  (inc msg-id))

(defn work [worker]
  (send logger log worker "About to start working (step 1/5)...")
  (send logger log worker "Getting ready (step 2/5)...")
  (send logger log worker "Just started (step 3/5)...")
  (send logger log worker "Working heavily now! (step 4/5)...")
  (send logger log worker "Done! (step 5/5)"))

(dofutures 1
           #(work :alpha)
           #(work :omega)
           #(work :omicron))

(await logger)
(shutdown-agents)
