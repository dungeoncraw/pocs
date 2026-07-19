(ns movies.search
  (:require [movies.predicates :as p]))


(defn matches-criteria?
  [movie {:keys [genre genres min-rating max-duration min-year max-year]}]
  (and
    (if genre
      (p/has-genre? movie genre)
      true)

    (if genres
      (p/has-any-genre? movie genres)
      true)

    (if min-rating
      (p/rating-at-least? movie min-rating)
      true)

    (if max-duration
      (p/duration-at-most? movie max-duration)
      true)

    (if min-year
      (p/released-after? movie min-year)
      true)

    (if max-year
      (p/released-before? movie max-year)
      true)))


(defn search-movies
  [movies criteria]
  (filter
    #(matches-criteria? % criteria)
    movies))
