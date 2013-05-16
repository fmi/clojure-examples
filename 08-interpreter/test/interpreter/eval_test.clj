(ns interpreter.eval-test
  (:use clojure.test
        interpreter.repl
        [interpreter.parser :only (parse stmt prog)]
        [interpreter.eval]))
(defn run
  [code bindings]
  (-> code (parse stmt) (evaluate (extend-env bindings (default-env)))))

(deftest env-test
  (is (= (lookup (extend-env '{a 1} (empty-env)) 'a)
         1))
  (is (= (lookup (extend-env '{a 1}
                             (extend-env '{b 2} (empty-env)))
                 'b)
         2))
  ())

(deftest eval-test
  (are [code bindings result] (= (run code bindings) result)
       "1"         {}          1
       "1 + 2"     {}          3
       "1 + 2 + 3" {}          6

       "x"         '{x 42}     42
       "x + y"     '{x 2, y 3} 5

       "if (x == 0) { 1 } else { 2 }" '{x 0} 1
       "if (x == 0) { 1 } else { 2 }" '{x 1} 2
       "if (x == 0) { 1 }"            '{x 0} 1
       "if (x == 0) { 1 }"            '{x 1} nil
       "if (x == 0) { x = 2; x }"     '{x 0} 2
       ))

(run-tests)
