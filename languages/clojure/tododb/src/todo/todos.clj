(ns todo.todos
  (:require [next.jdbc :as jdbc]
            [todo.db :refer [datasource query-options]]))


(def valid-priorities #{"low" "normal" "high"})


(defn valid-priority?
  [priority]
  (contains? valid-priorities priority))


(defn create-todo!
  [title]
  (jdbc/execute-one!
    datasource
    ["insert into todos (title)
     values (?)
     returning id, title, done, priority, created_at"
     title]
    query-options))


(defn create-todo-with-priority!
  [title priority]
  (when-not (valid-priority? priority)
    (throw (ex-info "Invalid priority"
                    {:priority         priority
                     :valid-priorities valid-priorities})))
  (jdbc/execute-one!
    datasource
    ["insert into todos (title, priority)
     values (?, ?)
     returning id, title, done, priority, created_at"
     title
     priority]
    query-options))


(defn find-todo-by-id
  [id]
  (jdbc/execute-one!
    datasource
    ["select id, title, done, priority, created_at
     from todos
     where id = ?"
     id]
    query-options))


(defn list-todos
  []
  (jdbc/execute!
    datasource
    ["select id, title, done, priority, created_at
     from todos
     order by id"]
    query-options))


(defn list-pending-todos
  []
  (jdbc/execute!
    datasource
    ["select id, title, done, priority, created_at
     from todos
     where done = false
     order by id"]
    query-options))


(defn list-completed-todos
  []
  (jdbc/execute!
    datasource
    ["select id, title, done, priority, created_at
     from todos
     where done = true
     order by id"]
    query-options))


(defn search-todos
  [query]
  (jdbc/execute!
    datasource
    ["select id, title, done, priority, created_at
     from todos
     where lower(title) like lower(?)
     order by id"
     (str "%" query "%")]
    query-options))


(defn mark-done!
  [id]
  (jdbc/execute-one!
    datasource
    ["update todos
     set done = true
     where id = ?
     returning id, title, done, priority, created_at"
     id]
    query-options))


(defn mark-pending!
  [id]
  (jdbc/execute-one!
    datasource
    ["update todos
     set done = false
     where id = ?
     returning id, title, done, priority, created_at"
     id]
    query-options))


(defn rename-todo!
  [id new-title]
  (jdbc/execute-one!
    datasource
    ["update todos
     set title = ?
     where id = ?
     returning id, title, done, priority, created_at"
     new-title
     id]
    query-options))


(defn change-priority!
  [id priority]
  (when-not (valid-priority? priority)
    (throw (ex-info "Invalid priority"
                    {:priority         priority
                     :valid-priorities valid-priorities})))
  (jdbc/execute-one!
    datasource
    ["update todos
     set priority = ?
     where id = ?
     returning id, title, done, priority, created_at"
     priority
     id]
    query-options))


(defn list-todos-by-priority
  [priority]
  (jdbc/execute!
    datasource
    ["select id, title, done, priority, created_at
     from todos
     where priority = ?
     order by id"
     priority]
    query-options))


(defn toggle-todo!
  [id]
  (jdbc/execute-one!
    datasource
    ["update todos
     set done = not done
     where id = ?
     returning id, title, done, priority, created_at"
     id]
    query-options))


(defn delete-todo!
  [id]
  (jdbc/execute-one!
    datasource
    ["delete from todos
     where id = ?
     returning id, title, done, priority, created_at"
     id]
    query-options))


(defn delete-completed-todos!
  []
  (jdbc/execute!
    datasource
    ["delete from todos
     where done = true
     returning id, title, done, priority, created_at"]
    query-options))


(defn todo-counts
  []
  (jdbc/execute-one!
    datasource
    ["select
       count(*) as total,
       count(*) filter (where done = true) as completed,
       count(*) filter (where done = false) as pending
     from todos"]
    query-options))


(defn todos-by-priority
  []
  (jdbc/execute!
    datasource
    ["select priority, count(*) as total
     from todos
     group by priority
     order by priority"]
    query-options))


(defn create-default-todos!
  []
  (jdbc/with-transaction [tx datasource]
                         [(jdbc/execute-one!
                            tx
                            ["insert into todos (title, priority)
        values (?, ?)
        returning id, title, done, priority, created_at"
                             "Learn Clojure syntax"
                             "high"]
                            query-options)

                          (jdbc/execute-one!
                            tx
                            ["insert into todos (title, priority)
        values (?, ?)
        returning id, title, done, priority, created_at"
                             "Practice next.jdbc"
                             "normal"]
                            query-options)

                          (jdbc/execute-one!
                            tx
                            ["insert into todos (title, priority)
        values (?, ?)
        returning id, title, done, priority, created_at"
                             "Build a small CLI"
                             "low"]
                            query-options)]))
