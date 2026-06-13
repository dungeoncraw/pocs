(ns shopping-cart.logic)

(defn find-product
  [products product-id]
  (first
    (filter
      #(= (:id %) product-id)
      products)))

(defn line-subtotal
  [product quantity]
  (* (:price product) quantity))

(defn build-cart-item
  [products cart-item]
  (let [product-id (:product-id cart-item)
        quantity (:quantity cart-item)
        product (find-product products product-id)
        subtotal (line-subtotal product quantity)]
    (assoc cart-item
      :product product
      :subtotal subtotal)))

(defn cart-lines
  [products cart]
  (map
    #(build-cart-item products %)
    cart))

(defn cart-subtotal
  [lines]
  (reduce
    +
    (map :subtotal lines)))

(defn discount-rate
  [subtotal]
  (cond
    (>= subtotal 300) 0.20
    (>= subtotal 200) 0.10
    (>= subtotal 100) 0.05
    :else 0.00))

(defn discount-value
  [subtotal]
  (* subtotal (discount-rate subtotal)))

(def tax-rate 0.10)

(defn tax-value
  [amount-after-discount]
  (* amount-after-discount tax-rate))

(defn checkout
  [products cart]
  (let [lines (cart-lines products cart)
        subtotal (cart-subtotal lines)
        discount (discount-value subtotal)
        amount-after-discount (- subtotal discount)
        tax (tax-value amount-after-discount)
        total (+ amount-after-discount tax)]
    {:lines lines
     :subtotal subtotal
     :discount discount
     :amount-after-discount amount-after-discount
     :tax tax
     :total total}))
