### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:28:28Z
- G: 
- A: 0.0.0
- X: file
---
> Sends weekday morning reminders for overdue items using configured database and SMTP services.

#### Purpose
- Wires the reminder application’s data source, repository, mail gateway, job, and scheduler.
- Loads reminder settings and scans web components.

#### Type
- Spring XML bean-configuration file.

#### Input
- `classpath:reminder.properties`, providing `reminder.db.url`, `reminder.db.user`, and `reminder.smtp.host`.
- Spring component scan for `org.example.reminder.web`.

#### Output
- Defines `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and `reminderScheduler` beans.
- Schedules `OverdueReminderJob.sendDueReminders` on weekdays.

#### Core logic
- Loads properties from `reminder.properties` and scans the reminder web package.
- Configures a Hikari data source, then injects it into `JdbcReminderRepository`.
- Configures `SmtpMailGateway` with the SMTP host property and port `587`.
- Constructs `OverdueReminderJob` with the repository and mail gateway; sets `graceDays` to `14`.
- Runs `sendDueReminders` at `07:00` Monday through Friday using a scheduler with pool size `2`.

#### Public API
- `reminderDataSource` — Hikari data source bean; closes via `close`.
- `reminderRepository` — JDBC reminder repository bean.
- `mailGateway` — SMTP mail gateway bean.
- `overdueReminderJob` — overdue reminder job bean.
- `reminderScheduler` — task scheduler bean.
- Scheduled method: `sendDueReminders()` — sends due reminders on the configured cron schedule.

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
- Spring property placeholder, component scan, scheduler, and scheduled-task elements.

#### Exceptions / Errors
- No exception handling or error conditions are specified in this configuration.
