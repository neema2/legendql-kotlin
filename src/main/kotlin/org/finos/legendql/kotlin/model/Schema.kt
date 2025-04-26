package org.finos.legendql.kotlin.model

import kotlin.reflect.KClass

/**
 * Represents a database table with a name and column definitions
 */
data class Table(
    val table: String,
    val columns: MutableMap<String, KClass<*>> = mutableMapOf()
) {
    /**
     * Copy the table with a new set of columns
     */
    fun copy(): Table = Table(table, columns.toMutableMap())
}

/**
 * Represents a database with a name and a list of tables
 */
data class Database(
    val name: String,
    val tables: List<Table> = emptyList()
)
