;;; Illustrates the ref history
;;;
;;; In our example, we have a ref accessed by two threads. The first opens a
;;; transaction, sleeps for a while and then attempts to do a read. The second
;;; runs a lot of small transcations that increment the ref.
;;;
;;; Since the first thread's transaction sleeps for quite a while, it is
;;; possible to exhaust all the ref history before it attempts to read its
;;; value. This would cause the transaction to retry.
;;;
;;; If we set a large min-history, the ref will have enough of its history for
;;; the first thread to deref it.

;; Let's use the default min history (0) and see how many times we need to
;; attempt reading:

(def the-ref (ref 0))
(def tries (atom 0))

(dorun (pvalues (dosync
                  (Thread/sleep 1000)
                  (swap! tries inc)
                  @the-ref)
                (dotimes [_ 1000]
                  (Thread/sleep 1)
                  (dosync
                    (alter the-ref inc)))))

(printf "Tries w/ default history: %s\n" @tries)

;; Now let's use a large history:

(def the-ref (ref 0 :min-history 1001))
(def tries (atom 0))

(dorun (pvalues (dosync
                  (Thread/sleep 1000)
                  (swap! tries inc)
                  @the-ref)
                (dotimes [_ 1000]
                  (Thread/sleep 1)
                  (dosync
                    (alter the-ref inc)))))

(printf "Tries w/ large history:   %s\n" @tries)

;; Finally, here is some code that will dump the exception deref throws:

(def the-ref (ref 0 :min-history 1))

(dorun (pvalues (dosync
                  (Thread/sleep 1000)
                  (swap! tries inc)
                  (try
                    @the-ref
                    (catch Throwable t
                      (println)
                      (.printStackTrace t))))
                (dotimes [_ 1000]
                  (Thread/sleep 1)
                  (dosync
                    (alter the-ref inc)))))

;; Sample output:
;;
;;   → clj 05-history.clj
;;   Tries w/ default history: 3
;;   Tries w/ large history:   1
;;
;;   clojure.lang.LockingTransaction$RetryEx
;;           at clojure.lang.LockingTransaction.<init>(LockingTransaction.java:105)
;;           at clojure.lang.LockingTransaction.runInTransaction(LockingTransaction.java:226)
;;           at user$eval1$fn__6.invoke(05-history.clj:26)
;;           at clojure.core$pcalls$fn__6294.invoke(core.clj:6374)
;;           at clojure.core$pmap$fn__6275$fn__6276.invoke(core.clj:6354)
;;           at clojure.core$binding_conveyor_fn$fn__4107.invoke(core.clj:1836)
;;           at clojure.lang.AFn.call(AFn.java:18)
;;           at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:334)
;;           at java.util.concurrent.FutureTask.run(FutureTask.java:166)
;;           at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1110)
;;           at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:603)
;;           at java.lang.Thread.run(Thread.java:722)

(shutdown-agents)
