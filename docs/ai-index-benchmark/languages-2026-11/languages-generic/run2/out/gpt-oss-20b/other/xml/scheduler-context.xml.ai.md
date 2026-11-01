### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:34:52Z
- G: 
- A: 0.0.0
- X: file
---
> Configures a Spring scheduler to send overdue reminders via email.

#### Purpose
- Wire data source, repository, mail gateway, and reminder job.
- Schedule daily reminder job on weekdays at 7 AM.

#### Type
- XML configuration file (Spring beans).

#### Input
- `reminder.properties` (property placeholder).
- Environment variables or property file values: `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host`.

#### Output
- Spring beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- Scheduled task invoking `overdueReminderJob.sendDueReminders()`.

#### Core logic
- Define HikariDataSource with JDBC URL, username, pool size.
- Instantiate `JdbcReminderRepository` with data source.
- Configure `SmtpMailGateway` with host and port.
- Create `OverdueReminderJob` with repository, gateway, graceDays=14.
- Set up a task scheduler with pool size 2.
- Schedule `sendDueReminders` method to run every weekday at 7 AM.

#### Public API
- Beans exposed to Spring context:
  - `reminderDataSource` → HikariDataSource (database connection pool).
  - `reminderRepository` → JdbcReminderRepository (data access).
  - `mailGateway` → SmtpMailGateway (SMTP client).
  - `overdueReminderJob` → OverdueReminderJob (reminder logic).
  - `reminderScheduler` → TaskScheduler (thread pool for scheduled tasks).

#### Constants and configuration
- `maximumPoolSize` = 8
- `port` = 587
- `graceDays` = 14
- Scheduler pool-size = 2
- Cron expression: `0 0 7 * * MON-FRI`

#### Dependencies
- Spring beans schema, context, task.
- `com.zaxxer.hikari.HikariDataSource`.
- `org.example.reminder.JdbcReminderRepository`.
- `org.example.reminder.SmtpMailGateway`.
- `org.example.reminder.OverdueReminderJob`.

#### Exceptions / Errors
- No explicit exception handling in this file; relies on Spring framework for bean creation errors.
