(ns todo.core
  (:require [todo.db :as db]
            [todo.todos :as todos]
            [todo.printing :refer [print-title print-todo print-todos]]))


(defn -main
  []
  (print-title "Reset database")
  (db/reset-db!)
  (println "Todo database ready.")

  (print-title "Create default todos inside transaction")
  (print-todos (todos/create-default-todos!))

  (print-title "Create one more todo")
  (let [todo (todos/create-todo-with-priority!
               "Review immutable data structures"
               "high")]
    (print-todo todo)

    (print-title "List all todos")
    (print-todos (todos/list-todos))

    (print-title "Find todo by ID")
    (print-todo (todos/find-todo-by-id (:id todo)))

    (print-title "Search todos containing: Clojure")
    (print-todos (todos/search-todos "Clojure"))

    (print-title "Mark todo as done")
    (print-todo (todos/mark-done! (:id todo)))

    (print-title "Rename todo")
    (print-todo
      (todos/rename-todo!
        (:id todo)
        "Review Clojure immutable data structures"))

    (print-title "Change priority")
    (print-todo
      (todos/change-priority! (:id todo) "normal"))

    (print-title "Pending todos")
    (print-todos (todos/list-pending-todos))

    (print-title "Completed todos")
    (print-todos (todos/list-completed-todos))

    (print-title "Todo counts")
    (println (todos/todo-counts))

    (print-title "Todos by priority")
    (println (todos/todos-by-priority))

    (print-title "Delete completed todos")
    (print-todos (todos/delete-completed-todos!))

    (print-title "Final todos")
    (print-todos (todos/list-todos))))
