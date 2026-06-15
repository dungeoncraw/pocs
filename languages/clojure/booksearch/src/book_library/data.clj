(ns book-library.data)

(def books
  [{:id 1
    :title "The Pragmatic Programmer"
    :author "David Thomas"
    :year 1999
    :genre :software
    :tags #{:programming :career :craft}}

   {:id 2
    :title "Clean Code"
    :author "Robert C. Martin"
    :year 2008
    :genre :software
    :tags #{:programming :quality :design}}

   {:id 3
    :title "Dune"
    :author "Frank Herbert"
    :year 1965
    :genre :science-fiction
    :tags #{:fiction :politics :ecology}}

   {:id 4
    :title "Foundation"
    :author "Isaac Asimov"
    :year 1951
    :genre :science-fiction
    :tags #{:fiction :empire :future}}

   {:id 5
    :title "The Hobbit"
    :author "J. R. R. Tolkien"
    :year 1937
    :genre :fantasy
    :tags #{:fiction :adventure :classic}}

   {:id 6
    :title "Structure and Interpretation of Computer Programs"
    :author "Harold Abelson"
    :year 1985
    :genre :software
    :tags #{:programming :lisp :computer-science}}])
