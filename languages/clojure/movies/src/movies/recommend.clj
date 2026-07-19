(ns movies.recommend
  (:require [movies.search :as s]))


(defn recommendation-score
  [movie]
  (let [rating-score (* (:rating movie) 10)
        recent-bonus (if (>= (:year movie) 2010) 5 0)
        short-bonus (if (<= (:duration-minutes movie) 130) 3 0)]
    (+ rating-score recent-bonus short-bonus)))


(defn add-score
  [movie]
  (assoc movie
    :score
    (recommendation-score movie)))


(defn recommend
  [movies criteria limit]
  (->> movies
       (s/search-movies criteria)
       (map add-score)
       (sort-by :score >)
       (take limit)))
