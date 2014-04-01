;; ----------------------------------------------------------------------------
;; Find index

;; level 1
(defn find-index-1 [coll v]
  (let [r (map-indexed vector coll)
        r (filter (comp #{v} second) r)
        r (ffirst r)]
    r))

;; level 2
(defn find-index-2 [coll v]
  (->> coll
       (map-indexed vector)
       (filter (comp #{v} second))
       (ffirst)))

;; level 3
(defn find-index-3 [coll v]
  (.indexOf coll v))

#_(do

  (find-index-1 [:a :b :c] :b)
  (find-index-2 [:a :b :c] :b)
  (find-index-3 [:a :b :c] :b)

  )

;; ----------------------------------------------------------------------------
;; Merge sort

;; NOTE:
;;
;; - multi-arity functions don't work
;; - recursive results
;;

(defn merge* [[l & lrest :as left] [r & rrest :as right] result]
   (if (and (seq left)
            (seq right))
     (if (< l r)
       (merge* lrest right (conj result l))
       (merge* left rrest (conj result r)))
     (concat result left right)))

(defn merge [left right]
  (merge* left right []))

(defn merge-sort [coll]
  (let [len (count coll)]
    (if (> len 1)
      (let [[left right] (split-at (/ len 2) coll)]
        (merge (merge-sort left) (merge-sort right)))
      coll)))

#_(do

  (merge-sort [3 5 2 1 4 7 17 18 4])

  )
