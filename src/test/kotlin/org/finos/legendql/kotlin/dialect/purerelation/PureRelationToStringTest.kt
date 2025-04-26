package org.finos.legendql.kotlin.dialect.purerelation

import org.finos.legendql.kotlin.model.*
import org.finos.legendql.kotlin.query.Query
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.reflect.KClass

/**
 * Tests for the pure relation dialect translation
 */
class PureRelationToStringTest {

    /**
     * Create a test database and table
     */
    private fun createTestTable(): Pair<Database, Table> {
        val columns = mutableMapOf<String, KClass<*>>(
            "id" to Int::class,
            "name" to String::class,
            "age" to Int::class,
            "salary" to Double::class,
            "department_id" to Int::class
        )
        
        val table = Table("employees", columns)
        val database = Database("test", listOf(table))
        
        return Pair(database, table)
    }

    /**
     * Test a simple select query
     */
    @Test
    fun testSimpleSelect() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.select("id", "name", "age")
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->project([id, name, age])"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test a filter query
     */
    @Test
    fun testFilter() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.filter(
            BinaryExpression(
                OperandExpression(ColumnReferenceExpression("age")),
                OperandExpression(LiteralExpression(IntegerLiteral(30))),
                GreaterThanBinaryOperator()
            )
        )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->filter((age > 30))"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test an extend query
     */
    @Test
    fun testExtend() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.extend(
            listOf(
                ComputedColumnAliasExpression(
                    "bonus",
                    BinaryExpression(
                        OperandExpression(ColumnReferenceExpression("salary")),
                        OperandExpression(LiteralExpression(IntegerLiteral(1000))),
                        AddBinaryOperator()
                    )
                )
            )
        )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->extend([(salary + 1000) as bonus])"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test a rename query
     */
    @Test
    fun testRename() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.rename(
            "id" to "employee_id",
            "name" to "employee_name"
        )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->rename([id as employee_id, name as employee_name])"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test a group by query
     */
    @Test
    fun testGroupBy() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.groupBy(
            listOf(
                ColumnReferenceExpression("department_id"),
                FunctionExpression(
                    AverageFunction(),
                    listOf(ColumnReferenceExpression("salary"))
                )
            ),
            listOf(ColumnReferenceExpression("department_id"))
        )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->groupBy([department_id], [department_id, avg(salary)])"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test an order by query
     */
    @Test
    fun testOrderBy() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.orderBy(
            OrderByExpression(DescendingOrderType(), ColumnReferenceExpression("salary")),
            OrderByExpression(AscendingOrderType(), ColumnReferenceExpression("name"))
        )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->sort([desc, asc])"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test a limit query
     */
    @Test
    fun testLimit() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.limit(10)
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->take(10)"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test an offset query
     */
    @Test
    fun testOffset() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.offset(5)
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->drop(5)"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test a join query
     */
    @Test
    fun testJoin() {
        val (database, _) = createTestTable()
        
        val departmentColumns = mutableMapOf<String, KClass<*>>(
            "id" to Int::class,
            "name" to String::class,
            "location" to String::class
        )
        
        val departmentTable = Table("departments", departmentColumns)
        val employeeTable = Table("employees", mutableMapOf(
            "id" to Int::class,
            "name" to String::class,
            "department_id" to Int::class
        ))
        
        val query = Query.fromTable(database, employeeTable)
        query.join(
            database.name,
            departmentTable.table,
            InnerJoinType(),
            BinaryExpression(
                OperandExpression(ColumnReferenceExpression("department_id")),
                OperandExpression(ColumnReferenceExpression("id")),
                EqualsBinaryOperator()
            )
        )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->join(test.departments, INNER, (department_id == id))"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test a complex query with multiple operations
     */
    @Test
    fun testComplexQuery() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.select("name", "salary")
        query.filter(
            BinaryExpression(
                OperandExpression(ColumnReferenceExpression("age")),
                OperandExpression(LiteralExpression(IntegerLiteral(30))),
                GreaterThanBinaryOperator()
            )
        )
        query.extend(
            listOf(
                ComputedColumnAliasExpression(
                    "bonus",
                    BinaryExpression(
                        OperandExpression(ColumnReferenceExpression("salary")),
                        OperandExpression(LiteralExpression(IntegerLiteral(1000))),
                        AddBinaryOperator()
                    )
                )
            )
        )
        query.orderBy(
            OrderByExpression(DescendingOrderType(), ColumnReferenceExpression("salary"))
        )
        query.limit(10)
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->project([name, salary])\n->filter((age > 30))\n->extend([(salary + 1000) as bonus])\n->sort([desc])\n->take(10)"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test a query with mathematical operations
     */
    @Test
    fun testMathematicalOperations() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.extend(
            listOf(
                ComputedColumnAliasExpression(
                    "calculation",
                    BinaryExpression(
                        OperandExpression(
                            BinaryExpression(
                                OperandExpression(ColumnReferenceExpression("salary")),
                                OperandExpression(LiteralExpression(IntegerLiteral(2))),
                                MultiplyBinaryOperator()
                            )
                        ),
                        OperandExpression(
                            BinaryExpression(
                                OperandExpression(ColumnReferenceExpression("age")),
                                OperandExpression(LiteralExpression(IntegerLiteral(10))),
                                DivideBinaryOperator()
                            )
                        ),
                        AddBinaryOperator()
                    )
                )
            )
        )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->extend([((salary * 2) + (age / 10)) as calculation])"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test a query with conditional expressions
     */
    @Test
    fun testConditionalExpressions() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.extend(
            listOf(
                ComputedColumnAliasExpression(
                    "status",
                    IfExpression(
                        BinaryExpression(
                            OperandExpression(ColumnReferenceExpression("age")),
                            OperandExpression(LiteralExpression(IntegerLiteral(30))),
                            GreaterThanBinaryOperator()
                        ),
                        LiteralExpression(StringLiteral("Senior")),
                        LiteralExpression(StringLiteral("Junior"))
                    )
                )
            )
        )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->extend([if((age > 30), 'Senior', 'Junior') as status])"
        assertEquals(expected, result.executableToString())
    }

    /**
     * Test a query with function expressions
     */
    @Test
    fun testFunctionExpressions() {
        val (database, table) = createTestTable()
        
        val query = Query.fromTable(database, table)
        query.extend(
            listOf(
                ComputedColumnAliasExpression(
                    "count",
                    FunctionExpression(
                        CountFunction(),
                        listOf(ColumnReferenceExpression("id"))
                    )
                ),
                ComputedColumnAliasExpression(
                    "avg_salary",
                    FunctionExpression(
                        AverageFunction(),
                        listOf(ColumnReferenceExpression("salary"))
                    )
                )
            )
        )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        val expected = "test.employees\n->extend([count(id) as count, avg(salary) as avg_salary])"
        assertEquals(expected, result.executableToString())
    }
}
