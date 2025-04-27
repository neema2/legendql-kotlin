package org.finos.legendql.kotlin.dialect.purerelation

import org.finos.legendql.kotlin.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Pure relation runtime implementation
 */
class PureRuntime : Runtime {
    override fun <T> eval(clauses: List<Clause>): T {
        throw UnsupportedOperationException("Pure runtime does not support evaluation")
    }

    override fun executableToString(clauses: List<Clause>): String {
        val visitor = PureRelationExpressionVisitor()
        return visitor.visitClauses(clauses, Unit)
    }
}

/**
 * Non-executable pure runtime implementation for testing
 */
class NonExecutablePureRuntime : Runtime {
    override fun <T> eval(clauses: List<Clause>): T {
        throw UnsupportedOperationException("Non-executable pure runtime does not support evaluation")
    }

    override fun executableToString(clauses: List<Clause>): String {
        val visitor = PureRelationExpressionVisitor()
        return visitor.visitClauses(clauses, Unit)
    }
}

/**
 * Pure relation expression visitor for converting metamodel to pure relation syntax
 */
class PureRelationExpressionVisitor : ExecutionVisitor<Unit, String> {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Visit clauses and convert to pure relation syntax
     */
    fun visitClauses(clauses: List<Clause>, parameter: Unit): String {
        val fromClause = clauses.filterIsInstance<FromClause>().firstOrNull()
            ?: throw IllegalArgumentException("No from clause found")
        
        val database = fromClause.database
        val table = fromClause.table
        
        val builder = StringBuilder()
        builder.append("$database.$table")
        
        // Process other clauses in order
        for (clause in clauses) {
            if (clause is FromClause) continue
            
            when (clause) {
                is SelectionClause -> {
                    builder.append("\n->project([")
                    builder.append(clause.expressions.joinToString(", ") { it.visit(this, parameter) })
                    builder.append("])")
                }
                is RenameClause -> {
                    builder.append("\n->rename([")
                    builder.append(clause.columnAliases.joinToString(", ") { it.visit(this, parameter) })
                    builder.append("])")
                }
                is ExtendClause -> {
                    builder.append("\n->extend([")
                    builder.append(clause.expressions.joinToString(", ") { it.visit(this, parameter) })
                    builder.append("])")
                }
                is FilterClause -> {
                    builder.append("\n->filter(")
                    builder.append(clause.expression.visit(this, parameter))
                    builder.append(")")
                }
                is GroupByClause -> {
                    builder.append("\n->groupBy(")
                    builder.append(clause.expression.visit(this, parameter))
                    builder.append(")")
                }
                is OrderByClause -> {
                    builder.append("\n->sort([")
                    builder.append(clause.ordering.joinToString(", ") { it.visit(this, parameter) })
                    builder.append("])")
                }
                is LimitClause -> {
                    builder.append("\n->take(")
                    builder.append(clause.value.visit(this, parameter))
                    builder.append(")")
                }
                is OffsetClause -> {
                    builder.append("\n->drop(")
                    builder.append(clause.value.visit(this, parameter))
                    builder.append(")")
                }
                is JoinClause -> {
                    builder.append("\n->join(")
                    builder.append(clause.fromClause.database)
                    builder.append(".")
                    builder.append(clause.fromClause.table)
                    builder.append(", ")
                    builder.append(clause.joinType.visit(this, parameter))
                    builder.append(", ")
                    builder.append(clause.onClause.visit(this, parameter))
                    builder.append(")")
                }
                else -> throw UnsupportedOperationException("Unsupported clause type: ${clause.javaClass.simpleName}")
            }
        }
        
        return builder.toString()
    }

    override fun visitRuntime(runtime: Runtime, parameter: Unit): String {
        return "runtime"
    }

    override fun visitFromClause(fromClause: FromClause, parameter: Unit): String {
        return "${fromClause.database}.${fromClause.table}"
    }

    override fun visitIntegerLiteral(integerLiteral: IntegerLiteral, parameter: Unit): String {
        return integerLiteral.value.toString()
    }

    override fun visitStringLiteral(stringLiteral: StringLiteral, parameter: Unit): String {
        return "'${stringLiteral.value}'"
    }

    override fun visitDateLiteral(dateLiteral: DateLiteral, parameter: Unit): String {
        return "%${dateLiteral.value.format(dateFormatter)}%"
    }

    override fun visitBooleanLiteral(booleanLiteral: BooleanLiteral, parameter: Unit): String {
        return booleanLiteral.value.toString()
    }

    override fun visitOperandExpression(operandExpression: OperandExpression, parameter: Unit): String {
        return operandExpression.expression.visit(this, parameter)
    }

    override fun visitNotUnaryOperator(notUnaryOperator: NotUnaryOperator, parameter: Unit): String {
        return "not"
    }

    override fun visitEqualsBinaryOperator(equalsBinaryOperator: EqualsBinaryOperator, parameter: Unit): String {
        return "=="
    }

