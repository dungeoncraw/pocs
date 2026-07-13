(ns expenses.validation
  (:require [expenses.data :as data]))

(defn valid-category?
  [category]
  (contains? data/valid-categories category))

(defn positive-amount?
  [amount]
  (> amount 0))
