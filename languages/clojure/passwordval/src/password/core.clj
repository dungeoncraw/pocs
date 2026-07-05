(ns password.core
  (:require [clojure.string :as str]))

(def min-password-length 8)
(def special-character-pattern #"[!@#$%^&*]")

(defn not-blank?
  [password]
  (not (str/blank? password)))

(defn long-enough?
  [password]
  (>= (count password) min-password-length))

(defn has-uppercase?
  [password]
  (re-find #"[A-Z]" password))

(defn has-lowercase?
  [password]
  (re-find #"[a-z]" password))

(defn has-digit?
  [password]
  (re-find #"[0-9]" password))

(defn has-special-character?
  [password]
  (re-find special-character-pattern password))

(defn has-no-spaces?
  [password]
  (not
    (boolean
      (re-find #"\s" password))))

(defn valid-password?
  [password]
  (and
    (not-blank? password)
    (long-enough? password)
    (has-uppercase? password)
    (has-lowercase? password)
    (has-digit? password)
    (has-special-character? password)
    (has-no-spaces? password)))


(defn password-errors
  [password]
  (cond-> []
          (not (not-blank? password))
          (conj :password-blank)

          (not (long-enough? password))
          (conj :password-too-short)

          (not (has-uppercase? password))
          (conj :missing-uppercase)

          (not (has-lowercase? password))
          (conj :missing-lowercase)

          (not (has-digit? password))
          (conj :missing-digit)

          (not (has-special-character? password))
          (conj :missing-special-character)

          (not (has-no-spaces? password))
          (conj :contains-spaces)))

(defn validation-result
  [password]
  (let [errors (password-errors password)]
    {:password password
     :valid? (empty? errors)
     :errors errors}))

(defn rule-score
  [password]
  (count
    (filter true?
            [(not-blank? password)
             (long-enough? password)
             (has-uppercase? password)
             (has-lowercase? password)
             (has-digit? password)
             (has-special-character? password)
             (has-no-spaces? password)])))


(defn strength
  [password]
  (let [score (rule-score password)]
    (cond
      (<= score 2) :weak
      (<= score 4) :medium
      (<= score 6) :strong
      :else :very-strong)))


(defn error-message
  [error]
  (case error
    :password-blank "Password cannot be blank."
    :password-too-short "Password must have at least 8 characters."
    :missing-uppercase "Password must contain at least one uppercase letter."
    :missing-lowercase "Password must contain at least one lowercase letter."
    :missing-digit "Password must contain at least one digit."
    :missing-special-character "Password must contain at least one special character: ! @ # $ % ^ & *"
    :contains-spaces "Password cannot contain spaces."
    "Unknown password error."))


(defn validation-summary
  [password]
  (let [result (validation-result password)]
    (if (:valid? result)
      {:valid? true
       :strength (strength password)
       :message "Password is valid."}
      {:valid? false
       :strength (strength password)
       :messages (mapv error-message (:errors result))})))


(defn print-validation
  [password]
  (println "Testing password:" password)

  (let [summary (validation-summary password)]
    (println "Valid?:" (:valid? summary))
    (println "Strength:" (:strength summary))

    (if (:valid? summary)
      (println (:message summary))
      (doseq [message (:messages summary)]
        (println "-" message))))

  (println))



(defn -main
  []

  (print-validation "")
  (print-validation "abc")
  (print-validation "abcdefgh")
  (print-validation "Abcdefgh")
  (print-validation "Abcdefg1")
  (print-validation "Abcdefg1!")
  (print-validation "Abc defg1!"))