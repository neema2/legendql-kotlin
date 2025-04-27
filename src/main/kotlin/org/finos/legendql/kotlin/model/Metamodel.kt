package org.finos.legendql.kotlin.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Base interface for all literals in the metamodel
 */
interface Literal<T> {
    fun value(): T
    fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R
}

/**
 * Integer literal implementation
 */
data class IntegerLiteral(val value: Int) : Literal<Int> {
    override fun value(): Int = value
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitIntegerLiteral(this, parameter)
}

/**
 * String literal implementation
 */
data class StringLiteral(val value: String) : Literal<String> {
    override fun value(): String = value
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitStringLiteral(this, parameter)
}

/**
 * Date literal implementation
 */
data class DateLiteral(val value: LocalDate) : Literal<LocalDate> {
    override fun value(): LocalDate = value
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitDateLiteral(this, parameter)
}

/**
 * Boolean literal implementation
 */
data class BooleanLiteral(val value: Boolean) : Literal<Boolean> {
    override fun value(): Boolean = value
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitBooleanLiteral(this, parameter)
}

/**
 * Base interface for all functions in the metamodel
 */
interface Function {
    fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R
}

/**
 * Count function implementation
 */
class CountFunction : Function {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitCountFunction(this, parameter)
}

/**
 * Average function implementation
 */
class AverageFunction : Function {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitAverageFunction(this, parameter)
}

/**
 * Modulo function implementation
 */
class ModuloFunction : Function {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitModuloFunction(this, parameter)
}

/**
 * Exponent function implementation
 */
class ExponentFunction : Function {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitExponentFunction(this, parameter)
}

/**
 * Base interface for all expressions in the metamodel
 */
interface Expression {
    fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R
}

/**
 * Base interface for all operators in the metamodel
 */
interface Operator {
    fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R
}

/**
 * Base interface for unary operators
 */
interface UnaryOperator : Operator

/**
 * NOT unary operator implementation
 */
class NotUnaryOperator : UnaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitNotUnaryOperator(this, parameter)
}

/**
 * Base interface for binary operators
 */
interface BinaryOperator : Operator

/**
 * EQUALS binary operator implementation
 */
class EqualsBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitEqualsBinaryOperator(this, parameter)
}

/**
 * NOT EQUALS binary operator implementation
 */
class NotEqualsBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitNotEqualsBinaryOperator(this, parameter)
}

/**
 * GREATER THAN binary operator implementation
 */
class GreaterThanBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitGreaterThanBinaryOperator(this, parameter)
}

/**
 * GREATER THAN OR EQUALS binary operator implementation
 */
class GreaterThanEqualsBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitGreaterThanEqualsBinaryOperator(this, parameter)
}

/**
 * LESS THAN binary operator implementation
 */
class LessThanBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitLessThanBinaryOperator(this, parameter)
}

/**
 * LESS THAN OR EQUALS binary operator implementation
 */
class LessThanEqualsBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitLessThanEqualsBinaryOperator(this, parameter)
}

/**
 * IN binary operator implementation
 */
class InBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitInBinaryOperator(this, parameter)
}

/**
 * NOT IN binary operator implementation
 */
class NotInBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitNotInBinaryOperator(this, parameter)
}

/**
 * IS binary operator implementation
 */
class IsBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitIsBinaryOperator(this, parameter)
}

/**
 * IS NOT binary operator implementation
 */
class IsNotBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitIsNotBinaryOperator(this, parameter)
}

/**
 * AND binary operator implementation
 */
class AndBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitAndBinaryOperator(this, parameter)
}

/**
 * OR binary operator implementation
 */
class OrBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitOrBinaryOperator(this, parameter)
}

/**
 * ADD binary operator implementation
 */
class AddBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitAddBinaryOperator(this, parameter)
}

/**
 * MULTIPLY binary operator implementation
 */
class MultiplyBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitMultiplyBinaryOperator(this, parameter)
}

/**
 * SUBTRACT binary operator implementation
 */
class SubtractBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitSubtractBinaryOperator(this, parameter)
}

/**
 * DIVIDE binary operator implementation
 */
class DivideBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitDivideBinaryOperator(this, parameter)
}

/**
 * BITWISE AND binary operator implementation
 */
class BitwiseAndBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitBitwiseAndBinaryOperator(this, parameter)
}

