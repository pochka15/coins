package pw.coins.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface UserRe : JpaRepository<UserEn, Long>, JpaSpecificationExecutor<UserEn> {
    fun findByName(name: String): UserEn?
}