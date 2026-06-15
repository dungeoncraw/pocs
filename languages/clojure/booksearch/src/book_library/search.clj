(ns book-library.search
  (:require [book-library.utils :as utils]))

(defn search-by-title
  [books query]
  (filter
    #(utils/contains-text? (:title %) query)
    books))

(defn search-by-author
  [books query]
  (filter
    #(utils/contains-text? (:author %) query)
    books))

(defn search-by-genre
  [books genre]
  (filter
    #(= (:genre %) genre)
    books))

(defn search-by-year
  [books year]
  (filter
    #(= (:year %) year)
    books))

(defn published-after
  [books year]
  (filter
    #(> (:year %) year)
    books))

(defn has-tag?
  [book tag]
  (contains? (:tags book) tag))

(defn search-by-tag
  [books tag]
  (filter
    #(has-tag? % tag)
    books))

(defn matches-query?
  [book query]
  (or
    (utils/contains-text? (:title book) query)
    (utils/contains-text? (:author book) query)
    (utils/contains-text? (name (:genre book)) query)))

(defn search
  [books query]
  (filter
    #(matches-query? % query)
    books))

(defn sort-by-year
  [books]
  (sort-by :year books))

(defn sort-by-year-desc
  [books]
  (sort-by :year > books))

(defn sort-by-title
  [books]
  (sort-by :title books))

(defn search-with-options
  [books {:keys [title author genre tag published-after-year]}]
  (cond->> books
        title
        (filter #(utils/contains-text? (:title %) title))

        author
        (filter #(utils/contains-text? (:author %) author))

        genre
        (filter #(= (:genre %) genre))

        tag
        (filter #(has-tag? % tag))

        published-after-year
        (filter #(> (:year %) published-after-year))
  ))