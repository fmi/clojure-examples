;;; This program illustrates how to use dynamic scoping to return arguments.
;;; The abs function can indicate whether it had to flip the sign of its
;;; argument or not. The caller can check *negative-arg* after calling abs.
;;;
;;; A caveat of this code is that it would result to an error if the caller has
;;; not bound *negative-arg*. Fixing this (with thread-bound?) is left as an
;;; exercise to the reader.
(def ^:dynamic *negative-arg*)

(defn abs [n]
  (if (neg? n)
    (do (set! *negative-arg* true)
        (- n))
    (do (set! *negative-arg* false)
        n)))

(defn report-abs [n]
  (binding [*negative-arg* nil]
    (let [abs-n (abs n)]
      (printf "The abs of %s is %s and *negative-arg* is: %s\n"
              n abs-n *negative-arg*))))

(report-abs 42)
(report-abs -42)

;; Output from this program:
;;
;;   → clj 04-dynamic-returns.clj
;;   The abs of 42 is 42 and *negative-arg* is: false
;;   The abs of -42 is 42 and *negative-arg* is: true
