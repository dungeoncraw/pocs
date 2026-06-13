(ns shopping-cart.data)

(def products
  [{:id 1
    :name "Keyboard"
    :category :electronics
    :price 100.00}

   {:id 2
    :name "Mouse"
    :category :electronics
    :price 50.00}

   {:id 3
    :name "Notebook"
    :category :stationery
    :price 20.00}

   {:id 4
    :name "Pen"
    :category :stationery
    :price 5.00}])

(def cart
  [{:product-id 1
    :quantity 1}

   {:product-id 2
    :quantity 2}

   {:product-id 4
    :quantity 5}])
