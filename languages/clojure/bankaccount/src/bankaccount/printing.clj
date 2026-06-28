(ns bankaccount.printing)

(defn account-summary
  [account]
  (str "Account "
       (:id account)
       " | owner: "
       (:owner account)
       " | balance: "
       (:balance account)))

(defn print-accounts
  [accounts]
  (doseq [account accounts]
    (println "-" (account-summary account))))

(defn print-result
  [result]
  (if (:success result)
    (println "Success:" (:operation result))
    (println "Error:" (:error result)))

  (print-accounts (:accounts result)))
