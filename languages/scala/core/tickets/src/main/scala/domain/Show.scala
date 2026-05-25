package domain

case class Show(
                 id: Long,
                 name: String,
                 description: String,
                 status: ShowStatus
               )