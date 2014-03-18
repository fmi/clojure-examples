(ns life.gui
  (use [seesaw core color graphics behave]
       life.state))

(def cell-size 20)
(def board-margin 10)

(defn project [x]
  (+ board-margin (* x cell-size)))

(defn rect-from [[x y]]
  (letfn [(project [n] (+ board-margin (* n cell-size)))]
    (rect (project x) (project y) cell-size cell-size)))

(defn cell-from [point]
  (letfn [(project [n] (int (/ (- n board-margin) cell-size)))]
    [(-> point .x project)
     (-> point .y project)]))

(defn draw-world [c g]
  (doseq [cell (living-cells)]
    (draw g (rect-from cell) (style :background :green))))

(defn make-panel []
  (border-panel
    :north (flow-panel :align :center
                       :items [(button :text "<<" :class :prev)
                               (label :text "--" :class :current)
                               (label :text "/")
                               (label :text "--" :class :total)
                               (button :text ">>" :class :next)
                               (button :text "Start/Stop" :class :tick)])
    :center (canvas :paint draw-world
                    :class :world
                    :background :black)
    :vgap 5
    :hgap 5
    :border 5))

(defn make-frame []
  (frame :title   "Game of life"
         :size    [600 :by 600]
         :content (make-panel)))


(defn redisplay [root]
  (config! (select root [:.world])   :paint draw-world)
  (config! (select root [:.current]) :text (current-generation))
  (config! (select root [:.total])   :text (generation-count)))

(declare ticker)

(defn add-behaviors [root]
  (listen (select root [:.world]) :mouse-clicked (fn [e] (toggle-cell! (cell-from (.getPoint e))) (redisplay root)))
  (listen (select root [:.prev])  :mouse-clicked (fn [e] (previous-generation!) (redisplay root)))
  (listen (select root [:.next])  :mouse-clicked (fn [e] (next-generation!) (redisplay root)))
  (listen (select root [:.tick])  :mouse-clicked (fn [e] (if (.isRunning ticker) (.stop ticker) (.start ticker)))))

(defonce the-frame (make-frame))
(defonce ticker (timer (fn [_] (next-generation!) (redisplay the-frame))
                       :delay 500
                       :start? false))

(defn start-game []
  (native!)
  (config! the-frame :content (make-panel))
  (show! the-frame)
  (add-behaviors the-frame)
  (redisplay the-frame))
