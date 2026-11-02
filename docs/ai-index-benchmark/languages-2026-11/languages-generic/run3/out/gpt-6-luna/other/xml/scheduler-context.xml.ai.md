### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:39:13Z
- G: 
- A: 0.0.0
- X: file
---
> Sends reminders for overdue items on weekday mornings using configured database and SMTP services.

#### Purpose
- Wires the reminder scheduler’s data source, repository, mail gateway, and overdue-reminder job.
- Loads reminder properties and scans the reminder web package for Spring components.

#### Type
- Spring XML application-context configuration.

#### Input
- `classpath:reminder.properties`; reads `reminder.db.url`, `reminder.db.user`, and `reminder.smtp.host`.
- Configures database and SMTP services; component-scans `org.example.reminder.web`.

#### Output
- Defines `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, and `reminderScheduler` beans.
- Schedules `OverdueReminderJob.sendDueReminders` to run on weekdays at 07:00; the job may send email through `mailGateway`.

#### Core logic
- Creates a HikariCP data source using the configured JDBC URL and username; closes it on bean destruction.
- Injects the data source into `JdbcReminderRepository`.
- Configures `SmtpMailGateway` with the configured host and port `587`.
- Constructs `OverdueReminderJob` with the repository and mail gateway; sets its grace period to 14 days.
- Runs the job on cron schedule `0 0 7 * * MON-FRI` using a scheduler with two threads.

#### Public API
- `OverdueReminderJob.sendDueReminders()` → scheduled reminder dispatch; configured as the scheduled task entry point.

#### Constants and configuration
- `reminder.db.url` — JDBC URL property.
- `reminder.db.user` — database username property.
- `reminder.smtp.host` — SMTP host property.
- `maximumPoolSize` — `8`.
- `port` — `587`.
- `graceDays` — `14`.
- `pool-size` — `2`.
- `cron` — `0 0 7 * * MON-FRI`.
- `location` — `classpath:reminder.properties`.
- `base-package` — `org.example.reminder.web`.

#### Dependencies
- Spring beans, context, and task XML namespaces.
- `com.zaxxer.hikari.HikariDataSource`.
- `org.example.reminder.JdbcReminderRepository`.
- `org.example.reminder.SmtpMailGateway`.
- `org.example.reminder.OverdueReminderJob`.

#### Exceptions / Errors
- No explicit exception handling or error conditions are configured.
