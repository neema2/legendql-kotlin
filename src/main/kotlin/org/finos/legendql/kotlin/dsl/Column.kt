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
    
    /**
     * Create an average aggregation
     */
    fun avg(): FunctionExpression {
        return FunctionExpression(AverageFunction(), listOf(ColumnReferenceExpression(name)))
    }
}

// Extension operator functions for comparison

/**
 * Helper object to track operator context
 */
object OperatorContext {
    private val lastExpression = ThreadLocal<BinaryExpression>()
    private val lastComparisonType = ThreadLocal<ComparisonType>()
    
    fun setLastExpression(expr: BinaryExpression) {
        lastExpression.set(expr)
    }
    
    fun getLastExpression(): BinaryExpression? {
        val expr = lastExpression.get()
        lastExpression.remove() // Clear after getting to avoid memory leaks
        return expr
    }
    
    fun setLastComparisonType(type: ComparisonType) {
        lastComparisonType.set(type)
    }
    
    fun getLastComparisonType(): ComparisonType {
        return lastComparisonType.get() ?: ComparisonType.EQUALS
    }
    
    fun clearContext() {
        lastExpression.remove()
        lastComparisonType.remove()
    }
}

/**
 * Enum for comparison types
 */
enum class ComparisonType {
    EQUALS,
    NOT_EQUALS,
    LESS_THAN,
    GREATER_THAN,
    LESS_THAN_EQUALS,
    GREATER_THAN_EQUALS
}

// Operator overloading removed - using infix functions instead

/**
 * Legacy equals operator (eq) - kept for backward compatibility
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
 * Equals operator for comparing two columns
 */
infix fun <T> Column<T>.eq(other: Column<T>): BinaryExpression {
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(other.asExpression()),
        EqualsBinaryOperator()
    )
}

/**
 * Legacy less than operator (lt) - kept for backward compatibility
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
 * Legacy greater than operator (gt) - kept for backward compatibility
 */
