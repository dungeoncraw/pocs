package order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait OrderRepository extends JpaRepository[Order, java.lang.Long]
