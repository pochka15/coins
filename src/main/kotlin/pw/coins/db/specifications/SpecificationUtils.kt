package pw.coins.db.specifications

import org.springframework.data.jpa.domain.Specification
import pw.coins.room.member.MemberEn
import kotlin.reflect.KProperty1

fun <T, R> fetch(prop: KProperty1<T, R>): Specification<T> =
    Specification { root, _, _ ->
        root.fetch<MemberEn, R>(prop.name)
        null
    }
