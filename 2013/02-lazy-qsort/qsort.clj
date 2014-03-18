(def comparisons (atom 0))

(defn sort-parts [work]
  (lazy-seq
    (loop [[part & parts] work]
      (if-let [[pivot & xs] (seq part)]
        (letfn [(smaller? [x] (swap! comparisons inc) (< x pivot))]
          (recur (list*
                   (filter smaller? xs)
                   pivot
                   (remove smaller? xs)
                   parts)))
        (when-let [[x & parts] parts]
          (cons x (sort-parts parts)))))))

(defn lazy-qsort [xs]
  (sort-parts (list xs)))

(defn random-numbers [n]
  (take n (repeatedly #(rand-int n))))

(let [numbers (random-numbers 100000)
      sorted (lazy-qsort numbers)]
  (doall (take 10 sorted))
  (printf "Comparisons for the first 10:  %s\n" @comparisons)
  (doall sorted)
  (printf "Comparisons for the whole seq: %s\n" @comparisons))
