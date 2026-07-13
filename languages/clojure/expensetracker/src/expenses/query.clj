(ns expenses.query)

(defn find-expense
  [expenses expense-id]
  (filter #(= (:id %) expense-id)
          expenses))

(defn expense-exists?
  [expenses expense-id]
  (some? (find-expense expenses expense-id)))

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

(defn expenses-between
  [expenses min-amount max-amount]
  (filter
    #(and (>= (:amount %) min-amount)
          (<= (:amount %) max-amount))
    expenses))
