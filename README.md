# Ledgr

## Video Demo: https://youtu.be/xsaEwwCUlGc

## Description: 
A personal finance tracker I built because I was tired of opening a spreadsheet, forgetting to update it for two weeks, and then having no idea where my money went. Ledgr is the "just log it and see it on a dashboard" version of that spreadsheet — log income and expenses, set a monthly budget, and actually see where things stand instead of guessing.

Started as my CS50 final project, ended up being a proper full-stack build.

## What it actually does

- **Accounts** — register, log in, log out. Sessions are handled manually with `HttpSession`, no Spring Security in the mix. Passwords are hashed with jBCrypt, never stored in plain text.
- **Dashboard** — current balance, total income, total expenses, and how you're tracking against this month's budget, plus charts so it doesn't feel like a spreadsheet.
- **Transactions** — add, edit, delete. Search and filter by type (income/expense) and category (food, bills, shopping, transport, entertainment, healthcare, education, investment, salary, and a catch-all "others").
- **Budgets** — set a budget per month, see what's left and what percentage you've burned through, plus a history of past months.
- **Analytics** — income vs. expense trend over time, spending broken down by category, your biggest category, and your highest-spending month.
- **Profile** — update your name, change your password, or delete your account entirely.

That covers every page from the original spec. Nothing here is a stub — if it's listed above, it's wired up end to end (controller → service → repository → template).

## Stack

- **Backend:** Java 21, Spring Boot 3 (Spring MVC + Spring Data JPA), Hibernate
- **Database:** PostgreSQL
- **Frontend:** Thymeleaf, Bootstrap 5, Chart.js
- **Auth:** hand-rolled session auth (`HttpSession` + an interceptor), jBCrypt for hashing — deliberately no Spring Security, no JWT, no OAuth
- **Build:** Maven



## Running it locally

You'll need Java 21, Maven, and a local Postgres instance.

**1. Create the database**

```sql
CREATE DATABASE ledgr_db;
```

**2. Point it at your Postgres credentials**

Open `src/main/resources/application.properties` and update the username/password if yours aren't the defaults (`postgres` / `postgres`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ledgr_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

**3. Run it**

```bash
mvn spring-boot:run
```

Or just hit run on `LedgrApplication.java` if you're in IntelliJ.

**4. Open it**

```
http://localhost:8080
```

Tables are created automatically on first run (`spring.jpa.hibernate.ddl-auto=update`) — no migration scripts to run, no seed data needed. Register an account and you're in.

## Project structure

```
src/main/java/com/ledgr/
├── config/       # web config + the session auth interceptor
├── controller/   # one per page: auth, dashboard, transactions, budgets, analytics, profile
├── dto/          # form-backing objects (login, register, transaction, budget, etc.)
├── entity/       # User, Transaction, Budget, plus the Category/TransactionType enums
├── repository/   # Spring Data JPA repositories
└── service/      # business logic — this is where the actual analytics math lives

src/main/resources/
├── templates/    # Thymeleaf pages + a shared sidebar fragment
├── static/css/   # the one stylesheet
└── application.properties
```

## Features i would like to add in the future

The core app is done — everything in the feature list above works. If I get time before I move on to the next project, these are the ones I'd add:

- Dark mode
- CSV export of transactions
- A short "insight" line on the dashboard (something like *"Food spending is up 18% vs last month"*)

Sorting on the transactions table isn't on this list since search + filter already cover the main use case.

## Why I built it this way

This wasn't meant to be the fanciest stack I could find — it was meant to be one I could build end to end, understand every piece of, and actually finish. Server-rendered Thymeleaf instead of a separate React frontend, manual sessions instead of Spring Security, one Postgres database instead of microservices. Boring on purpose, so the parts that *are* interesting — the budget math, the analytics queries, the actual UX of tracking your own money — got the attention instead.
