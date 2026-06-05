package models

final case class Person(
                   id: String,
                   name: String,
                   timeSlots: List[TimeSlot] = Nil
                 )