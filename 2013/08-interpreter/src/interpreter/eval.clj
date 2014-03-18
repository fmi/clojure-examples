(ns interpreter.eval)

(declare evaluate)

(defn error
  [& args]
  (throw (RuntimeException. (apply str args))))

(defn tag?
  [ast tag]
  (and (seq? ast)
       (= tag (first ast))))

(defn empty-env
  []
  {:bindings (atom {})
   :parent nil})

(defn extend-env
  [bindings parent]
  {:bindings (atom bindings) :parent parent})

(defn lookup
  [{:keys (bindings parent)} var-name]
  (cond (find @bindings var-name)
        (var-name @bindings)

        parent
        (recur parent var-name)

        :else
        (error "Unbound variable: " var-name)))

(defn modify
  [original-env var-name value]
  (loop [{:keys (bindings parent)} original-env]
    (cond (find @bindings var-name)
          (swap! bindings assoc var-name value)

          parent
          (recur parent)

          :else
          (swap! (:bindings original-env) assoc var-name value))))

(defn primitive
  [func]
  {:kind :primitive :code func})

(defrecord ConsCell [a d])
(defn default-env
  []
  (extend-env {'print (primitive prn)
               'twice (primitive (fn [a] (+ a a)))
               '+ (primitive +)
               '* (primitive *)
               '== (primitive =)
               '- (primitive -)}
              (empty-env)))

(defn lift
  [operator [_ left right] env]
  (operator (evaluate left env)
            (evaluate right env)))

(defn eval-set
  [[_ var-name expr] env]
  (modify env var-name (evaluate expr env))
  nil)

(defn eval-if
  [[_ condition consequent alternative] env]
  (if (evaluate condition env)
    (evaluate consequent env)
    (when alternative
      (evaluate alternative env))))

(defn eval-begin
  [[_ & asts] env]
  (loop [asts asts]
    (if (= (count asts) 1)
      (evaluate (first asts) env)
      (do (evaluate (first asts) env)
          (recur (rest asts))))))

(defn make-lambda
  [[_ args & body] env]
  {:kind :compound
   :args args
   :body (list* 'begin body)
   :env env})

(defn invoke
  [func params]
  (cond (= (:kind func) :primitive)
        (apply (:code func) params)

        (= (:kind func) :compound)
        (evaluate (:body func)
                  (extend-env (zipmap (:args func) params)
                              (:env func)))

        :else
        (error "Don't know how to invoke: " func)))

(defn evaluate
  [ast env]
  (cond (number? ast) ast
        (symbol? ast) (lookup env ast)

        (tag? ast '*) (lift * ast env)

        (tag? ast 'set!) (eval-set ast env)
        (tag? ast 'if) (eval-if ast env)

        (tag? ast 'begin) (eval-begin ast env)
        (tag? ast 'lambda) (make-lambda ast env)

        :else
        (invoke (evaluate (first ast) env)
                (map #(evaluate % env) (rest ast)))))
