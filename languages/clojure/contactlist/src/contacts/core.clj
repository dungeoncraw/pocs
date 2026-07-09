(ns contacts.core
  (:require [clojure.string :as str]))

(def contacts
  [{:id    1
    :name  "Ana Silva"
    :email "ana@example.com"
    :phone "1111-1111"
    :tags  #{:friend :work}}

   {:id    2
    :name  "Bruno Costa"
    :email "bruno@example.com"
    :phone "2222-2222"
    :tags  #{:family}}

   {:id    3
    :name  "Carla Souza"
    :email "carla@example.com"
    :phone "3333-3333"
    :tags  #{:work :clojure}}

   {:id    4
    :name  "Diego Santos"
    :email "diego@example.com"
    :phone "4444-4444"
    :tags  #{:friend}}])



(defn normalize
  [text]
  (str/lower-case text))


(defn contains-text?
  [text query]
  (str/includes?
    (normalize text)
    (normalize query)))


(defn next-id
  [contacts]
  (inc
    (apply max
           (map :id contacts))))


(defn find-contact
  [contacts contact-id]
  (first
    (filter #(= (:id %) contact-id)
            contacts)))


(defn contact-exists?
  [contacts contact-id]
  (some? (find-contact contacts contact-id)))


(defn search-by-name
  [contacts query]
  (filter
    #(contains-text? (:name %) query)
    contacts))


(defn search-by-email
  [contacts query]
  (filter
    #(contains-text? (:email %) query)
    contacts))

(defn search-by-phone
  [contacts query]
  (filter
    #(contains-text? (:phone %) query)
    contacts))

(defn search-contacts
  [contacts query]
  (or
    (search-by-name contacts query)
    (search-by-email contacts query)
    (search-by-phone contacts query)
    )
  )

(defn has-tag?
  [contact tag]
  (contains? (:tags contact) tag))


(defn contacts-by-tag
  [contacts tag]
  (filter
    #(has-tag? % tag)
    contacts))

(defn count-by-tag
  [contacts]
  (frequencies
    (mapcat :tags contacts)))

(defn update-contact
  [contacts contact-id update-fn]
  (mapv
    (fn [contact]
      (if (= (:id contact) contact-id)
        (update-fn contact)
        contact))
    contacts))



(defn add-contact
  [contacts name email phone tags]
  (conj contacts
        {:id    (next-id contacts)
         :name  name
         :email email
         :phone phone
         :tags  tags}))


(defn update-email
  [contacts contact-id new-email]
  (if (contact-exists? contacts contact-id)
    {:success   true
     :operation :update-email
     :contacts
     (update-contact
       contacts
       contact-id
       #(assoc % :email new-email))}
    {:success  false
     :error    :contact-not-found
     :contacts contacts}))


(defn update-phone
  [contacts contact-id new-phone]
  (if (contact-exists? contacts contact-id)
    {:success   true
     :operation :update-phone
     :contacts
     (update-contact
       contacts
       contact-id
       #(assoc % :phone new-phone))}
    {:success  false
     :error    :contact-not-found
     :contacts contacts}))


(defn add-tag
  [contacts contact-id tag]
  (if (contact-exists? contacts contact-id)
    {:success   true
     :operation :add-tag
     :contacts
     (update-contact
       contacts
       contact-id
       #(update % :tags conj tag))}
    {:success  false
     :error    :contact-not-found
     :contacts contacts}))


(defn remove-tag
  [contacts contact-id tag]
  (if (contact-exists? contacts contact-id)
    {:success   true
     :operation :remove-tag
     :contacts
     (update-contact
       contacts
       contact-id
       #(update % :tags disj tag))}
    {:success  false
     :error    :contact-not-found
     :contacts contacts}))


(defn delete-contact
  [contacts contact-id]
  (if (contact-exists? contacts contact-id)
    {:success   true
     :operation :delete-contact
     :contacts
     (vec
       (remove #(= (:id %) contact-id)
               contacts))}
    {:success  false
     :error    :contact-not-found
     :contacts contacts}))


(defn tags-summary
  [tags]
  (str/join ", "
            (map name tags)))


(defn contact-summary
  [contact]
  (str "Contact "
       (:id contact)
       " | "
       (:name contact)
       " | email: "
       (:email contact)
       " | phone: "
       (:phone contact)
       " | tags: "
       (tags-summary (:tags contact))))

(defn contact-errors
  [contact]
  (cond
    (not (contains? contact :name)) "Missing name"
    (not (contains? contact :email)) "Missing email"
    (not (contains? contact :phone)) "Missing phone"
    :else nil))
(defn print-contacts
  [contacts]
  (doseq [contact contacts]
    (println "-" (contact-summary contact))))


(defn print-result
  [result]
  (if (:success result)
    (println "Success:" (:operation result))
    (println "Error:" (:error result)))

  (print-contacts (:contacts result)))


(defn -main
  []
  (println "Initial contacts:")
  (print-contacts contacts)

  (println "\nSearch by name: ana")
  (print-contacts (search-by-name contacts "ana"))

  (println "\nContacts tagged with :work")
  (print-contacts (contacts-by-tag contacts :work))

  (println "\nAdd new contact:")
  (let [updated-contacts
        (add-contact contacts
                     "Eduardo Lima"
                     "eduardo@example.com"
                     "5555-5555"
                     #{:friend :clojure})]
    (print-contacts updated-contacts)

    (println "\nUpdate email for contact 2:")
    (let [result1 (update-email updated-contacts
                                2
                                "bruno.new@example.com")]
      (print-result result1)

      (println "\nAdd :vip tag to contact 1:")
      (let [result2 (add-tag (:contacts result1)
                             1
                             :vip)]
        (print-result result2)

        (println "\nRemove :work tag from contact 3:")
        (let [result3 (remove-tag (:contacts result2)
                                  3
                                  :work)]
          (print-result result3)

          (println "\nDelete contact 4:")
          (let [result4 (delete-contact (:contacts result3)
                                        4)]
            (print-result result4)
            (println (search-contacts (:contacts result4) "eduardo"))
            (println (count-by-tag (:contacts result4)))
            (doseq [contact (:contacts result4)]
              (println (contact-errors contact)))
            )
          )
        )
      )
    )
  )