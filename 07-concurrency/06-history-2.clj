;;; Another example of ref history.
;;;
;;; Just run each chunk in the REPL and observe the results. Number of retries
;;; (and how early the read-only transaction manages to complete) depends on the
;;; sleep in the "fast thread" and the initial history size.

(println "This is not mean to be run. Take each chunk and call in in the repl instead.")
(System/exit 0)

(def a (ref 0))
(future (dotimes [_ 500] (dosync (Thread/sleep 200) (alter a inc))))
(printf "200ms: deref: %s history: %s\n"
        @(future (dosync (Thread/sleep 1000) @a))
        (ref-history-count a))
;; 200ms: deref: 28 history: 5

(def b (ref 0))
(future (dotimes [_ 500] (dosync (Thread/sleep 20) (alter b inc))))
(printf "20ms: deref = %s history = %s\n"
        @(future (dosync (Thread/sleep 1000) @b))
        (ref-history-count b))
;; 20ms: deref = 500 history = 9

(def c (ref 0 :max-history 100))
(future (dotimes [_ 500] (dosync (Thread/sleep 20) (alter c inc))))
(printf " 20ms: deref = %s history = %s (max-history = 100)\n"
        @(future (dosync (Thread/sleep 1000) @c))
        (ref-history-count c))
;; 20ms: deref = 500 history = 10 (max-history = 100)

(def d (ref 0 :min-history 50 :max-history 100))
(future (dotimes [_ 500] (dosync (Thread/sleep 20) (alter d inc))))
(printf " 20ms: deref = %s history = %s (min-history = 50)\n"
        @(future (dosync (Thread/sleep 1000) @d))
        (ref-history-count d))
;; 20ms: deref = 28 history = 50 (min-history = 50)
