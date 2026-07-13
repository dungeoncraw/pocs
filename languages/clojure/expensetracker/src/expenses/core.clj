(ns expenses.core
  (:require [expenses.data :as data]
            [expenses.query :as q]
            [expenses.operations :as ops]
            [expenses.stats :as stats]
            [expenses.report :as report]
            [expenses.print :as p]))

(defn -main
  []
  (println "All expenses:")
  (p/print-expenses data/expenses)

  (println "\nFood expenses:")
  (p/print-expenses (q/expenses-by-category data/expenses :food))

  (println "\nExpenses over R$ 30:")
  (p/print-expenses (q/expenses-over data/expenses 30))

  (println "\nExpenses on 2026-07-01:")
  (p/print-expenses (q/expenses-on-date data/expenses "2026-07-01"))

  (println "\nExpenses between R$ 10 and R$ 50:")
  (p/print-expenses (q/expenses-between data/expenses 10 50))

  (println "\nFinding expense with id 1:")
  (p/print-expenses (q/find-expense data/expenses 1))

  (println "\nTotal amount:")
  (println (p/format-money (stats/total-amount data/expenses)))

  (println "\nTotals by category:")
  (doseq [entry (stats/totals-by-category data/expenses)]
    (p/print-category-total entry))

  (println "\nAdding new expense:")
  (let [result1 (ops/add-expense data/expenses
                                 "Movie ticket"
                                 :entertainment
                                 42.00
                                 "2026-07-07")]
    (p/print-result result1)

    (println "\nChanging category of expense 3:")
    (let [result2 (ops/change-category (:expenses result1)
                                       3
                                       :entertainment)]
      (p/print-result result2)

      (println "\nDeleting expense 2:")
      (let [result3 (ops/delete-expense (:expenses result2)
                                        2)]
        (p/print-result result3)

        (println "\nFinal report:")
        (p/print-report (report/expense-report (:expenses result3)))))))
