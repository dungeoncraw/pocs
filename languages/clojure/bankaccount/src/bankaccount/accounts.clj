(ns bankaccount.accounts)

(defn find-account
  [accounts account-id]
  (first (filter #(= (:id %) account-id) accounts)))

(defn account-exists?
  [accounts account-id]
  (some? (find-account accounts account-id)))

(defn positive-amount?
  [amount]
  (> amount 0))

(defn enough-balance?
  [account amount]
  (>= (:balance account) amount))

(defn update-account
  [accounts account-id update-fn]
  (mapv
    (fn [account]
      (if (= (:id account) account-id)
        (update-fn account)
        account))
    accounts))
