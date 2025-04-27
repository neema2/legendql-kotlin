package org.finos.legendql.kotlin.dsl

import org.finos.legendql.kotlin.model.*
import org.finos.legendql.kotlin.query.Query

/**
 * Main entry point for the DSL, similar to KTORM's Database
 */
class Database(val name: String = "default") {
    companion object {
        /**
         * Connect to a database with the given parameters
         */
        fun connect(
            url: String, 
            user: String = "", 
            password: String = "", 
            name: String = "default"
        ): Database {
            return Database(name)
        }
    }
    
    /**
     * Start a query from a table
     */
    fun <E> from(table: Table<E>): QueryBuilder<E> {
        val tableModel = org.finos.legendql.kotlin.model.Table(
            table.tableName, 
            table.getColumnsMetadata().toMutableMap()
        )
        val database = org.finos.legendql.kotlin.model.Database(name, listOf(tableModel))
        val query = Query.fromTable(database, tableModel)
        
        return QueryBuilder(query, database, tableModel, table)
    }
}

/**
 * Builder for constructing queries with a fluent API
 */
class QueryBuilder<E>(
    private val query: Query,
    private val database: org.finos.legendql.kotlin.model.Database,
    private val tableModel: org.finos.legendql.kotlin.model.Table,
    private val table: Table<E>
) {
    /**
     * Select specific columns from the table
     */
    fun select(vararg columns: Column<*>): QueryBuilder<E> {
        val columnNames = columns.map { it.name }
        query.select(*columnNames.toTypedArray())
        return this
    }
    
    /**
     * Extend with computed columns
     */
    fun extend(expressions: List<ComputedColumnAliasExpression>): QueryBuilder<E> {
        query.extend(expressions)
        return this
    }
    
    // Having method is already defined in the class
    
    /**
     * Filter rows using a condition
     */
    fun where(condition: () -> BinaryExpression): QueryBuilder<E> {
        // Clear any previous context
        OperatorContext.clearContext()
        
        // Try to get the expression from the condition lambda
        val expr = try {
            condition()
        } catch (e: UnsupportedOperationException) {
            // If an exception was thrown, check if we have a captured expression
            OperatorContext.getLastExpression() ?: throw RuntimeException(
                "Failed to capture expression in where clause. Make sure you're using the DSL correctly.", e
            )
        }
        
        query.filter(expr)
        return this
    }
    
    /**
     * Order results by columns
     */
    fun orderBy(vararg orders: OrderByExpression): QueryBuilder<E> {
        query.orderBy(*orders)
        return this
    }
    
    /**
     * Group results by columns
     */
    fun groupBy(vararg columns: Column<*>): QueryBuilder<E> {
        val columnRefs = columns.map { ColumnReferenceExpression(it.name) }
        query.groupBy(columnRefs, columnRefs)
        return this
    }
    
    /**
     * Add a having clause to a group by
     */
    fun having(condition: () -> BinaryExpression): QueryBuilder<E> {
        // Clear any previous context
        OperatorContext.clearContext()
        
        // Try to get the expression from the condition lambda
        val expr = try {
            condition()
        } catch (e: UnsupportedOperationException) {
            // If an exception was thrown, check if we have a captured expression
            OperatorContext.getLastExpression() ?: throw RuntimeException(
                "Failed to capture expression in having clause. Make sure you're using the DSL correctly.", e
            )
        }
        
        // Find the last GroupByClause and update its having expression
        val lastGroupByClause = query.clauses.lastOrNull { it is GroupByClause } as? GroupByClause
        if (lastGroupByClause != null) {
            val groupByExpr = lastGroupByClause.expression as GroupByExpression
            val updatedGroupByExpr = GroupByExpression(
                groupByExpr.selections,
                groupByExpr.groupBy,
                expr
            )
            query.clauses[query.clauses.lastIndexOf(lastGroupByClause)] = GroupByClause(updatedGroupByExpr)
        }
        
        return this
    }
    
    /**
     * Limit the number of results
     */
    fun limit(limit: Int): QueryBuilder<E> {
        query.limit(limit)
        return this
    }
    
    /**
     * Skip a number of results
     */
    fun offset(offset: Int): QueryBuilder<E> {
        query.offset(offset)
        return this
    }
    
    /**
     * Limit with offset in one call
     */
    fun limit(offset: Int, limit: Int): QueryBuilder<E> {
        query.offset(offset)
        query.limit(limit)
        return this
    }
    
    /**
     * Join with another table
     */
    fun <T> innerJoin(
        otherTable: Table<T>, 
        on: () -> BinaryExpression
    ): JoinQueryBuilder<E, T> {
        val otherTableModel = org.finos.legendql.kotlin.model.Table(
            otherTable.tableName, 
            otherTable.getColumnsMetadata().toMutableMap()
        )
        
        // Clear any previous context
        OperatorContext.clearContext()
        
        // Try to get the expression from the on lambda
        val expr = try {
            on()
        } catch (e: UnsupportedOperationException) {
            // If an exception was thrown, check if we have a captured expression
            OperatorContext.getLastExpression() ?: throw RuntimeException(
                "Failed to capture expression in join condition. Make sure you're using the DSL correctly.", e
            )
        }
        
        query.join(
            database.name,
            otherTableModel.table,
            InnerJoinType(),
            expr
        )
        
        return JoinQueryBuilder(query, database, tableModel, table, otherTable)
    }
    
    /**
     * Left join with another table
     */
    fun <T> leftJoin(
        otherTable: Table<T>, 
        on: () -> BinaryExpression
    ): JoinQueryBuilder<E, T> {
        val otherTableModel = org.finos.legendql.kotlin.model.Table(
            otherTable.tableName, 
            otherTable.getColumnsMetadata().toMutableMap()
        )
        
        // Clear any previous context
        OperatorContext.clearContext()
        
        // Try to get the expression from the on lambda
        val expr = try {
            on()
        } catch (e: UnsupportedOperationException) {
            // If an exception was thrown, check if we have a captured expression
            OperatorContext.getLastExpression() ?: throw RuntimeException(
                "Failed to capture expression in join condition. Make sure you're using the DSL correctly.", e
            )
        }
        
        query.join(
            database.name,
            otherTableModel.table,
            LeftJoinType(),
            expr
        )
        
        return JoinQueryBuilder(query, database, tableModel, table, otherTable)
    }
    
    /**
     * Execute the query and iterate over results
     */
    fun forEach(action: (Row) -> Unit) {
        // In a real implementation, this would execute the query and iterate over results
        // For now, we'll just create a dummy row for demonstration
        val row = Row(mapOf())
        action(row)
    }
    
    /**
     * Bind the query to a runtime
     */
    fun <R : Runtime> bind(runtime: R): DataFrame<Any> {
        return query.bind(runtime)
    }
}

/**
 * Builder for join queries
 */
class JoinQueryBuilder<E, T>(
    private val query: Query,
    private val database: org.finos.legendql.kotlin.model.Database,
    private val tableModel: org.finos.legendql.kotlin.model.Table,
    private val table: Table<E>,
    private val otherTable: Table<T>
) {
    /**
     * Select specific columns from the joined tables
     */
    fun select(vararg columns: Column<*>): QueryBuilder<E> {
        val columnNames = columns.map { it.name }
        query.select(*columnNames.toTypedArray())
        return QueryBuilder(query, database, tableModel, table)
    }
}

/**
 * Represents a row in the result set
 */
class Row(private val data: Map<String, Any?>) {
    /**
     * Get a value from the row by column
     */
    operator fun <T> get(column: Column<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return data[column.name] as T?
    }
    
    /**
     * Get a value from the row by name
     */
    operator fun <T> get(name: String): T? {
        @Suppress("UNCHECKED_CAST")
        return data[name] as T?
    }
}
