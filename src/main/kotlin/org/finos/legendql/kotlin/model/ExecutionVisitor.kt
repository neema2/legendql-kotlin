package org.finos.legendql.kotlin.model

/**
 * Interface for execution visitors in the metamodel
 */
interface ExecutionVisitor<P, R> {
    // Runtime
    fun visitRuntime(runtime: Runtime, parameter: P): R
    
    // Clauses
    fun visitFromClause(fromClause: FromClause, parameter: P): R
    fun visitFilterClause(filterClause: FilterClause, parameter: P): R
    fun visitSelectionClause(selectionClause: SelectionClause, parameter: P): R
    fun visitExtendClause(extendClause: ExtendClause, parameter: P): R
    fun visitGroupByClause(groupByClause: GroupByClause, parameter: P): R
    fun visitDistinctClause(distinctClause: DistinctClause, parameter: P): R
    fun visitOrderByClause(orderByClause: OrderByClause, parameter: P): R
    fun visitLimitClause(limitClause: LimitClause, parameter: P): R
    fun visitJoinClause(joinClause: JoinClause, parameter: P): R
    fun visitRenameClause(renameClause: RenameClause, parameter: P): R
    fun visitOffsetClause(offsetClause: OffsetClause, parameter: P): R
    
    // Literals
    fun visitIntegerLiteral(integerLiteral: IntegerLiteral, parameter: P): R
    fun visitLongLiteral(longLiteral: LongLiteral, parameter: P): R
    fun visitDoubleLiteral(doubleLiteral: DoubleLiteral, parameter: P): R
    fun visitStringLiteral(stringLiteral: StringLiteral, parameter: P): R
    fun visitDateLiteral(dateLiteral: DateLiteral, parameter: P): R
    fun visitBooleanLiteral(booleanLiteral: BooleanLiteral, parameter: P): R
    
    // Functions
    fun visitCountFunction(countFunction: CountFunction, parameter: P): R
    fun visitAverageFunction(averageFunction: AverageFunction, parameter: P): R
    fun visitModuloFunction(moduloFunction: ModuloFunction, parameter: P): R
    fun visitExponentFunction(exponentFunction: ExponentFunction, parameter: P): R
    fun visitSumFunction(sumFunction: SumFunction, parameter: P): R
    fun visitMinFunction(minFunction: MinFunction, parameter: P): R
    fun visitMaxFunction(maxFunction: MaxFunction, parameter: P): R
    
    // Unary Operators
    fun visitNotUnaryOperator(notUnaryOperator: NotUnaryOperator, parameter: P): R
    
    // Binary Operators
    fun visitEqualsBinaryOperator(equalsBinaryOperator: EqualsBinaryOperator, parameter: P): R
    fun visitNotEqualsBinaryOperator(notEqualsBinaryOperator: NotEqualsBinaryOperator, parameter: P): R
    fun visitGreaterThanBinaryOperator(greaterThanBinaryOperator: GreaterThanBinaryOperator, parameter: P): R
    fun visitGreaterThanEqualsBinaryOperator(greaterThanEqualsBinaryOperator: GreaterThanEqualsBinaryOperator, parameter: P): R
    fun visitLessThanBinaryOperator(lessThanBinaryOperator: LessThanBinaryOperator, parameter: P): R
    fun visitLessThanEqualsBinaryOperator(lessThanEqualsBinaryOperator: LessThanEqualsBinaryOperator, parameter: P): R
    fun visitInBinaryOperator(inBinaryOperator: InBinaryOperator, parameter: P): R
    fun visitNotInBinaryOperator(notInBinaryOperator: NotInBinaryOperator, parameter: P): R
    fun visitIsBinaryOperator(isBinaryOperator: IsBinaryOperator, parameter: P): R
    fun visitIsNotBinaryOperator(isNotBinaryOperator: IsNotBinaryOperator, parameter: P): R
    fun visitAndBinaryOperator(andBinaryOperator: AndBinaryOperator, parameter: P): R
    fun visitOrBinaryOperator(orBinaryOperator: OrBinaryOperator, parameter: P): R
    fun visitAddBinaryOperator(addBinaryOperator: AddBinaryOperator, parameter: P): R
    fun visitMultiplyBinaryOperator(multiplyBinaryOperator: MultiplyBinaryOperator, parameter: P): R
    fun visitSubtractBinaryOperator(subtractBinaryOperator: SubtractBinaryOperator, parameter: P): R
    fun visitDivideBinaryOperator(divideBinaryOperator: DivideBinaryOperator, parameter: P): R
    fun visitBitwiseAndBinaryOperator(bitwiseAndBinaryOperator: BitwiseAndBinaryOperator, parameter: P): R
    fun visitBitwiseOrBinaryOperator(bitwiseOrBinaryOperator: BitwiseOrBinaryOperator, parameter: P): R
    fun visitLikeBinaryOperator(likeBinaryOperator: LikeBinaryOperator, parameter: P): R
    fun visitIsNullBinaryOperator(isNullBinaryOperator: IsNullBinaryOperator, parameter: P): R
    fun visitIsNotNullBinaryOperator(isNotNullBinaryOperator: IsNotNullBinaryOperator, parameter: P): R
    
    // Join Types
    fun visitInnerJoinType(innerJoinType: InnerJoinType, parameter: P): R
    fun visitLeftJoinType(leftJoinType: LeftJoinType, parameter: P): R
    
    // Order Types
    fun visitAscendingOrderType(ascendingOrderType: AscendingOrderType, parameter: P): R
    fun visitDescendingOrderType(descendingOrderType: DescendingOrderType, parameter: P): R
    
    // Expressions
    fun visitOperandExpression(operandExpression: OperandExpression, parameter: P): R
    fun visitUnaryExpression(unaryExpression: UnaryExpression, parameter: P): R
    fun visitBinaryExpression(binaryExpression: BinaryExpression, parameter: P): R
    fun visitLiteralExpression(literalExpression: LiteralExpression, parameter: P): R
    fun visitVariableAliasExpression(variableAliasExpression: VariableAliasExpression, parameter: P): R
    fun visitColumnReferenceExpression(columnReferenceExpression: ColumnReferenceExpression, parameter: P): R
    fun visitColumnAliasExpression(columnAliasExpression: ColumnAliasExpression, parameter: P): R
    fun visitComputedColumnAliasExpression(computedColumnAliasExpression: ComputedColumnAliasExpression, parameter: P): R
    fun visitIfExpression(ifExpression: IfExpression, parameter: P): R
    fun visitOrderByExpression(orderByExpression: OrderByExpression, parameter: P): R
    fun visitFunctionExpression(functionExpression: FunctionExpression, parameter: P): R
    fun visitMapReduceExpression(mapReduceExpression: MapReduceExpression, parameter: P): R
    fun visitLambdaExpression(lambdaExpression: LambdaExpression, parameter: P): R
    fun visitGroupByExpression(groupByExpression: GroupByExpression, parameter: P): R
    fun visitJoinExpression(joinExpression: JoinExpression, parameter: P): R
    fun visitNullExpression(nullExpression: NullExpression, parameter: P): R
    fun visitAggregateExpression(aggregateExpression: AggregateExpression, parameter: P): R
}
