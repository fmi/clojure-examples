(ns lt.objs.my-intro-page
  (:require [lt.object :as object]
            [lt.objs.command :as cmd]
            [lt.objs.tabs :as tabs])
  (:require-macros [lt.macros :refer [behavior defui]]))

;; ----------------------------------------------------------------------------
;; Play around

;; 1. Eval file (ctrl-enter)
;; 2. Connect to LightTable UI
;; 3. Change the code
;; 4. Eval

;; ----------------------------------------------------------------------------
;; UI

(defui docs []
  [:button "Light Table's docs"]
  :click (fn []
           (cmd/exec! :show-docs)))

(defui changelog []
  [:button "changelog"]
  :click (fn []
           (cmd/exec! :version)))

(defui custom [this]
  [:button "custom"]
  :click (fn []
           ;; DOM events trigger, object events.
           (object/raise this ::custom)))

;; ----------------------------------------------------------------------------
;; Behaviors
;;
;; Tabs specific behaviour. SEE: lt.objs.tabs
;; NOTE: We use global events :close and :destroy instead of local ::close and ::destroy.
;;
(behavior ::on-close-destroy
          :triggers #{:close}
          :desc "Destory the object on close."
          :reaction (fn [this]
                      (object/raise this :destroy)))

;; Custom behavior.
(behavior ::on-custom
          :triggers #{::custom}
          :reaction (fn [this]
                      (js/alert "Custom link.")))

;; Add custom behavior to cursom tag.
(object/tag-behaviors ::intro-tag [::on-custom])

;; ----------------------------------------------------------------------------
;; Object
;;
;; Crates an object template. Name should be globally unique.
;;
(object/object* ::intro
                :triggers [::into.click :other-event]
                :tags #{::intro-tag}
                :behaviors [::on-close-destroy]
                :name "Welcome"
                :state [1 2 3 4] ;; we can put stuff here using (object/upate!)
                :init (fn [this]
                        [:div#intro
                         [:p "Welcome: " (changelog) " / " (docs) " / " (custom this) "."]]))

;; Introspect object:
;; @(object/create ::intro)

;; ----------------------------------------------------------------------------
;; Commands

(cmd/command {:command ::show-my-intro
              :desc "Show my intro page"
              :exec (fn [this]
                      (let [intro (object/create ::intro)]
                        intro ;; see current state
                        (tabs/add! intro)
                        (tabs/active! intro)))})
