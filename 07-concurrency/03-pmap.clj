;;; Illustrates the performance characteristics of pmap. It makes a comparison with
;;; map on two different datasets - one that involves a slow operation and one that
;;; involves a faster one.
;;;
;;; The dataset would be "files" containing phone numbers and the operation
;;; will be extracting the phone numbers from the files. Each file will have a
;;; bunch of spaces before the actual phone numbers. We will have two
;;; collection of files - one that has a small amount of large files and one
;;; that has a large amount of small files. pmap will have better performance
;;; in the first case and worse in the second.
;;;
;;; The reason for this is that in the second case, the overhead of
;;; coordinating the threads is larger than the benefit form paralellizing the
;;; operation.
;;;
;;; That problem can be solved by grouping the small files into chunks (say 250)
;;; and processing the chunks with pmap.

;; This function extracts a phone number from a file
(defn phone-numbers
  [string]
  (re-seq #"(\d{3})[\.-]?(\d{3})[\.-]?(\d{4})" string))

;; Small amount of large files
(def few-large-files
  (repeat 100
          (apply str (concat (repeat 1000000 \space)
                             "Sunil: 617.555.2937, Betty: 508.555.2218"))))

;; Large amount of small files
(def many-small-files
  (repeat 100000
          (apply str (concat (repeat 100 \space)
                             "Sunil: 617.555.2937, Betty: 508.555.2218"))))
;; Our reporting function
(defn report
  [report-name map-fn coll]
  (print report-name "")
  (time (dorun (map-fn phone-numbers coll))))

;; Comparison of map and pmap
(report "map   on few large files:" map few-large-files)
(report "pmap  on few large files:" pmap few-large-files)
(report "map  on many small files:" map many-small-files)
(report "pmap on many small files:" pmap many-small-files)

;; Solving many-small-files with pmap
(print "pmap  with some chunking: ")
(time (->> many-small-files
           (partition-all 250)
           (pmap (fn [chunk] (doall (map phone-numbers chunk))))
           (apply concat)
           dorun))

;; A sample output might be:
;;
;;   → clj 03-pmap.clj
;;   map   on few large files: "Elapsed time: 1106.56 msecs"
;;   pmap  on few large files: "Elapsed time: 306.21 msecs"
;;   map  on many large files: "Elapsed time: 320.668 msecs"
;;   pmap on many large files: "Elapsed time: 555.799 msecs"
;;   pmap  with some chunking: "Elapsed time: 99.447 msecs"

(shutdown-agents)
