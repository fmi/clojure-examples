;;; This program illustrates write skews. It is a classic example - one person
;;; has two accounts and they can withrdraw from them any amount, as long as
;;; both accounts have a non-negative total. For example if each account has
;;; $100, the person can withdraw $200.
;;;
;;; Write skew happens when there are concurrent withdraws for $200 on each
;;; account. Each withdraw will check the total of the two accounts before
;;; commiting. If the checks happen simultaneously, they will be followd by two
;;; withdraws and in the end each account with have $-100. This is because the
;;; values in the accounts at the end of the transaction are not necessarily
;;; the values in the beginning, and since they are mutualy read, but not
;;; mutually modified, no transaction will be retried.
;;;
;;; This can be solved by using ensure.

(def account-1 (ref 100))
(def account-2 (ref 100))

;; This illustrates write skew:

(defn withdraw
  [account amount]
  (dosync
    (when (>= (+ @account-1 @account-2) amount)
      (Thread/sleep 100)
      (alter account - amount))))

(dorun (pvalues (withdraw account-1 200)
                (withdraw account-2 200)))

(println "Write skew:")
(printf "- Account 1: %s\n" @account-1)
(printf "- Account 2: %s\n" @account-2)

;; This illusrates how to avoid write skew with ensure:

(dosync
  (ref-set account-1 100)
  (ref-set account-2 100))

(defn safe-withdraw
  [account amount]
  (dosync
    (when (>= (+ (ensure account-1) (ensure account-2)) amount)
      (Thread/sleep 100)
      (alter account - amount))))

(dorun (pvalues (safe-withdraw account-1 200)
                (safe-withdraw account-2 200)))

(println "Using ensure:")
(printf "- Account 1: %s\n" @account-1)
(printf "- Account 2: %s\n" @account-2)

;; A sample output can be:
;;
;;   → clj 04-write-skew.clj
;;   Write skew:
;;   - Account 1: -100
;;   - Account 2: -100
;;   Using ensure:
;;   - Account 1: 100
;;   - Account 2: -100

(shutdown-agents)
