;;; This program shows two usages of swap!, one correct and one incorrect. The
;;; atom good gets incremented with the function passed to swap!. The new value
;;; of the atom bad gets calculated before invoking swap! - instead swap! gets
;;; invoked with a function that returns the new, precalculated value. Not
;;; surprisingly, good gets incremented the correct number of times, while bad
;;; is inconsistently incremented.

(load-file "00-utils.clj")

(def good (atom 0))
(def bad (atom 0))
(def increments 10000)

(dotimes [_ increments]
  (in-background
    (swap! good inc)))

(dotimes [_ increments]
  (in-background
    (let [new-val (inc @bad)]
      (swap! bad (fn [_] new-val)))))

(await-backgound)

(printf "Final value of good: %s\n" @good)
(printf "Final value of bad:  %s\n" @bad)

;; A sample output of this program is:
;;
;;    → clj 02-incorrect-swap.clj
;;   Final value of good: 10000
;;   Final value of bad:  9979

(shutdown-agents)
