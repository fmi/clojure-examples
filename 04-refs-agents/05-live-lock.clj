;;; Demonstrates transactions that fail after retry limit.
;;;
;;; The outer transaction always starts a new transaction that modifies
;;; the reference. Since the future is dereferenced, the new transaction
;;; completes before the old one completes. This causes a perpetual retry.
;;;
;;; The try/finally is there to make sure shutdown-agents is called.

(def x (ref 0))

(try
  (dosync
    @(future (dosync (ref-set x 0)))
    (ref-set x 1))
  (finally
    (shutdown-agents)))
