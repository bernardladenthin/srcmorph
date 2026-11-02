### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:31:11Z
- G: 
- A: 0.0.0
- X: file
---
> Configures Spring beans for a reminder scheduler, including data source, repository, mail gateway, job, and cron trigger.

#### Purpose
- Wire Spring components for reminder scheduling.
- Define data source, repository, mail gateway, job, and cron trigger.

#### Type
- XML configuration file (Spring beans).

#### Input
- `reminder.properties` (property placeholder).
- Classpath resources: `org.example.reminder.web` package for component scanning.
- External properties: `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host`.

#### Output
- Beans:
  - `reminderDataSource` (`HikariDataSource`).
  - `reminderRepository` (`JdbcReminderRepository`).
  - `mailGateway` (`SmtpMailGateway`).
  - `overdueReminderJob` (`OverdueReminderJob`).
  - `reminderScheduler` (`TaskScheduler`).
- Scheduled task invoking `overdueReminderJob.sendDueReminders` at 7 AM Mon‑Fri.

#### Core logic
- Load properties from `reminder.properties`.
- Scan `org.example.reminder.web` for components.
- Configure `HikariDataSource` with JDBC URL, username, pool size.
- Instantiate `JdbcReminderRepository` with data source.
- Configure `SmtpMailGateway` with host and port.
- Create `OverdueReminderJob` with repository, mail gateway, grace days.
- Set up a `TaskScheduler` with pool size 2.
- Schedule `sendDueReminders` method via cron expression.

#### Public API
- None (configuration only).

#### Constants and configuration
- `reminder.db.url` – JDBC URL.
- `reminder.db.user` – DB username.
- `reminder.smtp.host` – SMTP host.
- `reminder.smtp.port` – 587.
- `overdueReminderJob.graceDays` – 14.
- Scheduler pool size – 2.
- Cron – `0 0 7 * * MON-FRI`.

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource`.
- `org.example.reminder.JdbcReminderRepository`.
- `org.example.reminder.SmtpMailGateway`.
- `org.example.reminder.OverdueReminderJob`.
- Spring `TaskScheduler`, `ScheduledTasks`, `PropertyPlaceholderConfigurer`.

#### Exceptions / Errors
- Spring bean creation failures if properties missing or classes not found.
