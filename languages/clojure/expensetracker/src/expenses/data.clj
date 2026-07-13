(ns expenses.data)

(def expenses
  [{:id             1
    :description    "Lunch"
    :category       :food
    :amount         35.50
    :date           "2026-07-01"
    :payment-method :credit-card}

   {:id             2
    :description    "Bus ticket"
    :category       :transport
    :amount         4.80
    :date           "2026-07-01"
    :payment-method :cash}

   {:id             3
    :description    "Coffee"
    :category       :food
    :amount         8.00
    :date           "2026-07-02"
    :payment-method :debit-card}

   {:id             4
    :description    "Book"
    :category       :education
    :amount         55.00
    :date           "2026-07-03"
    :payment-method :credit-card}

   {:id             5
    :description    "Internet bill"
    :category       :bills
    :amount         120.00
    :date           "2026-07-05"
    :payment-method :cash}

   {:id             6
    :description    "Uber"
    :category       :transport
    :amount         27.90
    :date           "2026-07-06"
    :payment-method :cash}])

(def valid-categories
  #{:food :transport :education})
