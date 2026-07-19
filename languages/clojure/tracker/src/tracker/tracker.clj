(ns tracker.core)

;; ---------------------------------------------------------
;; Initial data
;; ---------------------------------------------------------

(def movies
  [{:id 1
    :title "Inception"
    :year 2010
    :genres #{:sci-fi :thriller}
    :rating 8.8
    :duration-minutes 148}

   {:id 2
    :title "The Lord of the Rings: The Fellowship of the Ring"
    :year 2001
    :genres #{:fantasy :adventure}
    :rating 8.9
    :duration-minutes 178}

   {:id 3
    :title "The Matrix"
    :year 1999
    :genres #{:sci-fi :action}
    :rating 8.7
    :duration-minutes 136}

   {:id 4
    :title "Spirited Away"
    :year 2001
    :genres #{:animation :fantasy}
    :rating 8.6
    :duration-minutes 125}

   {:id 5
    :title "The Dark Knight"
    :year 2008
    :genres #{:action :crime :drama}
    :rating 9.0
    :duration-minutes 152}

   {:id 6
    :title "Interstellar"
    :year 2014
    :genres #{:sci-fi :drama}
    :rating 8.7
    :duration-minutes 169}

   {:id 7
    :title "Toy Story"
    :year 1995
    :genres #{:animation :adventure :comedy}
    :rating 8.3
    :duration-minutes 81}

   {:id 8
    :title "Parasite"
    :year 2019
    :genres #{:thriller :drama}
    :rating 8.5
    :duration-minutes 132}])


;; ---------------------------------------------------------
;; Basic predicates
;; ---------------------------------------------------------

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


;; ---------------------------------------------------------
;; Simple filters
;; ---------------------------------------------------------

(defn movies-by-genre
  [movies genre]
  (filter
    #(has-genre? % genre)
    movies))


(defn movies-by-any-genre
  [movies genres]
  (filter
    #(has-any-genre? % genres)
    movies))


(defn movies-with-min-rating
  [movies min-rating]
  (filter
    #(rating-at-least? % min-rating)
    movies))


(defn movies-under-duration
  [movies max-duration]
  (filter
    #(duration-at-most? % max-duration)
    movies))


(defn movies-released-between
  [movies min-year max-year]
  (filter
    #(and
       (released-after? % min-year)
       (released-before? % max-year))
    movies))


;; ---------------------------------------------------------
;; Sorting and ranking
;; ---------------------------------------------------------

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


;; ---------------------------------------------------------
;; Flexible search with criteria map
;; ---------------------------------------------------------

(defn matches-criteria?
  [movie {:keys [genre genres min-rating max-duration min-year max-year]}]
  (and
    ;; If :genre is provided, movie must have that genre.
    ;; If not provided, this rule passes automatically.
    (if genre
      (has-genre? movie genre)
      true)

    ;; If :genres is provided, movie must have at least one of them.
    (if genres
      (has-any-genre? movie genres)
      true)

    (if min-rating
      (rating-at-least? movie min-rating)
      true)

    (if max-duration
      (duration-at-most? movie max-duration)
      true)

    (if min-year
      (released-after? movie min-year)
      true)

    (if max-year
      (released-before? movie max-year)
      true)))


(defn search-movies
  [movies criteria]
  (filter
    #(matches-criteria? % criteria)
    movies))


;; ---------------------------------------------------------
;; Recommendation score
;; ---------------------------------------------------------

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
       (search-movies criteria)
       (map add-score)
       (sort-by :score >)
       (take limit)))


;; ---------------------------------------------------------
;; Printing helpers
;; ---------------------------------------------------------

(defn genres-summary
  [genres]
  (clojure.string/join ", "
                       (map name genres)))


(defn movie-summary
  [movie]
  (str (:title movie)
       " | "
       (:year movie)
       " | rating: "
       (:rating movie)
       " | duration: "
       (:duration-minutes movie)
       " min | genres: "
       (genres-summary (:genres movie))
       (if (:score movie)
         (str " | score: " (:score movie))
         "")))


(defn print-movies
  [movies]
  (doseq [movie movies]
    (println "-" (movie-summary movie))))


;; ---------------------------------------------------------
;; Main program
;; ---------------------------------------------------------

(defn -main
  []
  (println "All movies:")
  (print-movies movies)

  (println "\nSci-fi movies:")
  (print-movies
    (movies-by-genre movies :sci-fi))

  (println "\nMovies with rating >= 8.7:")
  (print-movies
    (movies-with-min-rating movies 8.7))

  (println "\nMovies under 130 minutes:")
  (print-movies
    (movies-under-duration movies 130))

  (println "\nMovies released between 2000 and 2015:")
  (print-movies
    (movies-released-between movies 2000 2015))

  (println "\nTop 3 rated movies:")
  (print-movies
    (top-rated movies 3))

  (println "\nSearch with criteria:")
  (print-movies
    (search-movies
      movies
      {:genres #{:sci-fi :fantasy}
       :min-rating 8.6
       :max-duration 170}))

  (println "\nRecommendations:")
  (print-movies
    (recommend
      movies
      {:genres #{:sci-fi :thriller :fantasy}
       :min-rating 8.5}
      5)))