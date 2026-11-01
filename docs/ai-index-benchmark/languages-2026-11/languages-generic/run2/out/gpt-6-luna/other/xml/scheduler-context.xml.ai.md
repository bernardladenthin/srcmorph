### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:33:07Z
- G: 
- A: 0.0.0
- X: file
---
> Sends reminders for overdue items on weekday mornings using a database repository and SMTP mail gateway.

#### Purpose
- Wires the reminder application’s database, repository, mail gateway, overdue-reminder job, and scheduled execution.
- Loads reminder settings from `reminder.properties` and scans `org.example.reminder.web` for components.

#### Type
- Spring Framework XML application-context configuration.

#### Input
- Classpath resource `reminder.properties`.
- Properties `reminder.db.url`, `reminder.db.user`, and `reminder.smtp.host`.

#### Output
- Defines Spring beans `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and `reminderScheduler`.
- Schedules `OverdueReminderJob.sendDueReminders` to run on weekdays at 07:00; the job is wired to access the database and send mail.

#### Core logic
- Resolves configuration properties from `classpath:reminder.properties`.
- Scans `org.example.reminder.web` for Spring components.
- Creates a Hikari data source and injects it into `JdbcReminderRepository`.
- Configures `SmtpMailGateway` and injects it with the repository into `OverdueReminderJob`.
- Runs `sendDueReminders` on the configured scheduler using cron `0 0 7 * * MON-FRI`.

#### Public API
- `reminderDataSource` — Hikari data source bean.
- `reminderRepository` — JDBC reminder repository bean.
- `mailGateway` — SMTP mail gateway bean.
- `overdueReminderJob` — overdue reminder job bean.
- `reminderScheduler` — task scheduler bean.
- `sendDueReminders()` — scheduled job method; sends due reminders.

#### Constants and configuration
- `reminder.db.url` — data source JDBC URL property.
- `reminder.db.user` — data source username property.
- `reminder.smtp.host` — SMTP host property.
- `maximumPoolSize` — `8`.
- `port` — `587`.
- `graceDays` — `14`.
- `pool-size` — `2`.
- `cron` — `0 0 7 * * MON-FRI`.
- `destroy-method` — `close`.

#### Dependencies
- Spring Beans, Context, and Task XML namespaces and schemas.
- `com.zaxxer.hikari.HikariDataSource`.
- `org.example.reminder.JdbcReminderRepository`.
- `org.example.reminder.SmtpMailGateway`.
- `org.example.reminder.OverdueReminderJob`.
- `reminder.properties`.

#### Exceptions / Errors
- No exception handling or explicit error conditions are defined.
