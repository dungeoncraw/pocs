(ns movies.sort)


(defn sort-by-rating-desc
  [movies]
  (sort-by :rating > movies))


(defn sort-by-duration
  [movies]
  (sort-by :duration-minutes movies))


(defn sort-by-year-desc
  [movies]
  (sort-by :year > movies))


(defn top-rated
  [movies limit]
  (take limit
        (sort-by-rating-desc movies)))
