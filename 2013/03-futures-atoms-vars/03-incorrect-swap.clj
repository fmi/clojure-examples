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

(dofutures increments
           #(swap! good inc))

(dofutures increments
           (fn []
             (let [new-val (inc @bad)]
               (swap! bad (fn [_] new-val)))))

(printf "Final value of good: %s\n" @good)
(printf "Final value of bad:  %s\n" @bad)

;; A sample output of this program is:
;;
;;    → clj 03-incorrect-swap.clj
;;   Final value of good: 10000
;;   Final value of bad:  7520

(shutdown-agents)
