(ns interpreter.repl
  (:use [interpreter.parser :only (parse stmt prog)]
        [interpreter.eval :only (default-env evaluate)]))

(def repl-env (default-env))

(defn eval-and-print
  [input]
  (try
    (-> input (parse stmt) (evaluate repl-env) println)
    (catch RuntimeException e
      (println "FAIL:" (.getMessage e)))))

(defn parse-and-print
  [input]
  (try
    (-> input (parse stmt) println)
    (catch RuntimeException e
      (println "FAIL:" (.getMessage e)))))

(defn run-file
  [file]
  (let [stmts (-> file slurp (parse prog))
        env (default-env)]
    (doseq [stm stmts]
      (evaluate stm env))))

(defn repl []
  (print "> ")
  (flush)
  (let [input (read-line)]
    (cond (= \. (get input 0))
          (do (parse-and-print (subs input 1))
              (recur))

          (= "exit" input)
          (println "See ya!")

          :else
          (do (eval-and-print input)
              (recur)))))
