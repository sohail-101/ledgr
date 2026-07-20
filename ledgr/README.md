## Running it locally

1. Install Postgres and create a database:

```sql
CREATE DATABASE ledgr_db;
```

2. Update `src/main/resources/application.properties` with your db username/password if they're different from the defaults.

3. Run it (needs Maven installed, or just hit run on `LedgrApplication.java` in IntelliJ):

```bash
mvn spring-boot:run
```

4. Go to `http://localhost:8080`

Tables get created automatically on first run (`ddl-auto=update`), no migration scripts needed for now.


