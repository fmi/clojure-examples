(use '[clojure.set :only (superset? union)]
     '[clojure.string :only (lower-case)])

(defn type-in [type-predicate]
  (fn [player]
    (map val (filter #(type-predicate (val %)) player))))

(def vectors-in (type-in vector?))
(def maps-in (type-in map?))
(def sets-in (type-in set?))
(def fns-in (type-in fn?))
(def lazy-seqs-in (type-in #(instance? clojure.lang.LazySeq %)))

(defn starts-with-last-three? [v0 v1]
  (and
    (>= (count v0) 3)
    (>= (count v1) 3)
    (= (take 3 v0) (take-last 3 v1))))

(defn sum [coll]
  (apply + coll))

(defn square [n]
  (* n n))

(defn square-sums [vec-of-lists]
  (for [sub-list vec-of-lists] (sum (map square sub-list))))

(defn average [coll]
  (if (seq coll)
    (/ (sum coll) (count coll))
    0))

(defn numbers [player]
  (letfn [(set-vals-in [player] (map seq (sets-in player)))
          (map-vals-in [player] (map vals (maps-in player)))]
    (flatten ((juxt vectors-in map-vals-in set-vals-in) player))))

(defn fibonacci
  ([] (fibonacci 1 1))
  ([a b] (cons a (lazy-seq (fibonacci b (+ b a))))))

(defn commutative-fibonacci? [f]
  (letfn [(returns-111? [[x y]] (= 111 (f x y)))
          (commutative? [[x y]] (= (f x y) (f y x)))]
    (and
      (= 0 (f 0 0))
      (every? returns-111? (for [x (fibonacci) :while (< x 100)
                                 y (fibonacci) :while (< y 100)]
                             [x y]))
      (every? commutative? (for [x (range 0 100)
                                 y (range 0 100) :when (<= x y)]
                             [x y])))))

(defn lazy-pair? [sets [l0 l1]]
  (let [uber-set (apply union sets)]
    (letfn [(pairs [] (for [x l0 y l1] [x y]))
            (sum-and-product [[x y]] [(+ x y) (* x y)])
            (sums-and-products [] (flatten (map sum-and-product (pairs))))]
      (and
        (every? uber-set (sums-and-products))
        (let [set-of-sums-and-products (set (sums-and-products))]
          (some #(subset? set-of-sums-and-products %) sets))))))

(defn lazy-seqs-pairs [lazy-seqs]
  (for [l0 lazy-seqs l1 lazy-seqs :while (not (identical? l0 l1))] [l0 l1]))

(defn rule-uff [player _]
  (let [maps (maps-in player)]
    (letfn [(make-uff-cmp [m]
              (letfn [(digits [n]
                        (count (remove #{\-} (seq (str n)))))
                      (count-halfdentities [n]
                        (count (filter #(= n (% n n)) (fns-in player))))
                      (sort-value-for-key [k]
                        [(digits (k m)) (- (count-halfdentities (k m))) k])]
                (fn [key0 key1]
                  (compare (sort-value-for-key key0) (sort-value-for-key key1)))))
            (uff? [m]
              (let [cmp (make-uff-cmp m)
                    other-maps (remove (partial identical? m) maps)
                    value-greater-than-sum-of-same-key-values? (fn [[k v]]
                                                                 (> v (sum (remove nil? (map k other-maps)))))
                    sorted-m (into (sorted-map-by cmp) m)
                    first-six-keys (set (map
                                          (comp lower-case name)
                                          (take 6 (keys sorted-m))))]
                (and
                  (= first-six-keys #{"list" "vector" "map" "set" "lazyseq" "cons"})
                  (every? value-greater-than-sum-of-same-key-values? m))))]
      (* 3 (count (filter uff? maps))))))

(defn rule-lazy-pairs [player _]
  (let [lazy-seqs (lazy-seqs-in player)
        sets (sets-in player)]
    (* 5
       (count (filter
                (partial lazy-pair? sets)
                (lazy-seqs-pairs lazy-seqs))))))

(defn rule-commutative-fibonacci [player _]
  (count (filter commutative-fibonacci? (fns-in player))))

(defn rule-averages [player other-player]
  (let [vectors (vectors-in player)
        other-player-average (average (numbers other-player))]
    (count
      (filter
        #(> (average (square-sums %)) other-player-average)
        vectors))))

(defn rule-first-last-three [player _]
  (let [vectors (vectors-in player)]
    (sum (for [v0 vectors
               v1 vectors
               :when (not (identical? v0 v1))
               :when (starts-with-last-three? v0 v1)]
           1))))

(defn points-for-player [player other-player]
  (let [rules [rule-first-last-three
               rule-averages
               rule-commutative-fibonacci
               rule-lazy-pairs
               rule-uff]]
    (sum (for [rule rules] (rule player other-player)))))

(defn game [p0 p1]
  [(points-for-player p0 p1) (points-for-player p1 p0)])

