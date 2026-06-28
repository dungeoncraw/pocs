(ns bankaccount.operations
  (:require [bankaccount.accounts :refer [find-account account-exists?
                                          positive-amount? enough-balance?
                                          update-account]]))

(defn deposit
  [accounts account-id amount]
  (cond
    (not (account-exists? accounts account-id))
    {:success false
     :error :account-not-found
     :accounts accounts}
    (not (positive-amount? amount))
    {:success false
     :error :invalid-amount
     :accounts accounts}
    :else
    {:success true
     :operation :deposit
     :amount amount
     :accounts
     (update-account
       accounts
       account-id
       #(update % :balance + amount))}))

(defn withdraw
  [accounts account-id amount]
  (let [account (find-account accounts account-id)]
    (cond
      (nil? account)
      {:success false
       :error :account-not-found
       :accounts accounts}

      (not (positive-amount? amount))
      {:success false
       :error :invalid-amount
       :accounts accounts}

      (not (enough-balance? account amount))
      {:success false
       :error :insufficient-balance
       :accounts accounts}

      :else
      {:success true
       :operation :withdraw
       :amount amount
       :accounts
       (update-account
         accounts
         account-id
         #(update % :balance - amount))})))

(defn transfer
  [accounts from-id to-id amount]
  (let [from-account (find-account accounts from-id)
        to-account (find-account accounts to-id)]
    (cond
      (nil? from-account)
      {:success false
       :error :from-account-not-found
       :accounts accounts}

      (nil? to-account)
      {:success false
       :error :to-account-not-found
       :accounts accounts}

      (= from-id to-id)
      {:success false
       :error :same-account-transfer
       :accounts accounts}

      (not (positive-amount? amount))
      {:success false
       :error :invalid-amount
       :accounts accounts}

      (not (enough-balance? from-account amount))
      {:success false
       :error :insufficient-balance
       :accounts accounts}

      :else
      (let [after-withdraw
            (update-account
              accounts
              from-id
              #(update % :balance - amount))

            after-deposit
            (update-account
              after-withdraw
              to-id
              #(update % :balance + amount))]
        {:success true
         :operation :transfer
         :from from-id
         :to to-id
         :amount amount
         :accounts after-deposit}))))
