package org.finos.legendql.kotlin.examples

import org.finos.legendql.kotlin.model.*
import org.finos.legendql.kotlin.dsl.LegendQL
import org.finos.legendql.kotlin.dialect.purerelation.NonExecutablePureRuntime
import kotlin.reflect.KClass

/**
 * Examples of using the LegendQL Kotlin DSL
 */
object Examples {
    /**
     * Create employee and department tables
     */
    private fun createTables(): Pair<LegendQL, LegendQL> {
        // Employee table
        val employeeColumns = mapOf(
            "id" to Int::class,
            "name" to String::class,
            "age" to Int::class,
            "salary" to Double::class,
            "department_id" to Int::class
        )
        
        // Department table
        val departmentColumns = mapOf(
            "id" to Int::class,
            "name" to String::class,
            "location" to String::class
        )
        
        val employees = LegendQL.table("company", "employees", employeeColumns)
        val departments = LegendQL.table("company", "departments", departmentColumns)
        
        return Pair(employees, departments)
    }
    
    /**
     * Example of a simple select query
     */
    fun simpleSelect() {
        val (employees, _) = createTables()
        
        // Select all columns
        val query1 = employees.select { table ->
            listOf(table["id"], table["name"], table["age"])
        }
        
        val runtime = NonExecutablePureRuntime()
        val result = query1.bind(runtime)
        
        println("Simple Select Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of filtering data
     */
    fun filtering() {
        val (employees, _) = createTables()
        
        // Filter by age and salary
        val query = employees.filter { table ->
            BinaryExpression(
                OperandExpression(table["age"]),
                OperandExpression(LiteralExpression(IntegerLiteral(30))),
                GreaterThanBinaryOperator()
            )
        }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Filter Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of extending with computed columns
     */
    fun extending() {
        val (employees, _) = createTables()
        
        // Extend with computed columns
        val query = employees.extend { table ->
            listOf(
                ComputedColumnAliasExpression(
                    "bonus",
                    BinaryExpression(
                        OperandExpression(table["salary"]),
                        OperandExpression(LiteralExpression(IntegerLiteral(1000))),
                        AddBinaryOperator()
                    )
                )
            )
        }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Extend Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of renaming columns
     */
    fun renaming() {
        val (employees, _) = createTables()
        
        // Rename columns
        val query = employees.rename { table ->
            listOf(
                ColumnAliasExpression("employee_id", table["id"]),
                ColumnAliasExpression("employee_name", table["name"])
            )
        }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Rename Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of grouping data
     */
    fun grouping() {
        val (employees, _) = createTables()
        
        // Group by department_id and calculate average salary
        val query = employees.groupBy { table ->
            GroupByExpression(
                listOf(
                    table["department_id"],
                    FunctionExpression(
                        AverageFunction(),
                        listOf(table["salary"])
                    )
                ),
                listOf(table["department_id"])
            )
        }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Group By Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of ordering data
     */
    fun ordering() {
        val (employees, _) = createTables()
        
        // Order by salary descending and name ascending
        val query = employees.orderBy { table ->
            listOf(
                OrderByExpression(DescendingOrderType(), table["salary"]),
                OrderByExpression(AscendingOrderType(), table["name"])
            )
        }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Order By Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of joining tables
     */
    fun joining() {
        val (employees, departments) = createTables()
        
        // Join employees and departments
        val query = employees.join(departments) { emp, dept ->
            Pair(
                BinaryExpression(
                    OperandExpression(emp["department_id"]),
                    OperandExpression(dept["id"]),
                    EqualsBinaryOperator()
                ),
                listOf(
                    emp["name"],
                    dept["name"]
                )
            )
        }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Join Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of limiting and offsetting data
     */
    fun limitingAndOffsetting() {
        val (employees, _) = createTables()
        
        // Limit to 10 rows and skip first 5
        val query = employees.limit(10).offset(5)
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Limit and Offset Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of complex query with multiple operations
     */
    fun complexQuery() {
        val (employees, departments) = createTables()
        
        // Complex query with multiple operations
        val query = employees
            .select { table -> 
                listOf(table["name"], table["salary"]) 
            }
            .filter { table ->
                BinaryExpression(
                    OperandExpression(table["age"]),
                    OperandExpression(LiteralExpression(IntegerLiteral(30))),
                    GreaterThanBinaryOperator()
                )
            }
            .extend { table ->
                listOf(
                    ComputedColumnAliasExpression(
                        "bonus",
                        BinaryExpression(
                            OperandExpression(table["salary"]),
                            OperandExpression(LiteralExpression(IntegerLiteral(1000))),
                            AddBinaryOperator()
                        )
                    )
                )
            }
            .orderBy { table ->
                listOf(
                    OrderByExpression(DescendingOrderType(), table["salary"])
                )
            }
            .limit(10)
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Complex Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Run all examples
     */
    @JvmStatic
    fun main(args: Array<String>) {
        simpleSelect()
        filtering()
        extending()
        renaming()
        grouping()
        ordering()
        joining()
        limitingAndOffsetting()
        complexQuery()
    }
}