infix fun <T : Comparable<T>> Column<T>.gt(value: T): BinaryExpression {
    val valueExpr = when (value) {
        is Int -> LiteralExpression(IntegerLiteral(value))
        is Double -> LiteralExpression(DoubleLiteral(value))
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
 * DSL-specific implementation for equals (==) operator
 * This is what actually gets called by the DSL
 */
fun <T> captureEquals(left: Column<T>, right: Any?): BinaryExpression {
    val valueExpr = when (right) {
        is Int -> LiteralExpression(IntegerLiteral(right))
        is String -> LiteralExpression(StringLiteral(right))
        is Boolean -> LiteralExpression(BooleanLiteral(right))
        is Column<*> -> right.asExpression()
        null -> NullExpression()
        else -> throw IllegalArgumentException("Unsupported type: ${right?.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(left.asExpression()),
        OperandExpression(valueExpr),
        EqualsBinaryOperator()
    )
}

/**
 * DSL-specific implementation for not equals (!=) operator
 * This is what actually gets called by the DSL
 */
fun <T> captureNotEquals(left: Column<T>, right: Any?): BinaryExpression {
    val valueExpr = when (right) {
        is Int -> LiteralExpression(IntegerLiteral(right))
        is String -> LiteralExpression(StringLiteral(right))
        is Boolean -> LiteralExpression(BooleanLiteral(right))
        is Column<*> -> right.asExpression()
        null -> NullExpression()
        else -> throw IllegalArgumentException("Unsupported type: ${right?.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(left.asExpression()),
        OperandExpression(valueExpr),
        NotEqualsBinaryOperator()
    )
}

/**
 * DSL-specific implementation for less than (<) operator
 * This is what actually gets called by the DSL
 */
fun <T : Comparable<T>> captureLessThan(left: Column<T>, right: T): BinaryExpression {
    val valueExpr = when (right) {
        is Int -> LiteralExpression(IntegerLiteral(right))
        is String -> LiteralExpression(StringLiteral(right))
        is Double -> LiteralExpression(DoubleLiteral(right))
        is Column<*> -> right.asExpression()
        else -> throw IllegalArgumentException("Unsupported type: ${right.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(left.asExpression()),
        OperandExpression(valueExpr),
        LessThanBinaryOperator()
    )
}

/**
 * DSL-specific implementation for greater than (>) operator
 * This is what actually gets called by the DSL
 */
fun <T : Comparable<T>> captureGreaterThan(left: Column<T>, right: T): BinaryExpression {
    val valueExpr = when (right) {
        is Int -> LiteralExpression(IntegerLiteral(right))
        is String -> LiteralExpression(StringLiteral(right))
        is Double -> LiteralExpression(DoubleLiteral(right))
        is Column<*> -> right.asExpression()
        else -> throw IllegalArgumentException("Unsupported type: ${right.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(left.asExpression()),
        OperandExpression(valueExpr),
        GreaterThanBinaryOperator()
    )
}

/**
 * DSL-specific implementation for less than or equal (<=) operator
 * This is what actually gets called by the DSL
 */
fun <T : Comparable<T>> captureLessThanOrEqual(left: Column<T>, right: T): BinaryExpression {
    val valueExpr = when (right) {
        is Int -> LiteralExpression(IntegerLiteral(right))
        is String -> LiteralExpression(StringLiteral(right))
        is Double -> LiteralExpression(DoubleLiteral(right))
        is Column<*> -> right.asExpression()
        else -> throw IllegalArgumentException("Unsupported type: ${right.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(left.asExpression()),
        OperandExpression(valueExpr),
        LessThanOrEqualBinaryOperator()
    )
}

/**
 * DSL-specific implementation for greater than or equal (>=) operator
 * This is what actually gets called by the DSL
 */
fun <T : Comparable<T>> captureGreaterThanOrEqual(left: Column<T>, right: T): BinaryExpression {
    val valueExpr = when (right) {
        is Int -> LiteralExpression(IntegerLiteral(right))
        is String -> LiteralExpression(StringLiteral(right))
        is Double -> LiteralExpression(DoubleLiteral(right))
        is Column<*> -> right.asExpression()
        else -> throw IllegalArgumentException("Unsupported type: ${right.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(left.asExpression()),
        OperandExpression(valueExpr),
        GreaterThanOrEqualBinaryOperator()
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

// Logical operators moved to Extensions.kt

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

/**
 * Addition operator
 */
operator fun <T : Number> Column<T>.plus(value: Number): BinaryExpression {
    val valueExpr = when (value) {
        is Int -> LiteralExpression(IntegerLiteral(value))
        is Double -> LiteralExpression(DoubleLiteral(value))
        is Long -> LiteralExpression(LongLiteral(value))
        else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(valueExpr),
        AdditionBinaryOperator()
    )
}

/**
 * Subtraction operator
 */
operator fun <T : Number> Column<T>.minus(value: Number): BinaryExpression {
    val valueExpr = when (value) {
        is Int -> LiteralExpression(IntegerLiteral(value))
        is Double -> LiteralExpression(DoubleLiteral(value))
        is Long -> LiteralExpression(LongLiteral(value))
        else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(valueExpr),
        SubtractionBinaryOperator()
    )
}

/**
 * Multiplication operator
 */
operator fun <T : Number> Column<T>.times(value: Number): BinaryExpression {
    val valueExpr = when (value) {
        is Int -> LiteralExpression(IntegerLiteral(value))
        is Double -> LiteralExpression(DoubleLiteral(value))
        is Long -> LiteralExpression(LongLiteral(value))
        else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(valueExpr),
        MultiplicationBinaryOperator()
    )
}

/**
 * Division operator
 */
operator fun <T : Number> Column<T>.div(value: Number): BinaryExpression {
    val valueExpr = when (value) {
        is Int -> LiteralExpression(IntegerLiteral(value))
        is Double -> LiteralExpression(DoubleLiteral(value))
        is Long -> LiteralExpression(LongLiteral(value))
        else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass}")
    }
    
    return BinaryExpression(
        OperandExpression(this.asExpression()),
        OperandExpression(valueExpr),
        DivisionBinaryOperator()
    )
}
