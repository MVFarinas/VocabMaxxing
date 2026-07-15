package com.vocabmaxxing.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object DatabaseFactory {

    private var hikariDataSource: HikariDataSource? = null

    fun init(environment: ApplicationEnvironment) {
        try {
            val dbUrl = environment.config.property("database.url").getString()
            val dbUser = environment.config.property("database.user").getString()
            val dbPassword = environment.config.property("database.password").getString()

            val hikariConfig = HikariConfig().apply {
                jdbcUrl = dbUrl
                username = dbUser
                password = dbPassword
                maximumPoolSize = 10
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }

            val dataSource = HikariDataSource(hikariConfig)
            hikariDataSource = dataSource
            Database.connect(dataSource)
            environment.log.info("Database connection successful.")


            transaction {
                SchemaUtils.create(Users, Words, Attempts)
            }
        } catch (e: Exception) {
            environment.log.error("Database connection failed.", e)
        }
    }

    fun isConnected(): Boolean {
        return try {
            hikariDataSource?.connection?.use { it.isValid(1) } ?: false
        } catch (e: Exception) {
            false
        }
    }
}