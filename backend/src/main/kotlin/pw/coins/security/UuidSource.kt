package pw.coins.security

import org.springframework.stereotype.Component
import java.util.*

@Component
class UuidSource {
  fun genUuid(): UUID {
    return UUID.randomUUID()
  }
}
