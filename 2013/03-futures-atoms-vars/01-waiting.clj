;;; Starting a future and not calling (shutdown-agents) will cause the program
;;; to wait some time before exiting (usually 1 minute). This might vary on
;;; different versions of Clojure and Java. IT is easily solvable by calling
;;; (shutdown-agents) at the end of the program.

(def answer
  (future (Thread/sleep 1000)
          42))

(printf "The answer is %s\n" @answer)

;; Without the call below, this program will wait for a while before
;; terminating.
;;
;; (shutdown-agents)
;;
;; For example, running on my machine it yields:
;;
;;   → time clj 01-waiting.clj
;;   The answer is 42
;;   clj 01-waiting.clj  1.76s user 0.09s system 2% cpu 1:02.25 total
