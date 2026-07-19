(ns movies.predicates)


(defn has-genre?
  [movie genre]
  (contains? (:genres movie) genre))


(defn has-any-genre?
  [movie genres]
  (boolean
    (some
      #(has-genre? movie %)
      genres)))


(defn rating-at-least?
  [movie min-rating]
  (>= (:rating movie) min-rating))


(defn duration-at-most?
  [movie max-duration]
  (<= (:duration-minutes movie) max-duration))


(defn released-after?
  [movie min-year]
  (>= (:year movie) min-year))


(defn released-before?
  [movie max-year]
  (<= (:year movie) max-year))
