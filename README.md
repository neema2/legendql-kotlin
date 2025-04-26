# LegendQL Kotlin

A Kotlin port of [LegendQL](https://github.com/hausea/legendql), a metamodel that represents a Query to be executed in the context of a Runtime, thereby creating a DataFrame.

## Overview

This project is a Kotlin implementation of LegendQL, maintaining a similar structure and approach to the original Python implementation. The DSL syntax is designed to be similar to KTORM, while the metamodel and pure relation translation are kept close to the Python implementation.

## Project Structure

- `model` - Contains the metamodel classes for representing SQL concepts
- `query` - Implements the query representation with methods for adding clauses
- `dsl` - Provides the main DSL interface with KTORM-like syntax
- `parser` - Handles parsing DSL expressions into metamodel objects
- `dialect` - Implements the pure relation translation

## Usage Examples

Here's a simple example of using the LegendQL Kotlin DSL:

```kotlin
// Create tables
val employeeColumns = mapOf(
    "id" to Int::class,
    "name" to String::class,
    "age" to Int::class,
    "salary" to Double::class,
    "department_id" to Int::class
)

val employees = LegendQL.table("company", "employees", employeeColumns)

// Build a query
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

// Execute the query
val runtime = NonExecutablePureRuntime()
val result = query.bind(runtime)
println(result.executableToString())
```

## Building and Testing

This project uses Gradle for building and testing. To build the project, run:

```bash
./gradlew build
```

To run the tests, run:

```bash
./gradlew test
```

## License

This project is licensed under the same license as the original LegendQL project.
