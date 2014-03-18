;;; This example illustrates retrying an atom update with swap!

(load-file "00-utils.clj")

(def number (atom 0))

(defn slow-inc
  [x]
  (printf "Incrementing %s slowly...\n" x)
  (Thread/sleep 100)
  (inc x))

(defn very-slow-inc
  [x]
  (printf "Incrementing %s very slowly...\n" x)
  (Thread/sleep 500)
  (inc x))

(wait-futures
  (future (swap! number very-slow-inc))
  (future (swap! number slow-inc)))

(printf "The final value: %s\n" @number)

;; The output of this program is:
;;
;;   → clj 02-atom-retry.clj
;;   Incrementing 0 very slowly...
;;   Incrementing 0 slowly...
;;   Incrementing 1 very slowly...
;;   The final value: 2
;;
;; The first update gets retried, because the atom got changed by slow-inc while
;; very-slow-inc was running. The final result is consistent - it is 2, because
;; two increments were invoked.

(shutdown-agents)
