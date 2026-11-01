### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:41:03Z
- G: 
- A: 0.0.0
- X: file
---
> Configures a Spring scheduler that sends overdue reminders via email.

#### Purpose
- Wire data source, repository, mail gateway, and job for overdue reminders.
- Schedule the job to run every weekday at 7 AM.

#### Type
- XML configuration (Spring beans) – Spring Framework.

#### Input
- `reminder.db.url`, `reminder.db.user` from `reminder.properties`.
- `reminder.smtp.host` from `reminder.properties`.

#### Output
- Beans: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- Scheduled task: `overdueReminderJob.sendDueReminders` executed by `reminderScheduler`.

#### Core logic
- Define HikariCP data source with URL, user, pool size 8.
- Instantiate `JdbcReminderRepository` with data source.
- Instantiate `SmtpMailGateway` with SMTP host and port 587.
- Instantiate `OverdueReminderJob` with repository, mail gateway, graceDays 14.
- Create a scheduler pool of size 2.
- Schedule `sendDueReminders` method to run at 7 AM Monday–Friday.

#### Public API
- Beans exposed: `reminderDataSource`, `reminderRepository`, `mailGateway`, `overdueReminderJob`, `reminderScheduler`.
- Scheduled method: `overdueReminderJob.sendDueReminders()` (cron: `0 0 7 * * MON-FRI`).

#### Constants and configuration
- `maximumPoolSize = 8`
- `port = 587`
- `graceDays = 14`
- `pool-size = 2`

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource`
- `org.example.reminder.JdbcReminderRepository`
- `org.example.reminder.SmtpMailGateway`
- `org.example.reminder.OverdueReminderJob`
- Spring schemas: beans, context, task.

#### Exceptions / Errors
- No explicit exception handling in this file; relies on Spring and underlying classes.
