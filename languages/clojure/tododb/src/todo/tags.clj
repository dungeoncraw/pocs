(ns todo.tags
  (:require [next.jdbc :as jdbc]
            [todo.db :refer [datasource query-options]]))


(defn create-tag!
  [name]
  (jdbc/execute-one!
    datasource
    ["insert into tags (name)
     values (?)
     returning id, name"
     name]
    query-options))


(defn add-tag-to-todo!
  [todo-id tag-id]
  (jdbc/execute-one!
    datasource
    ["insert into todo_tags (todo_id, tag_id)
     values (?, ?)
     on conflict do nothing
     returning todo_id, tag_id"
     todo-id
     tag-id]
    query-options))


(defn list-tags-by-todo
  [todo-id]
  (jdbc/execute!
    datasource
    ["select t.id, t.name
     from tags t
     join todo_tags tt on tt.tag_id = t.id
     where tt.todo_id = ?
     order by t.name"
     todo-id]
    query-options))
