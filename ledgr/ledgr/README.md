# Ledgr

Personal finance tracker built with Spring Boot for my CS50 final project. Lets you log income/expenses, set a monthly budget and see it all on one dashboard instead of a spreadsheet nobody updates.

## Stack

Java 21, Spring Boot 3 (MVC + Data JPA), PostgreSQL, Thymeleaf, Bootstrap 5, Chart.js. No Spring Security, sessions are handled manually with `HttpSession`, password hashing done with jBCrypt.

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

## What's done so far

- Landing page
- Register / login / logout (session based)
- Dashboard shell (balance, income, expense, budget progress, charts)
- Transaction CRUD (add, edit, delete, search + filter by type/category)
- Budgets (set/update per month, remaining + % spent, history table)
- Analytics (income vs expense trend, spending by category, largest category, highest spending month)
- Profile (change name, change password, delete account)

All the core pages from the spec are done. Left over as nice-to-haves if there's time before the deadline:

- Dark mode
- CSV export
- Sort transactions (search + type/category filter already work)
- Dashboard insight text ("Food spending increased 18% vs last month")
