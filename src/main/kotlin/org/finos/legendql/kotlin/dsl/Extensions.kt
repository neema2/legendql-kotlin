package org.finos.legendql.kotlin.dsl

import org.finos.legendql.kotlin.model.*

/**
 * Extension function to create ascending order expression
 */
fun <T> Column<T>.asc(): OrderByExpression {
    return OrderByExpression(AscendingOrderType(), this.asExpression())
}

/**
 * Extension function to create descending order expression
 */
fun <T> Column<T>.desc(): OrderByExpression {
    return OrderByExpression(DescendingOrderType(), this.asExpression())
}

/**
 * Average function for aggregation
 */
fun <T : Number> avg(column: Column<T>): FunctionExpression {
    return FunctionExpression(AverageFunction(), listOf(column.asExpression()))
}

/**
 * Count function for aggregation
 */
fun <T> count(column: Column<T>): FunctionExpression {
    return FunctionExpression(CountFunction(), listOf(column.asExpression()))
}

/**
 * Sum function for aggregation
 */
fun <T : Number> sum(column: Column<T>): FunctionExpression {
    return FunctionExpression(SumFunction(), listOf(column.asExpression()))
}

/**
 * Min function for aggregation
 */
fun <T : Comparable<T>> min(column: Column<T>): FunctionExpression {
    return FunctionExpression(MinFunction(), listOf(column.asExpression()))
}

/**
 * Max function for aggregation
 */
fun <T : Comparable<T>> max(column: Column<T>): FunctionExpression {
    return FunctionExpression(MaxFunction(), listOf(column.asExpression()))
}

/**
 * Extension function to create an alias for a column
 */
fun <T> Column<T>.aliased(alias: String): ColumnAliasExpression {
    return ColumnAliasExpression(alias, this.asExpression())
}

/**
 * Extension function to create an alias for a function expression
 */
fun FunctionExpression.aliased(alias: String): ComputedColumnAliasExpression {
    return ComputedColumnAliasExpression(alias, this)
}

/**
 * Extension function to create an alias for a binary expression
 */
fun BinaryExpression.aliased(alias: String): ComputedColumnAliasExpression {
    return ComputedColumnAliasExpression(alias, this)
}
