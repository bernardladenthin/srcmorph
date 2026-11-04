### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:00Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Configures a Spring application context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Sets up a reminder scheduler with database access and email notifications.
- Declares a scheduled task to send due reminders every weekday at 7 AM.

#### Type
- Spring application context (`<beans>` root).
- Namespaces: `http://www.springframework.org/schema/beans` (default), `http://www.springframework.org/schema/context`, `http://www.springframework.org/schema/task`.

#### Definitions
- **Property placeholder**: `reminder.properties` (classpath).
- **Component scan**: `org.example.reminder.web`.
- **Beans**:
  - `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`): `jdbcUrl`=`${reminder.db.url}`, `username`=`${reminder.db.user}`, `maximumPoolSize`=`8`.
  - `reminderRepository` (`org.example.reminder.JdbcReminderRepository`): constructor arg `reminderDataSource`.
  - `mailGateway` (`org.example.reminder.SmtpMailGateway`): `host`=`${reminder.smtp.host}`, `port`=`587`.
  - `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`): constructor args `reminderRepository`, `mailGateway`; property `graceDays`=`14`.
- **Scheduler**: `reminderScheduler` (`task:scheduler`) with `pool-size`=`2`.
- **Scheduled task**: `overdueReminderJob.sendDueReminders` invoked by cron `0 0 7 * * MON-FRI` via `reminderScheduler`.

#### Configuration
- `reminder.db.url` – database JDBC URL.
- `reminder.db.user` – database username.
- `reminder.smtp.host` – SMTP host.
- `reminder.smtp.port` – SMTP port (fixed 587).
- `overdueReminderJob.graceDays` – 14 days.
- Scheduler pool size – 2 threads.

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource` (HikariCP).
- `org.example.reminder.JdbcReminderRepository`.
- `org.example.reminder.SmtpMailGateway`.
- `org.example.reminder.OverdueReminderJob`.
- Spring `task` and `context` modules.
