(ns interpreter.core
  (:use [interpreter.repl :only (repl run-file)])
  (gen-class :main true))

(defn -main
  ([] (repl))
  ([filename] (run-file filename)))
