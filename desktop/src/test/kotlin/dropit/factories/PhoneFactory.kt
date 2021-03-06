package dropit.factories

import dropit.application.dto.TokenStatus
import dropit.jooq.tables.pojos.Phone
import dropit.jooq.tables.records.PhoneRecord
import dropit.jooq.tables.references.PHONE
import org.jooq.DSLContext
import java.util.*

object PhoneFactory {
    fun multiplePhones(jooq: DSLContext) {
        jooq.newRecord(PHONE, authorizedPhone()).insert()
        jooq.newRecord(PHONE, pendingPhone()).insert()
        jooq.newRecord(PHONE, deniedPhone()).insert()
    }

    fun authorizedPhone(jooq: DSLContext? = null): PhoneRecord {
        return PhoneRecord().apply {
            from(
                Phone(
                    id = UUID.fromString("4bd3b637-a200-4ec7-aeac-395736300cc5"),
                    name = "Authorized phone",
                    status = TokenStatus.AUTHORIZED,
                    token = UUID.fromString("b145285e-7ac5-4553-a49d-8940c12ea47d")
                )
            )
            jooq?.newRecord(PHONE, this)?.insert()
        }
    }

    fun pendingPhone(jooq: DSLContext? = null): PhoneRecord {
        return PhoneRecord().apply {
            from(
                Phone(
                    id = UUID.fromString("accc5020-2762-4371-aa0b-f990cce38a39"),
                    name = "Pending phone",
                    status = TokenStatus.PENDING,
                    token = UUID.fromString("3a2b9d2b-2138-40d1-a648-9cb1c1b2d1ac")
                )
            )
            jooq?.newRecord(PHONE, this)?.insert()
        }
    }

    fun deniedPhone(jooq: DSLContext? = null): PhoneRecord {
        return PhoneRecord().apply {
            from(
                Phone(
                    id = UUID.fromString("fd03ed18-691f-492b-85e2-56e9c1ca2523"),
                    name = "Denied phone",
                    status = TokenStatus.DENIED,
                    token = UUID.fromString("8abed804-e279-49c3-9666-75fccdcaa173")
                )
            )
            jooq?.newRecord(PHONE, this)?.insert()
        }
    }
}
