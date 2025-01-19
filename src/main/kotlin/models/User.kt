package models

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

const val MAX_VARCHAR_LENGTH = 100

@Serializable
data class ExposedUser(val firstName: String, val lastName: String, val id: Int)

class UserService(private val database: Database) {
    object Users : Table("users") {
        val id = Users.integer("id").autoIncrement()
        val oauthId = varchar("oauth_id", MAX_VARCHAR_LENGTH).uniqueIndex()
        val oauthEmail = varchar("oauth_email", MAX_VARCHAR_LENGTH).uniqueIndex()

        val firstName = varchar("first_name", MAX_VARCHAR_LENGTH)
        val lastName = varchar("last_name", MAX_VARCHAR_LENGTH)
        val createdAt = datetime("created_at")
        val updatedAt = datetime("updated_at")
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction (Dispatchers.IO, database) { block() }

    suspend fun insertIgnore(oauthId: String, oauthEmail: String, firstName: String, lastName: String): Int = dbQuery {
        Users.insertIgnore {
            it[Users.oauthId] = oauthId
            it[Users.oauthEmail] = oauthEmail
            it[Users.firstName] = firstName
            it[Users.lastName] = lastName
        }[Users.id]
    }

    suspend fun findByOauthUser(oauthId: String): ExposedUser? {
        return dbQuery {
            Users.selectAll()
                .where { Users.oauthId eq oauthId }
                .map { ExposedUser(it[Users.firstName], it[Users.lastName], it[Users.id]) }
                .singleOrNull()
        }
    }

    suspend fun read(id: Int): ExposedUser? {
        return dbQuery {
            Users.selectAll()
                .where { Users.id eq id }
                .map { ExposedUser(it[Users.firstName], it[Users.lastName], it[Users.id]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: ExposedUser) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[firstName] = user.firstName
                it[lastName] = user.lastName
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}