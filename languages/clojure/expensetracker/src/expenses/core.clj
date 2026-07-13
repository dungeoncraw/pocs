(ns expenses.core)
(def expenses
  [{:id          1
    :description "Lunch"
    :category    :food
    :amount      35.50
    :date        "2026-07-01"}

   {:id          2
    :description "Bus ticket"
    :category    :transport
    :amount      4.80
    :date        "2026-07-01"}

   {:id          3
    :description "Coffee"
    :category    :food
    :amount      8.00
    :date        "2026-07-02"}

   {:id          4
    :description "Book"
    :category    :education
    :amount      55.00
    :date        "2026-07-03"}

   {:id          5
    :description "Internet bill"
    :category    :bills
    :amount      120.00
    :date        "2026-07-05"}

   {:id          6
    :description "Uber"
    :category    :transport
    :amount      27.90
    :date        "2026-07-06"}])


(def valid-categories
  #{:food :transport :education})

(defn next-id
  [expenses]
  (inc
    (apply max
           (map :id expenses))))

(defn valid-category?
  [category]
  (contains? valid-categories category))

(defn positive-amount?
  [amount]
  (> amount 0))

(defn find-expense
  [expenses expense-id]
  (filter #(= (:id %) expense-id)
          expenses))

(defn expense-exists?
  [expenses expense-id]
  (some? (find-expense expenses expense-id)))


(defn add-expense
  [expenses description category amount date]
  (cond
    (not (valid-category? category))
    {:success  false
     :error    :invalid-category
     :expenses expenses}

    (not (positive-amount? amount))
    {:success  false
     :error    :invalid-amount
     :expenses expenses}

    :else
    {:success   true
     :operation :add-expense
     :expenses
     (conj expenses
           {:id          (next-id expenses)
            :description description
            :category    category
            :amount      amount
            :date        date})}))


(defn update-expense
  [expenses expense-id update-fn]
  (mapv
    (fn [expense]
      (if (= (:id expense) expense-id)
        (update-fn expense)
        expense))
    expenses))


(defn change-category
  [expenses expense-id new-category]
  (cond
    (not (expense-exists? expenses expense-id))
    {:success  false
     :error    :expense-not-found
     :expenses expenses}

    (not (valid-category? new-category))
    {:success  false
     :error    :invalid-category
     :expenses expenses}

    :else
    {:success   true
     :operation :change-category
     :expenses
     (update-expense
       expenses
       expense-id
       #(assoc % :category new-category))}))


(defn delete-expense
  [expenses expense-id]
  (if (expense-exists? expenses expense-id)
    {:success   true
     :operation :delete-expense
     :expenses
     (vec
       (remove #(= (:id %) expense-id)
               expenses))}
    {:success  false
     :error    :expense-not-found
     :expenses expenses}))

(defn expenses-by-category
  [expenses category]
  (filter
    #(= (:category %) category)
    expenses))


(defn expenses-over
  [expenses minimum-amount]
  (filter
    #(> (:amount %) minimum-amount)
    expenses))


(defn expenses-on-date
  [expenses date]
  (filter
    #(= (:date %) date)
    expenses))


(defn total-amount
  [expenses]
  (reduce
    +
    (map :amount expenses)))


(defn average-expense
  [expenses]
  (if (empty? expenses)
    0
    (/ (total-amount expenses)
       (count expenses))))


(defn most-expensive
  [expenses]
  (first
    (sort-by :amount > expenses)))


(defn cheapest
  [expenses]
  (first
    (sort-by :amount expenses)))



(defn group-expenses-by-category
  [expenses]
  (group-by :category expenses))


(defn category-total
  [expenses]
  (total-amount expenses))


(defn totals-by-category
  [expenses]
  (let [grouped (group-expenses-by-category expenses)]
    (into {}
          (map
            (fn [[category expenses-in-category]]
              [category (category-total expenses-in-category)])
            grouped))))

(defn totals-by-date
  [expenses]
  (into {}
        (map (fn [[date expenses-on-date]]
               [date (reduce + 0 (map :amount expenses-on-date))])
             (group-by :date expenses)
             )))
(defn expenses-between
  [expenses min-amount max-amount]
  (filter
    #(and (>= (:amount %) min-amount)
          (<= (:amount %) max-amount))
    expenses)
  )

(defn count-by-category
  [expenses]
  (frequencies (map :category expenses)))
(defn category-summary
  [expenses]
  (let [grouped (group-expenses-by-category expenses)]
    (into {}
          (map
            (fn [[category expenses-in-category]]
              [category
               {:count   (count expenses-in-category)
                :total   (total-amount expenses-in-category)
                :average (average-expense expenses-in-category)}])
            grouped))))


(defn expense-report
  [expenses]
  {:total              (total-amount expenses)
   :count              (count expenses)
   :average            (average-expense expenses)
   :most-expensive     (most-expensive expenses)
   :cheapest           (cheapest expenses)
   :totals-by-category (totals-by-category expenses)
   :category-summary   (category-summary expenses)
   :count-by-category  (count-by-category expenses)
   :totals-by-date     (totals-by-date expenses)})


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
    (println "-" date ":" (format-money total))))


(defn print-result
  [result]
  (if (:success result)
    (println "Success:" (:operation result))
    (println "Error:" (:error result)))

  (print-expenses (:expenses result)))


(defn -main
  []
  (println "All expenses:")
  (print-expenses expenses)

  (println "\nFood expenses:")
  (print-expenses (expenses-by-category expenses :food))

  (println "\nExpenses over R$ 30:")
  (print-expenses (expenses-over expenses 30))

  (println "\nTotal amount:")
  (println (format-money (total-amount expenses)))

  (println "\nTotals by category:")
  (doseq [entry (totals-by-category expenses)]
    (print-category-total entry))

  (println "\nAdding new expense:")
  (let [result1 (add-expense expenses
                             "Movie ticket"
                             :entertainment
                             42.00
                             "2026-07-07")]
    (print-result result1)

    (println "\nChanging category of expense 3:")
    (let [result2 (change-category (:expenses result1)
                                   3
                                   :entertainment)]
      (print-result result2)

      (println "\nDeleting expense 2:")
      (let [result3 (delete-expense (:expenses result2)
                                    2)]
        (print-result result3)

        (println "\nFinal report:")
        (print-report (expense-report (:expenses result3)))))))