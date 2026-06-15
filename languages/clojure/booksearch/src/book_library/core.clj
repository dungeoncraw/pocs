(ns book-library.core
  (:require [book-library.data :refer [books]]
            [book-library.utils :as utils]
            [book-library.search :as search]))

(defn -main
  [& _args]
  (println "All books:")
  (utils/print-books books)

  (println "\nSearch by title: code")
  (utils/print-books (search/search-by-title books "code"))

  (println "\nSearch by author: martin")
  (utils/print-books (search/search-by-author books "martin"))

  (println "\nSearch by genre: :science-fiction")
  (utils/print-books (search/search-by-genre books :science-fiction))

  (println "\nBooks published after 1980:")
  (utils/print-books (search/published-after books 1980))

  (println "\nBooks tagged with :programming")
  (utils/print-books (search/search-by-tag books :programming))

  (println "\nGeneral search: fiction")
  (utils/print-books (search/search books "fiction"))

  (println "\nSorted by year descending:")
  (utils/print-books (search/sort-by-year-desc books))

  (println "\nSearch with options:")
  (utils/print-books (search/search-with-options
                       books
                       {:genre :software
                        :tag :programming
                        :published-after-year 1990})
                     )
  )