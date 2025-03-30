
## Retrieving All Rows From a Table
```java
var users = 
    new QueryAapter()
        .query( query -> query.table("users"))
        .get();

```

## Retrieving a Single Row / Column From a Table

```java
import java.util.Optional;

Optional<User> user =new QueryAapter()
    .query( query -> query.table("users"))
    .first();

```

## Aggregates
```java
Long count =new QueryAapter()
    .query( query -> query.table("users"))
    .conut();

```

## Select Statements
```java
var users = 
    new QueryAapter()
        .query( query -> 
            query
                .table("users")
                .select("name", "email as user_email")
                .selectRaw("count(*) as user_count, status")
        )
        .get();
```
