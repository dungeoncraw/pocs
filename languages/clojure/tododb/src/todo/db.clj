(ns todo.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))


(def db-spec
  {:dbtype "postgresql"
   :host "localhost"
   :port 5432
   :dbname "todo_app"
   :user "todo_user"
   :password "todo_password"})

(def datasource
  (jdbc/get-datasource db-spec))

(def query-options
  {:builder-fn rs/as-unqualified-lower-maps})


(defn migrate!
  []
  (jdbc/execute!
    datasource
    ["create table if not exists todos (
       id bigserial primary key,
       title text not null,
       done boolean not null default false,
       priority text not null default 'normal',
       created_at timestamptz not null default now()
     )"]))


(defn reset-db!
  []
  (jdbc/execute! datasource ["drop table if exists todos"])
  (migrate!))
