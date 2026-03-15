package user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
case class User(
                 @Id id: java.lang.Long,
                 name: String,
                 email: String
               )