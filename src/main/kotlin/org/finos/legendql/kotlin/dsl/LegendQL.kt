package org.finos.legendql.kotlin.dsl

import org.finos.legendql.kotlin.model.*
import org.finos.legendql.kotlin.query.Query
import org.finos.legendql.kotlin.parser.Parser
import org.finos.legendql.kotlin.parser.ParseType
import kotlin.reflect.KClass

/**
 * Main DSL interface for LegendQL
 */
class LegendQL(database: org.finos.legendql.kotlin.model.Database, table: org.finos.legendql.kotlin.model.Table) {
    private val query = Query.fromTable(database, table)

    companion object {
        /**
         * Create a LegendQL instance from a table
         */
        fun fromTable(database: org.finos.legendql.kotlin.model.Database, table: org.finos.legendql.kotlin.model.Table): LegendQL {
            return LegendQL(database, table)
        }

        /**
         * Create a LegendQL instance from a database and table name with columns
         */
        fun fromDb(database: org.finos.legendql.kotlin.model.Database, table: String, columns: Map<String, KClass<*>>): LegendQL {
            return fromTable(database, org.finos.legendql.kotlin.model.Table(table, columns.toMutableMap()))
        }

        /**
         * Create a LegendQL instance from a lakehouse dataset
         */
        fun fromLh(dataset: org.finos.legendql.kotlin.model.Table): LegendQL {
            return fromTable(org.finos.legendql.kotlin.model.Database("lakehouse", listOf(dataset)), dataset)
        }

        /**
         * Create a table definition
         */
        fun table(databaseName: String, tableName: String, columns: Map<String, KClass<*>>): LegendQL {
            val table = org.finos.legendql.kotlin.model.Table(tableName, columns.toMutableMap())
            val database = org.finos.legendql.kotlin.model.Database(databaseName, listOf(table))
            return fromTable(database, table)
        }

        /**
         * Create multiple table definitions from a database
         */
        fun db(databaseName: String, tables: Map<String, Map<String, KClass<*>>>): List<LegendQL> {
            return tables.map { (tableName, columns) ->
                val table = org.finos.legendql.kotlin.model.Table(tableName, columns.toMutableMap())
                val database = org.finos.legendql.kotlin.model.Database(databaseName, listOf(table))
                fromTable(database, table)
            }
        }
    }

    /**
     * Bind the query to a runtime
     */
    fun <R : Runtime> bind(runtime: R): DataFrame<Any> {
        return query.bind(runtime)
    }

    /**
     * Evaluate the query with a runtime
     */
    fun <R : Runtime, T> eval(runtime: R): DataFrame<T> {
        return query.eval(runtime)
    }

    /**
     * Select columns from the table
     * 
     * In KTORM style:
     * ```
     * database.from(Employees).select(Employees.name, Employees.job)
     * ```
     */
    fun select(columns: (TableAlias) -> List<ColumnExpression>): LegendQL {
        val tableAlias = TableAlias(query.table)
        val expressions = columns(tableAlias)
        val expressionAndTable = Parser.parse(expressions, listOf(query.table), ParseType.SELECT)
        query.addClause(SelectionClause(expressionAndTable.first as List<Expression>))
        query.updateTable(expressionAndTable.second)
        return this
    }

    /**
     * Extend the table with computed columns
     * 
     * In KTORM style:
     * ```
     * database.from(Employees).select(Employees.name, Employees.salary * 1.1 as "newSalary")
     * ```
     */
    fun extend(columns: (TableAlias) -> List<ColumnExpression>): LegendQL {
        val tableAlias = TableAlias(query.table)
        val expressions = columns(tableAlias)
        val expressionAndTable = Parser.parse(expressions, listOf(query.table), ParseType.EXTEND)
        query.addClause(ExtendClause(expressionAndTable.first as List<Expression>))
        query.updateTable(expressionAndTable.second)
        return this
    }

    /**
     * Rename columns in the table
     * 
     * In KTORM style:
     * ```
     * database.from(Employees).select(Employees.id as "employeeId", Employees.name as "employeeName")
     * ```
     */
    fun rename(columns: (TableAlias) -> List<ColumnExpression>): LegendQL {
        val tableAlias = TableAlias(query.table)
        val expressions = columns(tableAlias)
        val expressionAndTable = Parser.parse(expressions, listOf(query.table), ParseType.RENAME)
        query.addClause(RenameClause(expressionAndTable.first as List<ColumnAliasExpression>))
        query.updateTable(expressionAndTable.second)
        return this
    }

    /**
     * Filter rows in the table
     * 
     * In KTORM style:
     * ```
     * database.from(Employees).select().where { (Employees.salary gt 1000) and (Employees.departmentId eq 1) }
     * ```
     */
    fun filter(condition: (TableAlias) -> Expression): LegendQL {
        val tableAlias = TableAlias(query.table)
        val expression = condition(tableAlias)
        val expressionAndTable = Parser.parse(expression, listOf(query.table), ParseType.FILTER)
        query.addClause(FilterClause(expressionAndTable.first.first()))
        query.updateTable(expressionAndTable.second)
        return this
    }