/**
 * BITWISE OR binary operator implementation
 */
class BitwiseOrBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitBitwiseOrBinaryOperator(this, parameter)
}

/**
 * LIKE binary operator implementation
 */
class LikeBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitLikeBinaryOperator(this, parameter)
}

/**
 * IS NULL binary operator implementation
 */
class IsNullBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitIsNullBinaryOperator(this, parameter)
}

/**
 * IS NOT NULL binary operator implementation
 */
class IsNotNullBinaryOperator : BinaryOperator {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitIsNotNullBinaryOperator(this, parameter)
}

/**
 * Null expression implementation
 */
class NullExpression : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitNullExpression(this, parameter)
}

/**
 * Operand expression implementation
 */
data class OperandExpression(val expression: Expression) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitOperandExpression(this, parameter)
}

/**
 * Unary expression implementation
 */
data class UnaryExpression(
    val operator: UnaryOperator,
    val expression: OperandExpression
) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitUnaryExpression(this, parameter)
}

/**
 * Binary expression implementation
 */
data class BinaryExpression(
    val left: OperandExpression,
    val right: OperandExpression,
    val operator: BinaryOperator
) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitBinaryExpression(this, parameter)
}

/**
 * Literal expression implementation
 */
data class LiteralExpression(val literal: Literal<*>) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitLiteralExpression(this, parameter)
}

/**
 * Base interface for alias expressions
 */
interface AliasExpression : Expression {
    val alias: String?
}

/**
 * Variable alias expression implementation
 */
data class VariableAliasExpression(override val alias: String?) : AliasExpression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitVariableAliasExpression(this, parameter)
}

/**
 * Column reference expression implementation
 */
data class ColumnReferenceExpression(val name: String) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitColumnReferenceExpression(this, parameter)
}

/**
 * Column alias expression implementation
 */
data class ColumnAliasExpression(
    override val alias: String?,
    val reference: ColumnReferenceExpression? = null
) : AliasExpression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitColumnAliasExpression(this, parameter)
}

/**
 * Computed column alias expression implementation
 */
data class ComputedColumnAliasExpression(
    override val alias: String?,
    val expression: Expression? = null
) : AliasExpression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitComputedColumnAliasExpression(this, parameter)
}

/**
 * If expression implementation
 */
data class IfExpression(
    val test: Expression,
    val body: Expression,
    val orelse: Expression
) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitIfExpression(this, parameter)
}

/**
 * Base interface for order types
 */
interface OrderType : Expression

/**
 * Ascending order type implementation
 */
class AscendingOrderType : OrderType {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitAscendingOrderType(this, parameter)
}

/**
 * Descending order type implementation
 */
class DescendingOrderType : OrderType {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitDescendingOrderType(this, parameter)
}

/**
 * Order by expression implementation
 */
data class OrderByExpression(
    val direction: OrderType,
    val expression: Expression
) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitOrderByExpression(this, parameter)
}

/**
 * Function expression implementation
 */
data class FunctionExpression(
    val function: Function,
    val parameters: List<Expression>
) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitFunctionExpression(this, parameter)
}

/**
 * Map reduce expression implementation
 */
data class MapReduceExpression(
    val mapExpression: Expression,
    val reduceExpression: Expression
) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitMapReduceExpression(this, parameter)
}

/**
 * Lambda expression implementation
 */
data class LambdaExpression(
    val parameters: List<String>,
    val expression: Expression
) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitLambdaExpression(this, parameter)
}

/**
 * Base interface for all clauses in the metamodel
 */
interface Clause {
    fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R
}

/**
 * Rename clause implementation
 */
data class RenameClause(val columnAliases: List<ColumnAliasExpression>) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitRenameClause(this, parameter)
}

/**
 * Filter clause implementation
 */
data class FilterClause(val expression: Expression) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitFilterClause(this, parameter)
}

/**
 * Selection clause implementation
 */
data class SelectionClause(val expressions: List<Expression>) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitSelectionClause(this, parameter)
}

/**
 * Extend clause implementation
 */
data class ExtendClause(val expressions: List<Expression>) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitExtendClause(this, parameter)
}

/**
 * Group by clause implementation
 */
data class GroupByClause(val expression: Expression) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitGroupByClause(this, parameter)
}

/**
 * Group by expression implementation
 */
