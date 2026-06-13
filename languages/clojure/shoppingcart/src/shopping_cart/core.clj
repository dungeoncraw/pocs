(ns shopping-cart.core
  (:require [shopping-cart.data :as data]
            [shopping-cart.logic :as logic]
            [shopping-cart.cart-ops :as cart-ops]
            [shopping-cart.ui :as ui]))

(defn -main
  []
  (println "Original checkout:")
  (ui/print-checkout (logic/checkout data/products data/cart))

  (println "\nAfter adding notebook:")
  (let [updated-cart (cart-ops/add-item data/cart 3 3)
        result (logic/checkout data/products updated-cart)]
    (ui/print-checkout result))

  (println "\nAfter removing mouse:")
  (let [updated-cart (cart-ops/remove-item data/cart 2)
        result (logic/checkout data/products updated-cart)]
    (ui/print-checkout result)))
