(ns expenses.operations
  (:require [expenses.validation :as v]
            [expenses.query :as q]))

(defn next-id
  [expenses]
  (inc
    (apply max
           (map :id expenses))))

(defn add-expense
  [expenses description category amount date]
  (cond
    (not (v/valid-category? category))
    {:success  false
     :error    :invalid-category
     :expenses expenses}

    (not (v/positive-amount? amount))
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
    (not (q/expense-exists? expenses expense-id))
    {:success  false
     :error    :expense-not-found
     :expenses expenses}

    (not (v/valid-category? new-category))
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
  (if (q/expense-exists? expenses expense-id)
    {:success   true
     :operation :delete-expense
     :expenses
     (vec
       (remove #(= (:id %) expense-id)
               expenses))}
    {:success  false
     :error    :expense-not-found
     :expenses expenses}))
