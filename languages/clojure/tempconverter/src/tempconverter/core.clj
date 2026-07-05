(ns tempconverter.core)

(defn celsius->fahrenheit
  [celsius]
  (+ (* celsius 9/5) 32))

(defn fahrenheit->celsius
  [fahrenheit]
  (* (- fahrenheit 32) 5/9))

(defn celsius->kelvin
  [celsius]
  (+ celsius 273.15))


(defn kelvin->celsius
  [kelvin]
  (- kelvin 273.15))

(defn fahrenheit->kelvin
  [fahrenheit]
  (celsius->kelvin (fahrenheit->celsius fahrenheit)))

(defn kelvin->fahrenheit
  [kelvin]
  (celsius->fahrenheit (kelvin->celsius kelvin)))


(defn unit-label
  [unit]
  (case unit
    :celsius "Celsius"
    :fahrenheit "Fahrenheit"
    :kelvin "Kelvin"
    "Unknown unit"))

(defn unit-symbol
  [unit]
  (case unit
    :celsius "°C"
    :fahrenheit "°F"
    :kelvin "K"
    "Unknown unit"))


(defn convert
  [value from-unit to-unit]
  (cond
    (= from-unit to-unit)
      value
    (and (= from-unit :celsius)
         (= to-unit :fahrenheit))
    (celsius->fahrenheit value)

    (and (= from-unit :fahrenheit)
         (= to-unit :celsius))
    (fahrenheit->celsius value)

    (and (= from-unit :celsius)
         (= to-unit :kelvin))
    (celsius->kelvin value)

    (and (= from-unit :kelvin)
         (= to-unit :celsius))
    (kelvin->celsius value)

    (and (= from-unit :fahrenheit)
         (= to-unit :kelvin))
    (fahrenheit->kelvin value)

    (and (= from-unit :kelvin)
         (= to-unit :fahrenheit))
    (kelvin->fahrenheit value)
  :else
    nil
  ))

(def valid-units #{:celsius :fahrenheit :kelvin})
(defn valid-unit?
  [unit]
  (contains? valid-units unit))

(defn valid-conversion?
  [from-unit to-unit]
  (and
    (valid-unit? from-unit)
    (valid-unit? to-unit)))

(defn conversion-result
  [value from-unit to-unit]
  (if (valid-conversion? from-unit to-unit)
    {:success true
     :input-value value
     :from-unit from-unit
     :to-unit to-unit
     :result (convert value from-unit to-unit)}
    {:success false
     :error :invalid-unit
     :input-value value
     :from-unit from-unit
     :to-unit to-unit}))


(defn format-temperature
  [value unit]
  (str value " " (unit-symbol unit)))


(defn conversion-summary
  [conversion]
  (if (:success conversion)
    (str
      (format-temperature (:input-value conversion)
                          (:from-unit conversion))
      " = "
      (format-temperature (:result conversion)
                          (:to-unit conversion)))
    (str "Invalid conversion from "
         (:from-unit conversion)
         " to "
         (:to-unit conversion))))


(defn print-conversion
  [value from-unit to-unit]
  (let [result (conversion-result value from-unit to-unit)]
    (println (conversion-summary result))))


(defn classify-celsius
  [celsius]
  (cond
    (< celsius 0) :freezing
    (< celsius 10) :cold
    (< celsius 25) :comfortable
    (< celsius 35) :hot
    :else :extreme-heat))


(defn classify-temperature
  [value unit]
  (let [celsius-value (convert value unit :celsius)]
    (classify-celsius celsius-value)))



(defn -main
  []
  (println "Temperature Converter")
  (println)

  (println "Basic conversions:")
  (print-conversion 0 :celsius :fahrenheit)
  (print-conversion 100 :celsius :fahrenheit)
  (print-conversion 32 :fahrenheit :celsius)
  (print-conversion 212 :fahrenheit :celsius)
  (print-conversion 0 :celsius :kelvin)
  (print-conversion 273.15 :kelvin :celsius)

  (println)
  (println "Composed conversions:")
  (print-conversion 32 :fahrenheit :kelvin)
  (print-conversion 300 :kelvin :fahrenheit)

  (println)
  (println "Same-unit conversion:")
  (print-conversion 25 :celsius :celsius)

  (println)
  (println "Invalid conversion:")
  (print-conversion 25 :celsius :rankine)

  (println)
  (println "Classification:")
  (println "25 °C is" (classify-temperature 25 :celsius))
  (println "86 °F is" (classify-temperature 86 :fahrenheit))
  (println "280 K is" (classify-temperature 280 :kelvin)))