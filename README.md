## Setting up
```
// jdbc connection
Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
// init database manager
DatabaseManager.initManager(conn, new MysqlGrammar());
```

## define bean and model
```
@TableName("users")
public class UserBean {
    @TableKey(type = KeyType.AUTO)
    Long id;
    String username;
    String password;
    Integer gender;
    String email;
    @TableColumn("created_at")
    Timestamp createdAt;
    @TableColumn("updated_at")
    Timestamp updatedAt;
    // ...getter and setter
}

public class UserModel extends BaseModel<UserBean> {
}
```

## model insert update delete query
```
UserBean bean = new UserBean();
bean.setId(31L);
bean.setUsername("name");
bean.setPassword("password");
bean.setEmail("email@gmail.com");
bean.setGender(1);
bean.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
bean.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
// .. attribute set

var userModel = new UserModel();

// insert
userModel.insert(bean);

// findById
var opt = userModel.findById(9);
if (opt.isPresent()) {
    var user = opt.get();
    // update data
    user.setUsername("update username");
    user.setEmail("update@gmail.com");
    // update
    userModel.update(user);
}

// delate
UserBean bean = new UserBean();
bean.setId(21L);
long count = userModel.delete(bean);

// query
var all = userModel.query(
    new Query()
        .select("users.*")
        // default with bean @TableName()
        //.table("users")
        .where(wrapper -> {
            wrapper.where("id", 20);
        })
);
```

## Query builder

```java
var query = new Query().table("users");
```

## Select Statements
```java
var query = new Query()
    .table("users")
    .select("name", "email as user_email")
    .selectRaw("count(*) as user_count, status")
```
