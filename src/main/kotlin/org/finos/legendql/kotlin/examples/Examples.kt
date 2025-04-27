package org.finos.legendql.kotlin.examples

import org.finos.legendql.kotlin.model.*
import org.finos.legendql.kotlin.dsl.*
import org.finos.legendql.kotlin.dialect.purerelation.NonExecutablePureRuntime
import kotlin.reflect.KClass

/**
 * Define tables using KTORM-like syntax
 */
object Departments : Table<Nothing>("departments", "company") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val location = varchar("location")
}

/**
 * Define employee table with reference to departments
 */
object Employees : Table<Nothing>("employees", "company") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val age = int("age")
    val salary = double("salary")
    val departmentId = int("department_id").references(Departments)
}

/**
 * Examples of using the LegendQL Kotlin DSL
 */
object Examples {
    /**
     * Connect to the database
     */
    private fun createDatabase(): Database {
        return Database.connect(
            url = "jdbc:mysql://localhost:3306/legendql", 
            user = "root", 
            password = "password",
            name = "company"
        )
    }
    
    /**
     * Example of a simple select query
     */
    fun simpleSelect() {
        val database = createDatabase()
        
        // Select specific columns
        val query = database
            .from(Employees)
            .select(Employees.id, Employees.name, Employees.age)
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Simple Select Query:")
        println(result.executableToString())
        println()
        
        // In a real implementation, this would print actual data
        query.forEach { row ->
            println("${row[Employees.id]}: ${row[Employees.name]} (${row[Employees.age]})")
        }
    }
    
    /**
     * Example of filtering data
     */
    fun filtering() {
        val database = createDatabase()
        
        // Filter by age using operator overloading (>)
        val query = database
            .from(Employees)
            .where { Employees.age > 30 }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Filter Query:")
        println(result.executableToString())
        println()
        
        // In a real implementation, this would print actual data
        query.forEach { row ->
            println("${row[Employees.name]} (${row[Employees.age]})")
        }
    }
    
    /**
     * Example of complex filtering with multiple conditions
     */
    fun complexFiltering() {
        val database = createDatabase()
        
        // Filter by department ID and name pattern using operator overloading (==, &&)
        val query = database
            .from(Employees)
            .where { (Employees.departmentId == 1) && (Employees.name like "%vince%") }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Complex Filter Query:")
        println(result.executableToString())
        println()
        
        // In a real implementation, this would print actual data
        query.forEach { row ->
            println("${row[Employees.name]} (Dept: ${row[Employees.departmentId]})")
        }
    }
    
    /**
     * Example of extending with computed columns
     */
    fun extending() {
        val database = createDatabase()
        
        // Extend with computed columns using KTORM-style operators
        val query = database
            .from(Employees)
            .select(Employees.name, Employees.salary)
            .extend(
                listOf(
                    (Employees.salary + 1000).aliased("bonus")
                )
            )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Extend Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of ordering data
     */
    fun ordering() {
        val database = createDatabase()
        
        // Order by salary descending and name ascending
        val query = database
            .from(Employees)
            .orderBy(Employees.salary.desc(), Employees.name.asc())
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Order By Query:")
        println(result.executableToString())
        println()
        
        // In a real implementation, this would print actual data
        query.forEach { row ->
            println("${row[Employees.name]}: ${row[Employees.salary]}")
        }
    }
    
    /**
     * Example of grouping data
     */
    fun grouping() {
        val database = createDatabase()
        
        // Group by department and calculate average salary
        val query = database
            .from(Employees)
            .select(Employees.departmentId, avg(Employees.salary).aliased("avg_salary"))
            .groupBy(Employees.departmentId)
            .having { avg(Employees.salary) > 1000.0 }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Group By Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of joining tables
     */
    fun joining() {
        val database = createDatabase()
        
        // Join employees and departments
        val query = database
            .from(Employees)
            .innerJoin(Departments) { Employees.departmentId == Departments.id }
            .select(Employees.name, Departments.name)
        
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
        val database = createDatabase()
        
        // Limit to 10 rows and skip first 5
        val query = database
            .from(Employees)
            .limit(5, 10) // offset 5, limit 10
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Limit and Offset Query:")
        println(result.executableToString())
        println()
        
        // In a real implementation, this would print actual data
        query.forEach { row ->
            println("${row[Employees.name]}")
        }
    }
    
    /**
     * Example of complex query with multiple operations
     */
    fun complexQuery() {
        val database = createDatabase()
        
        // Complex query with multiple operations
        val query = database
            .from(Employees)
            .select(Employees.name, Employees.salary)
            .where { Employees.age > 30 }
            .extend(
                listOf(
                    (Employees.salary + 1000).aliased("bonus")
                )
            )
            .orderBy(Employees.salary.desc())
            .limit(10)
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Complex Query:")
        println(result.executableToString())
        println()
    }
    
    /**
     * Example of arithmetic operators in KTORM-style syntax
     */
    fun arithmeticOperators() {
        val database = createDatabase()
        
        // Query using arithmetic operators
        val query = database
            .from(Employees)
            .select(
                Employees.name,
                Employees.salary,
                (Employees.salary + 1000).aliased("salary_plus"),
                (Employees.salary - 500).aliased("salary_minus"),
                (Employees.salary * 2).aliased("salary_times"),
                (Employees.salary / 2).aliased("salary_divided")
            )
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Arithmetic Operators Query:")
        println(result.executableToString())
        println()
        
        // In a real implementation, this would print actual data
        query.forEach { row ->
            println("${row[Employees.name]}: ${row[Employees.salary]}")
            println("  + 1000 = ${row["salary_plus"]}")
            println("  - 500 = ${row["salary_minus"]}")
            println("  * 2 = ${row["salary_times"]}")
            println("  / 2 = ${row["salary_divided"]}")
        }
    }
    
    /**
     * Example of comparison operators with Kotlin operator overloading
     */
    fun comparisonOperators() {
        val database = createDatabase()
        
        // Query using comparison operators with Kotlin operator overloading
        val query = database
            .from(Employees)
            .where { 
                (Employees.salary > 1000.0) && 
                (Employees.age < 50) && 
                (Employees.name == "John") || 
                (Employees.departmentId == 1)
            }
        
        val runtime = NonExecutablePureRuntime()
        val result = query.bind(runtime)
        
        println("Comparison Operators Query:")
        println(result.executableToString())
        println()
        
        // In a real implementation, this would print actual data
        query.forEach { row ->
            println("${row[Employees.name]}: ${row[Employees.salary]}")
        }
    }
    
    /**
     * Run all examples
     */
    @JvmStatic
    fun main(args: Array<String>) {
        println("Running LegendQL Kotlin Examples with KTORM-like syntax")
        println("======================================================")
        println()
        
        simpleSelect()
        filtering()
        complexFiltering()
        extending()
        ordering()
        grouping()
        joining()
        limitingAndOffsetting()
        complexQuery()
        arithmeticOperators()
        comparisonOperators()
        
        println("All examples completed successfully")
    }
}
