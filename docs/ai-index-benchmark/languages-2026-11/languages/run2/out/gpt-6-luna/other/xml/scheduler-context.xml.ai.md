### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:33:27Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the reminder service’s database access, SMTP delivery, overdue-reminder job, and weekday schedule.

#### Purpose
- Wires the reminder scheduler and its supporting data source, repository, and mail gateway.
- Loads reminder settings from `reminder.properties` and scans the reminder web package.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource` — `com.zaxxer.hikari.HikariDataSource`; closes via `destroy-method="close"`.
  - `reminderRepository` — `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
  - `mailGateway` — `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob` — `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
  - `reminderScheduler` — task scheduler.
- Scheduled task: `overdueReminderJob.sendDueReminders`, scheduled with cron `0 0 7 * * MON-FRI`.
- Property placeholder location: `classpath:reminder.properties`.
- Component scan: `org.example.reminder.web`.

#### Configuration
- `reminderDataSource.jdbcUrl`: `${reminder.db.url}`.
- `reminderDataSource.username`: `${reminder.db.user}`.
- `reminderDataSource.maximumPoolSize`: `8`.
- `mailGateway.host`: `${reminder.smtp.host}`.
- `mailGateway.port`: `587`.
- `overdueReminderJob.graceDays`: `14`.
- `reminderScheduler.pool-size`: `2`.
- Scheduled task cron: `0 0 7 * * MON-FRI`.

#### Dependencies
- Property file: `classpath:reminder.properties`.
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
- Required properties: `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host`.
