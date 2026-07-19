(ns movies.format
  (:require [clojure.string :as str]))


(defn genres-summary
  [genres]
  (str/join ", "
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
