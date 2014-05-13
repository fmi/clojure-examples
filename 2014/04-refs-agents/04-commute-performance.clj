;;; An example of the performance gain of using commute

(load-file "00-utils.clj")

(def x (ref 0))

(defn run-operations [modifier]
  (dotimes [_ 5]
    (in-background
      (dotimes [_ 1000]
        (dosync (modifier x + (apply + (range 1000))))))
    (in-background
      (dotimes [_ 1000]
        (dosync (modifier x - (apply + (range 1000)))))))

  (await-backgound))

(println "Using alter")
(time (run-operations alter))

(println "Using commute")
(time (run-operations commute))

(shutdown-agents)
