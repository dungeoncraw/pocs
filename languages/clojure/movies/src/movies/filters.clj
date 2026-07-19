(ns movies.filters
  (:require [movies.predicates :as p]))


(defn movies-by-genre
  [movies genre]
  (filter
    #(p/has-genre? % genre)
    movies))


(defn movies-by-any-genre
  [movies genres]
  (filter
    #(p/has-any-genre? % genres)
    movies))


(defn movies-with-min-rating
  [movies min-rating]
  (filter
    #(p/rating-at-least? % min-rating)
    movies))


(defn movies-under-duration
  [movies max-duration]
  (filter
    #(p/duration-at-most? % max-duration)
    movies))


(defn movies-released-between
  [movies min-year max-year]
  (filter
    #(and
       (p/released-after? % min-year)
       (p/released-before? % max-year))
    movies))
