;;; Illustrates delivering to a promise.
;;;
;;; The main thread creates a promise and then attempts to deref it. Before
;;; that, it starts an agent that delivers to the promise. The main thread is
;;; forced to wait, until the agent delivers.

(def the-promise (promise))
(def background (agent nil))

(send background (fn [_] (Thread/sleep 100)))
(send background (fn [_] (println "About to deliver the promise")))
(send background (fn [_] (deliver the-promise 42)))
(send background (fn [_] (println "Delivered the promise a while ago")))

(println "About to read the promise")
(println @the-promise)

;; The output I get is:
;;
;;   → clj 01-promises.clj
;;   About to read the promise
;;   About to deliver the promise
;;   Delivered the promise a while ago
;;   42

(shutdown-agents)
