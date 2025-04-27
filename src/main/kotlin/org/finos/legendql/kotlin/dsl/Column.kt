package org.finos.legendql.kotlin.dsl

import org.finos.legendql.kotlin.model.*
import kotlin.reflect.KClass

/**
 * Represents a column in a table with type information
 */
class Column<T>(
    val name: String,
    val table: Table<*>,
    val type: KClass<*>
) {
    /**
     * Mark column as primary key
     */
    fun primaryKey(): Column<T> {
        return this
    }
    
    /**
     * Create a column reference expression
     */
    fun asExpression(): ColumnReferenceExpression {
        return ColumnReferenceExpression(name)
    }
    
    /**
     * Create column references for another table (for join conditions)
     */
    fun references(otherTable: Table<*>): Column<T> {
        return this
    }
}

// Extension infix functions for operators

/**
 * Equals operator
 */
infix fun <T> Column<T>.eq(value: T): BinaryExpression {
    val valueExpr = when (value) {
        is Int -> LiteralExpression(IntegerLiteral(value))
        is String -> LiteralExpression(StringLiteral(value))
        is Boolean -> LiteralExpression(BooleanLiteral(value))
        is Column<*> -> value.asExpression()
        null -> NullExpression()
        else -> throw IllegalArgumentException("Unsupported type: ${value?.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(valueExpr),
        EqualsBinaryOperator()
    )
}

/**
 * Less than operator
 */
infix fun <T : Comparable<T>> Column<T>.lt(value: T): BinaryExpression {
    val valueExpr = when (value) {
        is Int -> LiteralExpression(IntegerLiteral(value))
        is String -> LiteralExpression(StringLiteral(value))
        is Column<*> -> value.asExpression()
        else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(valueExpr),
        LessThanBinaryOperator()
    )
}

/**
 * Greater than operator
 */
infix fun <T : Comparable<T>> Column<T>.gt(value: T): BinaryExpression {
    val valueExpr = when (value) {
        is Int -> LiteralExpression(IntegerLiteral(value))
        is String -> LiteralExpression(StringLiteral(value))
        is Column<*> -> value.asExpression()
        else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(valueExpr),
        GreaterThanBinaryOperator()
    )
}

/**
 * Like operator for string pattern matching
 */
infix fun Column<String>.like(pattern: String): BinaryExpression {
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(LiteralExpression(StringLiteral(pattern))),
        LikeBinaryOperator()
    )
}

/**
 * Logical AND operator
 */
infix fun BinaryExpression.and(expr: BinaryExpression): BinaryExpression {
    return BinaryExpression(
        OperandExpression(this),
        OperandExpression(expr),
        AndBinaryOperator()
    )
}

/**
 * Logical OR operator
 */
infix fun BinaryExpression.or(expr: BinaryExpression): BinaryExpression {
    return BinaryExpression(
        OperandExpression(this),
        OperandExpression(expr),
        OrBinaryOperator()
    )
}

/**
 * Check if value is null
 */
fun Column<*>.isNull(): BinaryExpression {
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(NullExpression()),
        IsNullBinaryOperator()
    )
}

/**
 * Check if value is not null
 */
fun Column<*>.isNotNull(): BinaryExpression {
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(NullExpression()),
        IsNotNullBinaryOperator()
    )
}
