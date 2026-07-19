(ns movies.data)

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
