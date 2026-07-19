(ns movies.core
  (:require [movies.data :as data]
            [movies.filters :as f]
            [movies.sort :as s]
            [movies.search :as search]
            [movies.recommend :as r]
            [movies.format :as fmt]
            [movies.predicates :as p]))


(defn -main
  []
  (println "All movies:")
  (fmt/print-movies data/movies)

  (println "\nSci-fi movies sorted by year desc, via ->>:")
  (->> data/movies
       (filter #(p/has-genre? % :sci-fi))
       (sort-by :year >)
       fmt/print-movies)

  (println "\nTop 3 highly-rated short movies, via ->>:")
  (->> data/movies
       (filter #(p/rating-at-least? % 8.5))
       (filter #(p/duration-at-most? % 140))
       (sort-by :rating >)
       (take 3)
       fmt/print-movies)

  (println "\nMovie titles released between 2000 and 2015 sorted by duration, via ->>:")
  (->> data/movies
       (filter #(p/released-after? % 2000))
       (filter #(p/released-before? % 2015))
       (sort-by :duration-minutes)
       (map :title)
       (run! #(println "-" %)))

  (println "\nAverage rating of animation movies, via ->>:")
  (let [ratings (->> data/movies
                     (filter #(p/has-genre? % :animation))
                     (map :rating))]
    (println (/ (reduce + ratings) (double (count ratings)))))

  (println "\nAll unique genres across the catalog, via ->>:")
  (->> data/movies
       (mapcat :genres)
       distinct
       sort
       (map name)
       (clojure.string/join ", ")
       println)

  (println "\nRecommendations:")
  (fmt/print-movies
    (r/recommend
      data/movies
      {:genres #{:sci-fi :thriller :fantasy}
       :min-rating 8.5}
      5)))
