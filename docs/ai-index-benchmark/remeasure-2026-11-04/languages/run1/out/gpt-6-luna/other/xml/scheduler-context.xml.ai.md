### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:58:28Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Configures a weekday morning reminder job backed by a pooled database connection and SMTP mail gateway.

#### Purpose
- Wires the reminder repository, mail gateway, overdue reminder job, and scheduled execution.
- Loads reminder settings from `reminder.properties` and scans the reminder web package.

#### Type
- Spring application context; root `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource` — `com.zaxxer.hikari.HikariDataSource`; `destroy-method="close"`.
  - `reminderRepository` — `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
  - `mailGateway` — `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob` — `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
- Scheduler `reminderScheduler`.
- Scheduled task: `overdueReminderJob.sendDueReminders`, using `reminderScheduler`.

#### Configuration
- Property file: `classpath:reminder.properties`.
- Component scan: `org.example.reminder.web`.
- `reminderDataSource`: `jdbcUrl=${reminder.db.url}`, `username=${reminder.db.user}`, `maximumPoolSize=8`.
- `mailGateway`: `host=${reminder.smtp.host}`, `port=587`.
- `overdueReminderJob`: `graceDays=14`.
- `reminderScheduler`: `pool-size=2`.
- Schedule: `cron="0 0 7 * * MON-FRI"`.

#### Dependencies
- Referenced property file: `reminder.properties`.
- Property placeholders: `${reminder.db.url}`, `${reminder.db.user}`, `${reminder.smtp.host}`.
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
