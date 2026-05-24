(ns core.core)

(def todos
  [{:id 1 :title "Learn def" :done false}
   {:id 2 :title "Learn maps" :done false}
   {:id 3 :title "Learn filter" :done true}])

(defn add-todo
  [todos title]
  (conj todos {:id (inc (count todos))
               :title title
               :done false}))

(defn complete-todo
  [todos id]
  (mapv
    (fn [todo]
      (if (= (:id todo) id)
        (assoc todo :done true)
        todo))
    todos))

(defn pending-todos
  [todos]
  (filter #(false? (:done %)) todos))

(defn completed-todos
  [todos]
  (filter :done todos))

(defn todo-summary
  [todo]
  (str (:id todo) " - " (:title todo) " - done? " (:done todo)))

(defn print-todos
  [todos]
  (doseq [todo todos]
    (println (todo-summary todo))))

(defn -main
  []
  (println "Original todos:")
  (print-todos todos)

  (println "\nAfter adding a todo:")
  (def updated-todos (add-todo todos "Learn reduce"))
  (print-todos updated-todos)

  (println "\nAfter completing todo 1:")
  (def completed-list (complete-todo updated-todos 1))
  (print-todos completed-list)

  (println "\nPending todos:")
  (print-todos (pending-todos completed-list)))