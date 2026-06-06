(ns grades.core)

(def students
  [{:id 1
    :name "Briana"
    :grades [8.0 8.9 7.5]}
   {:id 2
    :name "Anatoly"
    :grades [9.0 8.5 9.5]}
   {:id 3
    :name "Sophie"
    :grades [7.0 6.5 8.0]}
   {:id 4
    :name "Dmitry"
    :grades [8.5 9.0 8.0]}])

(defn average
  [numbers]
  (/ (reduce + numbers)
     (count numbers)))
(defn student-average
  [student]
  (average (:grades student)))


(defn status
  [avg]
  (cond
    (>= avg 7.0) :approved
    (>= avg 5.0) :recovery
    :else :failed))

(defn add-result
  [student]
  (let [avg (student-average student)
        result (status avg)]
    (assoc student
      :average avg
      :status result)))

(defn approved?
  [student]
  (= (:status student) :approved))

(defn students-with-results
  [students]
  (map add-result students))


(defn approved-students
  [students]
  (filter approved? students))


(defn class-average
  [students]
  (let [averages (map :average students)]
    (average averages)))


(defn group-by-status
  [students]
  (group-by :status students))


(defn student-summary
  [{:keys [name average status]}]
  (str name
       " | average: "
       average
       " | status: "
       status))


(defn print-students
  [students]
  (doseq [student students]
    (println (student-summary student))))


(defn -main
  []
  (let [results (students-with-results students)]

    (println "All students:")
    (print-students results)

    (println "\nApproved students:")
    (print-students (approved-students results))

    (println "\nClass average:")
    (println (class-average results))

    (println "\nStudents grouped by status:")
    (println (group-by-status results))))