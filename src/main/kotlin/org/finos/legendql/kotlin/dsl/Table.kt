package org.finos.legendql.kotlin.dsl

import org.finos.legendql.kotlin.model.*
import kotlin.reflect.KClass

/**
 * Base class for table definitions in the DSL, similar to KTORM
 */
open class Table<E>(val tableName: String, val databaseName: String = "default") {
    val columns = mutableMapOf<String, Column<*>>()
    
    /**
     * Create an integer column
     */
    fun int(name: String): Column<Int> {
        val column = Column<Int>(name, this, Int::class)
        columns[name] = column
        return column
    }
    
    /**
     * Create a string column
     */
    fun varchar(name: String): Column<String> {
        val column = Column<String>(name, this, String::class)
        columns[name] = column
        return column
    }
    
    /**
     * Create a double column
     */
    fun double(name: String): Column<Double> {
        val column = Column<Double>(name, this, Double::class)
        columns[name] = column
        return column
    }
    
    /**
     * Create a boolean column
     */
    fun boolean(name: String): Column<Boolean> {
        val column = Column<Boolean>(name, this, Boolean::class)
        columns[name] = column
        return column
    }
    
    /**
     * Create a date column
     */
    fun date(name: String): Column<java.time.LocalDate> {
        val column = Column<java.time.LocalDate>(name, this, java.time.LocalDate::class)
        columns[name] = column
        return column
    }
    
    /**
     * Create a datetime column
     */
    fun datetime(name: String): Column<java.time.LocalDateTime> {
        val column = Column<java.time.LocalDateTime>(name, this, java.time.LocalDateTime::class)
        columns[name] = column
        return column
    }
    
    /**
     * Get column metadata for table creation
     */
    fun getColumnsMetadata(): Map<String, KClass<*>> {
        return columns.mapValues { it.value.type }
    }
}