    /**
     * Group by columns in the table
     * 
     * In KTORM style:
     * ```
     * database.from(Employees)
     *     .select(Employees.departmentId, avg(Employees.salary).aliased("avgSalary"))
     *     .groupBy(Employees.departmentId)
     *     .having { avg(Employees.salary) gt 1000 }
     * ```
     */
    fun groupBy(aggregation: (TableAlias) -> GroupByExpression): LegendQL {
        val tableAlias = TableAlias(query.table)
        val expression = aggregation(tableAlias)
        val expressionAndTable = Parser.parse(expression, listOf(query.table), ParseType.GROUP_BY)
        val groupByExpr = expressionAndTable.first as GroupByExpression
        query.addClause(GroupByClause(groupByExpr))
        query.updateTable(expressionAndTable.second)
        return this
    }

    /**
     * Join with another table
     * 
     * In KTORM style:
     * ```
     * database.from(Employees)
     *     .innerJoin(Departments, on = Employees.departmentId eq Departments.id)
     *     .select(Employees.name, Departments.name)
     * ```
     */
    private fun join(other: LegendQL, joinCondition: (TableAlias, TableAlias) -> Pair<Expression, List<ColumnExpression>>, joinType: JoinType): LegendQL {
        val tableAlias1 = TableAlias(query.table)
        val tableAlias2 = TableAlias(other.query.table)
        val (condition, columns) = joinCondition(tableAlias1, tableAlias2)
        val expressionAndTable = Parser.parse(Pair(condition, columns), listOf(query.table, other.query.table), ParseType.JOIN)
        val joinExpr = expressionAndTable.first as JoinExpression
        query.addClause(JoinClause(
            FromClause(other.query.database.name, other.query.table.table),
            joinType,
            joinExpr
        ))
        query.updateTable(expressionAndTable.second)
        return this
    }

    /**
     * Inner join with another table
     */
    fun join(other: LegendQL, joinCondition: (TableAlias, TableAlias) -> Pair<Expression, List<ColumnExpression>>): LegendQL {
        return join(other, joinCondition, InnerJoinType())
    }

    /**
     * Left join with another table
     */
    fun leftJoin(other: LegendQL, joinCondition: (TableAlias, TableAlias) -> Pair<Expression, List<ColumnExpression>>): LegendQL {
        return join(other, joinCondition, LeftJoinType())
    }

    /**
     * Order the result by columns
     * 
     * In KTORM style:
     * ```
     * database.from(Employees).select().orderBy(Employees.salary.desc(), Employees.name.asc())
     * ```
     */
    fun orderBy(columns: (TableAlias) -> List<OrderByExpression>): LegendQL {
        val tableAlias = TableAlias(query.table)
        val expressions = columns(tableAlias)
        val expressionAndTable = Parser.parse(expressions, listOf(query.table), ParseType.ORDER_BY)
        query.addClause(OrderByClause(expressionAndTable.first.map { it as OrderType }))
        query.updateTable(expressionAndTable.second)
        return this
    }

    /**
     * Limit the number of rows in the result
     * 
     * In KTORM style:
     * ```
     * database.from(Employees).select().limit(10)
     * ```
     */
    fun limit(limit: Int): LegendQL {
        val clause = LimitClause(IntegerLiteral(limit))
        query.addClause(clause)
        return this
    }

    /**
     * Skip a number of rows in the result
     * 
     * In KTORM style:
     * ```
     * database.from(Employees).select().offset(10)
     * ```
     */
    fun offset(offset: Int): LegendQL {
        val clause = OffsetClause(IntegerLiteral(offset))
        query.addClause(clause)
        return this
    }

    /**
     * Skip a number of rows and limit the number of rows in the result
     * 
     * In KTORM style:
     * ```
     * database.from(Employees).select().limit(10, 20)
     * ```
     */
    fun take(offset: Int, limit: Int): LegendQL {
        query.addClause(OffsetClause(IntegerLiteral(offset)))
        query.addClause(LimitClause(IntegerLiteral(limit)))
        return this
    }

    /**
     * Get the table definition
     */
    fun getTableDefinition(): org.finos.legendql.kotlin.model.Table {
        return query.table
    }
}

/**
 * Table alias for DSL expressions
 */
class TableAlias(val table: org.finos.legendql.kotlin.model.Table) {
    /**
     * Get a column reference by name
     */
    operator fun get(name: String): ColumnReferenceExpression {
        return ColumnReferenceExpression(name)
    }
}

/**
 * Base interface for column expressions in the DSL
 */
interface ColumnExpression