data class GroupByExpression(
    val selections: List<Expression>,
    val expressions: List<Expression>,
    val having: Expression? = null
) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitGroupByExpression(this, parameter)
}

/**
 * Distinct clause implementation
 */
data class DistinctClause(val expressions: List<Expression>) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitDistinctClause(this, parameter)
}

/**
 * Order by clause implementation
 */
data class OrderByClause(val ordering: List<OrderType>) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitOrderByClause(this, parameter)
}

/**
 * Limit clause implementation
 */
data class LimitClause(val value: IntegerLiteral) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitLimitClause(this, parameter)
}

/**
 * From clause implementation
 */
data class FromClause(val database: String, val table: String) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitFromClause(this, parameter)
}

/**
 * Base interface for join types
 */
interface JoinType {
    fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R
}

/**
 * Inner join type implementation
 */
class InnerJoinType : JoinType {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitInnerJoinType(this, parameter)
}

/**
 * Left join type implementation
 */
class LeftJoinType : JoinType {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitLeftJoinType(this, parameter)
}

/**
 * Join expression implementation
 */
data class JoinExpression(val on: Expression) : Expression {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitJoinExpression(this, parameter)
}

/**
 * Join clause implementation
 */
data class JoinClause(
    val fromClause: FromClause,
    val joinType: JoinType,
    val onClause: JoinExpression
) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitJoinClause(this, parameter)
}

/**
 * Offset clause implementation
 */
data class OffsetClause(val value: IntegerLiteral) : Clause {
    override fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitOffsetClause(this, parameter)
}

/**
 * Base interface for runtime implementations
 */
interface Runtime {
    fun <T> eval(clauses: List<Clause>): T
    fun executableToString(clauses: List<Clause>): String
    fun <P, R> visit(visitor: ExecutionVisitor<P, R>, parameter: P): R = 
        visitor.visitRuntime(this, parameter)
}

/**
 * DataFrame implementation
 */
data class DataFrame<T>(
    val runtime: Runtime,
    val clauses: List<Clause>,
    var results: T? = null
) {
    fun eval(): DataFrame<T> {
        results = runtime.eval(clauses)
        return this
    }

    fun data(): T? = results

    fun executableToString(): String = runtime.executableToString(clauses)
}

/**
 * Execution visitor interface for the visitor pattern
 */