    override fun visitNotEqualsBinaryOperator(notEqualsBinaryOperator: NotEqualsBinaryOperator, parameter: Unit): String {
        return "!="
    }

    override fun visitGreaterThanBinaryOperator(greaterThanBinaryOperator: GreaterThanBinaryOperator, parameter: Unit): String {
        return ">"
    }

    override fun visitGreaterThanEqualsBinaryOperator(greaterThanEqualsBinaryOperator: GreaterThanEqualsBinaryOperator, parameter: Unit): String {
        return ">="
    }

    override fun visitLessThanBinaryOperator(lessThanBinaryOperator: LessThanBinaryOperator, parameter: Unit): String {
        return "<"
    }

    override fun visitLessThanEqualsBinaryOperator(lessThanEqualsBinaryOperator: LessThanEqualsBinaryOperator, parameter: Unit): String {
        return "<="
    }

    override fun visitAndBinaryOperator(andBinaryOperator: AndBinaryOperator, parameter: Unit): String {
        return "&&"
    }

    override fun visitOrBinaryOperator(orBinaryOperator: OrBinaryOperator, parameter: Unit): String {
        return "||"
    }

    override fun visitAddBinaryOperator(addBinaryOperator: AddBinaryOperator, parameter: Unit): String {
        return "+"
    }

    override fun visitMultiplyBinaryOperator(multiplyBinaryOperator: MultiplyBinaryOperator, parameter: Unit): String {
        return "*"
    }

    override fun visitSubtractBinaryOperator(subtractBinaryOperator: SubtractBinaryOperator, parameter: Unit): String {
        return "-"
    }

    override fun visitDivideBinaryOperator(divideBinaryOperator: DivideBinaryOperator, parameter: Unit): String {
        return "/"
    }

    override fun visitLiteralExpression(literalExpression: LiteralExpression, parameter: Unit): String {
        return literalExpression.literal.visit(this, parameter)
    }

    override fun visitVariableAliasExpression(variableAliasExpression: VariableAliasExpression, parameter: Unit): String {
        return variableAliasExpression.alias ?: "var"
    }

    override fun visitComputedColumnAliasExpression(computedColumnAliasExpression: ComputedColumnAliasExpression, parameter: Unit): String {
        val expression = computedColumnAliasExpression.expression?.visit(this, parameter) ?: "null"
        val alias = computedColumnAliasExpression.alias ?: "column"
        return "$expression as $alias"
    }

    override fun visitColumnAliasExpression(columnAliasExpression: ColumnAliasExpression, parameter: Unit): String {
        val reference = columnAliasExpression.reference?.visit(this, parameter) ?: "column"
        val alias = columnAliasExpression.alias ?: "column"
        return "$reference as $alias"
    }

    override fun visitFunctionExpression(functionExpression: FunctionExpression, parameter: Unit): String {
        val function = functionExpression.function.visit(this, parameter)
        val params = functionExpression.parameters.joinToString(", ") { it.visit(this, parameter) }
        return "$function($params)"
    }

    override fun visitMapReduceExpression(mapReduceExpression: MapReduceExpression, parameter: Unit): String {
        val map = mapReduceExpression.mapExpression.visit(this, parameter)
        val reduce = mapReduceExpression.reduceExpression.visit(this, parameter)
        return "map($map, $reduce)"
    }

    override fun visitLambdaExpression(lambdaExpression: LambdaExpression, parameter: Unit): String {
        val params = lambdaExpression.parameters.joinToString(", ")
        val expression = lambdaExpression.expression.visit(this, parameter)
        return "{$params|$expression}"
    }

    override fun visitCountFunction(countFunction: CountFunction, parameter: Unit): String {
        return "count"
    }

    override fun visitAverageFunction(averageFunction: AverageFunction, parameter: Unit): String {
        return "avg"
    }

    override fun visitModuloFunction(moduloFunction: ModuloFunction, parameter: Unit): String {
        return "mod"
    }

    override fun visitExponentFunction(exponentFunction: ExponentFunction, parameter: Unit): String {
        return "pow"
    }

    override fun visitFilterClause(filterClause: FilterClause, parameter: Unit): String {
        return filterClause.expression.visit(this, parameter)
    }

    override fun visitSelectionClause(selectionClause: SelectionClause, parameter: Unit): String {
        return selectionClause.expressions.joinToString(", ") { it.visit(this, parameter) }
    }

    override fun visitExtendClause(extendClause: ExtendClause, parameter: Unit): String {
        return extendClause.expressions.joinToString(", ") { it.visit(this, parameter) }
    }

    override fun visitGroupByClause(groupByClause: GroupByClause, parameter: Unit): String {
        return groupByClause.expression.visit(this, parameter)
    }

