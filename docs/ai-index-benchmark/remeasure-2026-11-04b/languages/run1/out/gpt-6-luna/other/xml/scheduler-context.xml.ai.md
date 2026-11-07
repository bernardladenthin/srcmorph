### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:13:27Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Configures weekday morning delivery of overdue reminders using a JDBC-backed repository and SMTP gateway.

#### Purpose
- Wires the reminder data source, repository, mail gateway, job, and scheduler.
- Loads reminder settings from `reminder.properties` and scans the reminder web package.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource`: `com.zaxxer.hikari.HikariDataSource`; closes via `destroy-method="close"`.
  - `reminderRepository`: `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
  - `mailGateway`: `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob`: `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
- Scheduled task: `overdueReminderJob.sendDueReminders`, scheduled with cron `0 0 7 * * MON-FRI` using `reminderScheduler`.
- Scheduler: `reminderScheduler`, pool size `2`.
- Component scan: `org.example.reminder.web`.

#### Configuration
- Property placeholder file: `classpath:reminder.properties`.
- `reminderDataSource`: `jdbcUrl=${reminder.db.url}`; `username=${reminder.db.user}`; `maximumPoolSize=8`.
- `mailGateway`: `host=${reminder.smtp.host}`; `port=587`.
- `overdueReminderJob`: `graceDays=14`.

#### Dependencies
- Referenced property keys: `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host`.
- Referenced file: `reminder.properties`.
- Referenced classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