interface ExecutionVisitor<P, R> {
    fun visitRuntime(runtime: Runtime, parameter: P): R
    fun visitFromClause(fromClause: FromClause, parameter: P): R
    fun visitIntegerLiteral(integerLiteral: IntegerLiteral, parameter: P): R
    fun visitStringLiteral(stringLiteral: StringLiteral, parameter: P): R
    fun visitDateLiteral(dateLiteral: DateLiteral, parameter: P): R
    fun visitBooleanLiteral(booleanLiteral: BooleanLiteral, parameter: P): R
    fun visitOperandExpression(operandExpression: OperandExpression, parameter: P): R
    fun visitNotUnaryOperator(notUnaryOperator: NotUnaryOperator, parameter: P): R
    fun visitEqualsBinaryOperator(equalsBinaryOperator: EqualsBinaryOperator, parameter: P): R
    fun visitNotEqualsBinaryOperator(notEqualsBinaryOperator: NotEqualsBinaryOperator, parameter: P): R
    fun visitGreaterThanBinaryOperator(greaterThanBinaryOperator: GreaterThanBinaryOperator, parameter: P): R
    fun visitGreaterThanEqualsBinaryOperator(greaterThanEqualsBinaryOperator: GreaterThanEqualsBinaryOperator, parameter: P): R
    fun visitLessThanBinaryOperator(lessThanBinaryOperator: LessThanBinaryOperator, parameter: P): R
    fun visitLessThanEqualsBinaryOperator(lessThanEqualsBinaryOperator: LessThanEqualsBinaryOperator, parameter: P): R
    fun visitAndBinaryOperator(andBinaryOperator: AndBinaryOperator, parameter: P): R
    fun visitOrBinaryOperator(orBinaryOperator: OrBinaryOperator, parameter: P): R
    fun visitAddBinaryOperator(addBinaryOperator: AddBinaryOperator, parameter: P): R
    fun visitMultiplyBinaryOperator(multiplyBinaryOperator: MultiplyBinaryOperator, parameter: P): R
    fun visitSubtractBinaryOperator(subtractBinaryOperator: SubtractBinaryOperator, parameter: P): R
    fun visitDivideBinaryOperator(divideBinaryOperator: DivideBinaryOperator, parameter: P): R
    fun visitLiteralExpression(literalExpression: LiteralExpression, parameter: P): R
    fun visitVariableAliasExpression(variableAliasExpression: VariableAliasExpression, parameter: P): R
    fun visitComputedColumnAliasExpression(computedColumnAliasExpression: ComputedColumnAliasExpression, parameter: P): R
    fun visitColumnAliasExpression(columnAliasExpression: ColumnAliasExpression, parameter: P): R
    fun visitFunctionExpression(functionExpression: FunctionExpression, parameter: P): R
    fun visitMapReduceExpression(mapReduceExpression: MapReduceExpression, parameter: P): R
    fun visitLambdaExpression(lambdaExpression: LambdaExpression, parameter: P): R
    fun visitCountFunction(countFunction: CountFunction, parameter: P): R
    fun visitAverageFunction(averageFunction: AverageFunction, parameter: P): R
    fun visitModuloFunction(moduloFunction: ModuloFunction, parameter: P): R
    fun visitExponentFunction(exponentFunction: ExponentFunction, parameter: P): R
    fun visitFilterClause(filterClause: FilterClause, parameter: P): R
    fun visitSelectionClause(selectionClause: SelectionClause, parameter: P): R
    fun visitExtendClause(extendClause: ExtendClause, parameter: P): R
    fun visitGroupByClause(groupByClause: GroupByClause, parameter: P): R
    fun visitGroupByExpression(groupByExpression: GroupByExpression, parameter: P): R
    fun visitDistinctClause(distinctClause: DistinctClause, parameter: P): R
    fun visitOrderByClause(orderByClause: OrderByClause, parameter: P): R
    fun visitLimitClause(limitClause: LimitClause, parameter: P): R
    fun visitJoinExpression(joinExpression: JoinExpression, parameter: P): R
    fun visitJoinClause(joinClause: JoinClause, parameter: P): R
    fun visitInnerJoinType(innerJoinType: InnerJoinType, parameter: P): R
    fun visitLeftJoinType(leftJoinType: LeftJoinType, parameter: P): R
    fun visitColumnReferenceExpression(columnReferenceExpression: ColumnReferenceExpression, parameter: P): R
    fun visitIfExpression(ifExpression: IfExpression, parameter: P): R
    fun visitOrderByExpression(orderByExpression: OrderByExpression, parameter: P): R
    fun visitAscendingOrderType(ascendingOrderType: AscendingOrderType, parameter: P): R
    fun visitDescendingOrderType(descendingOrderType: DescendingOrderType, parameter: P): R
    fun visitRenameClause(renameClause: RenameClause, parameter: P): R
    fun visitOffsetClause(offsetClause: OffsetClause, parameter: P): R
    fun visitUnaryExpression(unaryExpression: UnaryExpression, parameter: P): R
    fun visitBinaryExpression(binaryExpression: BinaryExpression, parameter: P): R
    fun visitInBinaryOperator(inBinaryOperator: InBinaryOperator, parameter: P): R
    fun visitNotInBinaryOperator(notInBinaryOperator: NotInBinaryOperator, parameter: P): R
    fun visitIsBinaryOperator(isBinaryOperator: IsBinaryOperator, parameter: P): R
    fun visitIsNotBinaryOperator(isNotBinaryOperator: IsNotBinaryOperator, parameter: P): R
    fun visitBitwiseAndBinaryOperator(bitwiseAndBinaryOperator: BitwiseAndBinaryOperator, parameter: P): R
    fun visitBitwiseOrBinaryOperator(bitwiseOrBinaryOperator: BitwiseOrBinaryOperator, parameter: P): R
    fun visitLikeBinaryOperator(likeBinaryOperator: LikeBinaryOperator, parameter: P): R
    fun visitIsNullBinaryOperator(isNullBinaryOperator: IsNullBinaryOperator, parameter: P): R
    fun visitIsNotNullBinaryOperator(isNotNullBinaryOperator: IsNotNullBinaryOperator, parameter: P): R
    fun visitNullExpression(nullExpression: NullExpression, parameter: P): R
}
