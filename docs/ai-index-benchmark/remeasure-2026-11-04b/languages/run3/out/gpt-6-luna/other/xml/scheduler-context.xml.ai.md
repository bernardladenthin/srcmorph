### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:20:19Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Configures weekday morning delivery of overdue reminders using a JDBC repository, SMTP gateway, and Spring-managed scheduler.

#### Purpose
- Wires the reminder database, repository, mail gateway, and overdue-reminder job.
- Schedules `sendDueReminders` to run on weekdays.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource` — `com.zaxxer.hikari.HikariDataSource`; `destroy-method="close"`.
  - `reminderRepository` — `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
  - `mailGateway` — `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob` — `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
- Component scan: `org.example.reminder.web`.
- Property placeholder source: `classpath:reminder.properties`.
- Scheduler: `reminderScheduler`; scheduled method `overdueReminderJob.sendDueReminders`.

#### Configuration
- `reminderDataSource.jdbcUrl`: `${reminder.db.url}`.
- `reminderDataSource.username`: `${reminder.db.user}`.
- `reminderDataSource.maximumPoolSize`: `8`.
- `mailGateway.host`: `${reminder.smtp.host}`.
- `mailGateway.port`: `587`.
- `overdueReminderJob.graceDays`: `14`.
- `reminderScheduler.pool-size`: `2`.
- `overdueReminderJob.sendDueReminders` cron: `0 0 7 * * MON-FRI`.

#### Dependencies
- Property file: `classpath:reminder.properties`.
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
- Placeholder keys: `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host`.
