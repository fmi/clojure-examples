;;; This example illustrates retrying an atom update with swap!

(load-file "00-utils.clj")

(def number (atom 0))

(defn slow-inc [x]
  (printf "Incrementing %s slowly..." x)
  (println)
  (Thread/sleep 1000)
  (inc x))

(defn very-slow-inc [x]
  (printf "Incrementing %s very slowly..." x)
  (println)
  (Thread/sleep 3000)
  (inc x))

(in-background
  (swap! number very-slow-inc))

(in-background
  (swap! number slow-inc))

(await-backgound)

(printf "The final value: %s\n" @number)

;; The output of this program is:
;;
;;   → clj 01-atom-retry.clj
;;   Incrementing 0 very slowly...
;;   Incrementing 0 slowly...
;;   Incrementing 1 very slowly...
;;   The final value: 2
;;
;; The first update gets retried, because the atom got changed by slow-inc while
;; very-slow-inc was running. The final result is consistent - it is 2, because
;; two increments were invoked.
