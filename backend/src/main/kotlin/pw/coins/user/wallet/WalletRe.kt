package pw.coins.user.wallet

import org.springframework.data.jpa.repository.JpaRepository

interface WalletRe : JpaRepository<WalletEn, Long>