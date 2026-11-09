### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:43:50Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire reminder scheduler components.
- Schedule overdue reminder job.

#### Type
- Spring context XML (`<beans>` root).  
- Namespaces: `xmlns` (beans), `xmlns:context`, `xmlns:task`.

#### Definitions
- **Property placeholder**: `reminder.properties`.
- **Component scan**: `org.example.reminder.web`.
- **Beans**:
  - `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`): `jdbcUrl` `${reminder.db.url}`, `username` `${reminder.db.user}`, `maximumPoolSize` `8`.
  - `reminderRepository` (`org.example.reminder.JdbcReminderRepository`): constructor `reminderDataSource`.
  - `mailGateway` (`org.example.reminder.SmtpMailGateway`): `host` `${reminder.smtp.host}`, `port` `587`.
  - `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`): constructor `reminderRepository`, `mailGateway`; property `graceDays` `14`.
- **Scheduler**: `reminderScheduler` (`task:scheduler`) with `pool-size` `2`.
- **Scheduled task**: `overdueReminderJob.sendDueReminders` triggered by cron `0 0 7 * * MON-FRI`.

#### Configuration
- `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host` (from `reminder.properties`).
- `reminder.db.url` → JDBC URL.
- `reminder.db.user` → DB username.
- `reminder.smtp.host` → SMTP host.

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource` (HikariCP).
- `org.example.reminder.JdbcReminderRepository`.
- `org.example.reminder.SmtpMailGateway`.
- `org.example.reminder.OverdueReminderJob`.
- Spring `task` and `context` modules.
