(ns expenses.report
  (:require [expenses.stats :as s]))

(defn expense-report
  [expenses]
  {:total                    (s/total-amount expenses)
   :count                    (count expenses)
   :average                  (s/average-expense expenses)
   :most-expensive           (s/most-expensive expenses)
   :cheapest                 (s/cheapest expenses)
   :totals-by-category       (s/totals-by-category expenses)
   :category-summary         (s/category-summary expenses)
   :count-by-category        (s/count-by-category expenses)
   :totals-by-date           (s/totals-by-date expenses)
   :totals-by-payment-method (s/totals-by-payment-method expenses)})
