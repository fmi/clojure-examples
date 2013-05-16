(ns interpreter.parser
  (:use (blancas.kern (core :exclude (parse) :as kern))
        blancas.kern.lexer.java-style))

(defn- begin
  [ast]
  (if (= (count ast) 1)
    (first ast)
    (list* 'begin ast)))

(declare expr)
(declare stmt)
(declare lambda)
(declare var-ref)

(def block
  (bind [stmts (braces (sep-by semi (fwd stmt)))]
    (return (apply list stmts))))

(def funcall
  (bind [func (<|> (fwd var-ref) (fwd lambda) (parens (fwd expr)))
         exprs (parens (sep-by comma expr))]
      (return (list* func exprs))))

(def lambda
  (bind [_ (token "function")
         params (parens (sep-by comma identifier))
         body block]
    (return (list* 'lambda
                   (apply list (map symbol params))
                   body))))

(def condition
  (bind [_ (token "if")
         conditional (parens expr)
         consequent block
         alternative (optional (>> (token "else") block))]
    (return
      (if alternative
        (list 'if conditional (begin consequent) (begin alternative))
        (list 'if conditional (begin consequent))))))

(def var-ref
  (bind [id identifier]
    (return (symbol id))))

(def factor
  (<|> dec-lit lambda (<:> funcall) var-ref (parens (fwd expr))))

(def assign
  (bind [id identifier
         _ (token "=")
         ex expr]
    (return (list 'set! (symbol id) ex))))

(defn prefix1
  [p op]
  (<|> (bind [f op
              a (prefix1 p op)]
          (return (list f a)))
       (bind [a p] (return a))))

(defn chainl1
  [p op]
  (letfn [(rest [a] (<|> (bind [f op b p] (rest (list f a b)))
                         (return a)))]
    (bind [a p] (rest a))))

(defn chainr1
  [p op]
  (bind [a p]
        (<|> (bind [f op b (chainr1 p op)]
                   (return (list f a b)))
             (return a))))

(defn ops
  [& symbols]
  (bind [op (apply token symbols)]
    (return (symbol op))))

(defn rename
  [op n]
  (>> (token op) (return n)))

(def unary (prefix1 factor (ops "!" "-")))
(def power (chainr1 unary  (ops "^")))
(def term  (chainl1 power  (ops "*" "/" "%")))
(def sum   (chainl1 term   (ops "+" "-")))
(def relex (chainl1 sum    (ops "==" "!=" ">=" "<=" ">" "<")))
(def orex  (chainl1 relex  (rename "&&" 'and)))
(def expr  (chainl1 orex   (rename "||" 'or)))

(def stmt (<|> (<:> assign) condition expr))
(def prog (bind [stmts (many1 stmt)]
                (return (apply list stmts))))

(defn parse
  ([input] (parse input prog))
  ([input parser]
   (let [result (kern/parse parser input)]
     (when-not (:ok result)
       (print-error result)
       (throw (RuntimeException. "Parsing failed")))
     (:value result))))