    override fun visitGroupByExpression(groupByExpression: GroupByExpression, parameter: Unit): String {
        val selections = groupByExpression.selections.joinToString(", ") { it.visit(this, parameter) }
        val expressions = groupByExpression.expressions.joinToString(", ") { it.visit(this, parameter) }
        val having = groupByExpression.having?.let { ", ${it.visit(this, parameter)}" } ?: ""
        return "[$expressions], [$selections]$having"
    }

    override fun visitDistinctClause(distinctClause: DistinctClause, parameter: Unit): String {
        return distinctClause.expressions.joinToString(", ") { it.visit(this, parameter) }
    }

    override fun visitOrderByClause(orderByClause: OrderByClause, parameter: Unit): String {
        return orderByClause.ordering.joinToString(", ") { it.visit(this, parameter) }
    }

    override fun visitLimitClause(limitClause: LimitClause, parameter: Unit): String {
        return limitClause.value.visit(this, parameter)
    }

    override fun visitJoinExpression(joinExpression: JoinExpression, parameter: Unit): String {
        return joinExpression.on.visit(this, parameter)
    }

    override fun visitJoinClause(joinClause: JoinClause, parameter: Unit): String {
        val from = joinClause.fromClause.visit(this, parameter)
        val type = joinClause.joinType.visit(this, parameter)
        val on = joinClause.onClause.visit(this, parameter)
        return "$from, $type, $on"
    }

    override fun visitInnerJoinType(innerJoinType: InnerJoinType, parameter: Unit): String {
        return "INNER"
    }

    override fun visitLeftJoinType(leftJoinType: LeftJoinType, parameter: Unit): String {
        return "LEFT_OUTER"
    }

    override fun visitColumnReferenceExpression(columnReferenceExpression: ColumnReferenceExpression, parameter: Unit): String {
        return columnReferenceExpression.name
    }

    override fun visitIfExpression(ifExpression: IfExpression, parameter: Unit): String {
        val test = ifExpression.test.visit(this, parameter)
        val body = ifExpression.body.visit(this, parameter)
        val orelse = ifExpression.orelse.visit(this, parameter)
        return "if($test, $body, $orelse)"
    }

    override fun visitOrderByExpression(orderByExpression: OrderByExpression, parameter: Unit): String {
        val direction = orderByExpression.direction.visit(this, parameter)
        val expression = orderByExpression.expression.visit(this, parameter)
        return "$expression $direction"
    }

    override fun visitAscendingOrderType(ascendingOrderType: AscendingOrderType, parameter: Unit): String {
        return "asc"
    }

    override fun visitDescendingOrderType(descendingOrderType: DescendingOrderType, parameter: Unit): String {
        return "desc"
    }

    override fun visitRenameClause(renameClause: RenameClause, parameter: Unit): String {
        return renameClause.columnAliases.joinToString(", ") { it.visit(this, parameter) }
    }

    override fun visitOffsetClause(offsetClause: OffsetClause, parameter: Unit): String {
        return offsetClause.value.visit(this, parameter)
    }

    override fun visitUnaryExpression(unaryExpression: UnaryExpression, parameter: Unit): String {
        val operator = unaryExpression.operator.visit(this, parameter)
        val expression = unaryExpression.expression.visit(this, parameter)
        return "$operator($expression)"
    }

    override fun visitBinaryExpression(binaryExpression: BinaryExpression, parameter: Unit): String {
        val left = binaryExpression.left.visit(this, parameter)
        val right = binaryExpression.right.visit(this, parameter)
        val operator = binaryExpression.operator.visit(this, parameter)
        return "($left $operator $right)"
    }

    override fun visitInBinaryOperator(inBinaryOperator: InBinaryOperator, parameter: Unit): String {
        return "in"
    }

    override fun visitNotInBinaryOperator(notInBinaryOperator: NotInBinaryOperator, parameter: Unit): String {
        return "notIn"
    }

    override fun visitIsBinaryOperator(isBinaryOperator: IsBinaryOperator, parameter: Unit): String {
        return "is"
    }

    override fun visitIsNotBinaryOperator(isNotBinaryOperator: IsNotBinaryOperator, parameter: Unit): String {
        return "isNot"
    }

    override fun visitBitwiseAndBinaryOperator(bitwiseAndBinaryOperator: BitwiseAndBinaryOperator, parameter: Unit): String {
        return "&"
    }

    override fun visitBitwiseOrBinaryOperator(bitwiseOrBinaryOperator: BitwiseOrBinaryOperator, parameter: Unit): String {
        return "|"
    }
    
    override fun visitLikeBinaryOperator(likeBinaryOperator: LikeBinaryOperator, parameter: Unit): String {
        return "like"
    }
    
    override fun visitIsNullBinaryOperator(isNullBinaryOperator: IsNullBinaryOperator, parameter: Unit): String {
        return "is null"
    }
    
    override fun visitIsNotNullBinaryOperator(isNotNullBinaryOperator: IsNotNullBinaryOperator, parameter: Unit): String {
        return "is not null"
    }
    
    override fun visitNullExpression(nullExpression: NullExpression, parameter: Unit): String {
        return "null"
    }
}
