(ns shopping-cart.cart-ops)

(defn same-product?
  [product-id cart-item]
  (= (:product-id cart-item) product-id))

(defn item-exists?
  [cart product-id]
  (some
    #(same-product? product-id %)
    cart))

(defn increase-item-quantity
  [cart product-id quantity]
  (mapv
    (fn [cart-item]
      (if (same-product? product-id cart-item)
        (update cart-item :quantity + quantity)
        cart-item))
    cart))

(defn add-item
  [cart product-id quantity]
  (if (item-exists? cart product-id)
    (increase-item-quantity cart product-id quantity)
    (conj cart {:product-id product-id
                :quantity quantity})))

(defn remove-item
  [cart product-id]
  (vec
    (remove
      #(same-product? product-id %)
      cart)))
