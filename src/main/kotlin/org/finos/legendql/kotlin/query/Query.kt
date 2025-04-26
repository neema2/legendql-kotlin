package org.finos.legendql.kotlin.query

import org.finos.legendql.kotlin.model.*

/**
 * Represents a query with clauses that can be executed by a runtime
 */
class Query(
    val database: Database,
    val tableHistory: MutableList<Table> = mutableListOf(),
    var table: Table,
    val clauses: MutableList<Clause> = mutableListOf()
) {
    companion object {
        /**
         * Create a query from a table
         */
        fun fromTable(database: Database, table: Table): Query {
            val tableCopy = Table(table.table, table.columns.toMutableMap())
            return Query(
                database,
                mutableListOf(tableCopy),
                tableCopy,
                mutableListOf(FromClause(database.name, table.table))
            )
        }

        /**
         * Create a query from a database and table name with columns
         */
        fun fromDb(database: Database, table: String, columns: Map<String, KClass<*>>): Query {
            return fromTable(database, Table(table, columns.toMutableMap()))
        }
    }

    /**
     * Bind the query to a runtime
     */
    fun <R : Runtime> bind(runtime: R): DataFrame<Any> {
        return DataFrame(runtime, clauses)
    }

    /**
     * Evaluate the query with a runtime
     */
    fun <R : Runtime, T> eval(runtime: R): DataFrame<T> {
        return bind(runtime).eval() as DataFrame<T>
    }

    /**
     * Add a clause to the query
     */
    fun addClause(clause: Clause) {
        clauses.add(clause)
    }

    /**
     * Update the current table
     */
    fun updateTable(table: Table) {
        tableHistory.add(table)
        this.table = table
    }

    /**
     * Select columns from the table
     */
    fun select(vararg names: String): Query {
        addClause(SelectionClause(names.map { ColumnReferenceExpression(it) }))
        return this
    }

    /**
     * Rename columns in the table
     */
    fun rename(vararg renames: Pair<String, String>): Query {
        addClause(RenameClause(renames.map { 
            ColumnAliasExpression(it.second, ColumnReferenceExpression(it.first)) 
        }))
        return this
    }

    /**
     * Extend the table with computed columns
     */
    fun extend(expressions: List<Expression>): Query {
        addClause(ExtendClause(expressions))
        return this
    }

    /**
     * Filter rows in the table
     */
    fun filter(expression: Expression): Query {
        addClause(FilterClause(expression))
        return this
    }

    /**
     * Group by columns in the table
     */
    fun groupBy(
        selections: List<Expression>,
        groupBy: List<Expression>,
        having: Expression? = null
    ): Query {
        addClause(GroupByClause(GroupByExpression(selections, groupBy, having)))
        return this
    }

    /**
     * Limit the number of rows in the result
     */
    fun limit(limit: Int): Query {
        addClause(LimitClause(IntegerLiteral(limit)))
        return this
    }

    /**
     * Skip a number of rows in the result
     */
    fun offset(offset: Int): Query {
        addClause(OffsetClause(IntegerLiteral(offset)))
        return this
    }

    /**
     * Order the result by columns
     */
    fun orderBy(vararg ordering: OrderByExpression): Query {
        addClause(OrderByClause(ordering.map { it.direction }))
        return this
    }

    /**
     * Join with another table
     */
    fun join(
        database: String,
        table: String,
        joinType: JoinType,
        onClause: Expression
    ): Query {
        addClause(JoinClause(FromClause(database, table), joinType, JoinExpression(onClause)))
        return this
    }
}
