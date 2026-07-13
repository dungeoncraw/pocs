(ns expenses.stats)

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
             (group-by :date expenses))))

(defn totals-by-payment-method
  [expenses]
  (into {}
        (map (fn [[payment-method expenses-by-method]]
               [payment-method (reduce + 0 (map :amount expenses-by-method))])
             (group-by :payment-method expenses))))

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
