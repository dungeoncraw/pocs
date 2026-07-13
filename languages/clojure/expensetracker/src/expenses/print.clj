(ns expenses.print)

(defn format-money
  [amount]
  (str "R$ " amount))

(defn expense-summary
  [expense]
  (str "#"
       (:id expense)
       " | "
       (:description expense)
       " | "
       (:category expense)
       " | "
       (format-money (:amount expense))
       " | "
       (:date expense)))

(defn print-expenses
  [expenses]
  (doseq [expense expenses]
    (println "-" (expense-summary expense))))

(defn print-category-total
  [[category total]]
  (println "-" category ":" (format-money total)))

(defn print-report
  [report]
  (println "Expense report")
  (println "Total:" (format-money (:total report)))
  (println "Count:" (:count report))
  (println "Average:" (format-money (:average report)))

  (println)
  (println "Most expensive:")
  (println "-" (expense-summary (:most-expensive report)))

  (println)
  (println "Cheapest:")
  (println "-" (expense-summary (:cheapest report)))

  (println)
  (println "Totals by category:")
  (doseq [entry (:totals-by-category report)]
    (print-category-total entry))

  (println)
  (println "Count by category:")
  (doseq [[category cnt] (:count-by-category report)]
    (println "-" category ":" cnt))

  (println)
  (println "Totals by date:")
  (doseq [[date total] (:totals-by-date report)]
    (println "-" date ":" (format-money total)))

  (println)
  (println "Totals by payment method:")
  (doseq [[payment-method total] (:totals-by-payment-method report)]
    (println "-" payment-method ":" (format-money total))))

(defn print-result
  [result]
  (if (:success result)
    (println "Success:" (:operation result))
    (println "Error:" (:error result)))

  (print-expenses (:expenses result)))
