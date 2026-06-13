(ns shopping-cart.ui)

(defn print-line
  [line]
  (let [product (:product line)
        name (:name product)
        price (:price product)
        quantity (:quantity line)
        subtotal (:subtotal line)]
    (println name
             "| price:"
             price
             "| quantity:"
             quantity
             "| subtotal:"
             subtotal)))

(defn print-checkout
  [result]
  (println "Cart items:")
  (doseq [line (:lines result)]
    (print-line line))

  (println)
  (println "Subtotal:" (:subtotal result))
  (println "Discount:" (:discount result))
  (println "Amount after discount:" (:amount-after-discount result))
  (println "Tax:" (:tax result))
  (println "Total:" (:total result)))
