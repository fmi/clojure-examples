(ns interpreter.parser-test
  (:use clojure.test
        interpreter.parser))

(defn- normalize
  [ast]
  (if (= (count ast) 1)
    (first ast)
    ast))

(deftest parser-test
  (are [input expected] (= (-> input parse normalize) expected)
       "x"     'x
       "x + y" '(+ x y)
       "x * y" '(* x y)
       "1 < x" '(< 1 x)

       "a + b * c + d" '(+ (+ a (* b c)) d)

       "x = 10"        '(set! x 10)
       "a = b + c"     '(set! a (+ b c))

       "function(a) { a * a }" '(lambda (a) (* a a))
       "function(a) { a; a }"  '(lambda (a) a a)
       "foo(1, 2)"             '(foo 1 2)
       "x = function(a) { a }" '(set! x (lambda (a) a))

       "if (x) { a } else { b }"       '(if x a b)
       "if (x) { a; b } else { c; d }" '(if x (begin a b) (begin c d))
       "if (x) { a }"                  '(if x a)
       "if (x) { a; b }"               '(if x (begin a b))
       "function(a) { a }(1)"          '((lambda (a) a) 1)))

(run-tests)
