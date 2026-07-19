(ns todo.printing)


(defn print-title
  [title]
  (println)
  (println "--------------------------------------------------")
  (println title)
  (println "--------------------------------------------------"))


(defn todo-summary
  [todo]
  (str "#"
       (:id todo)
       " | "
       (:title todo)
       " | done: "
       (:done todo)
       " | priority: "
       (:priority todo)))


(defn print-todo
  [todo]
  (if todo
    (println "-" (todo-summary todo))
    (println "- not found")))


(defn print-todos
  [todos]
  (doseq [todo todos]
    (print-todo todo)))
