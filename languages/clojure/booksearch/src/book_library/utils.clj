(ns book-library.utils
  (:require [clojure.string :as str]))

(defn normalize
  [text]
  (str/lower-case text))

(defn contains-text?
  [text query]
  (str/includes?
    (normalize text)
    (normalize query)))

(defn book-summary
  [book]
  (str (:title book)
       " | "
       (:author book)
       " | "
       (:year book)
       " | "
       (:genre book)))

(defn print-books
  [books]
  (doseq [book books]
    (println "-" (book-summary book))))
