(ns bankaccount.core
  (:require [bankaccount.data :refer [accounts]]
            [bankaccount.operations :refer [deposit withdraw transfer]]
            [bankaccount.printing :refer [print-accounts print-result]]))

(defn -main
  []
  (println "Initial accounts:")
  (print-accounts accounts)

  (println "\nDeposit 200 into Ana's account:")
  (let [result (deposit accounts 1 200)]
    (print-result result)

    (println "\nWithdraw 100 from Bruno's account:")
    (let [result2 (withdraw (:accounts result) 2 100)]
      (print-result result2)

      (println "\nTransfer 300 from Ana to Carla:")
      (let [result3 (transfer (:accounts result2) 1 3 300)]
        (print-result result3)

        (println "\nTry to withdraw 9999 from Carla:")
        (let [result4 (withdraw (:accounts result3) 3 9999)]
          (print-result result4))))))
