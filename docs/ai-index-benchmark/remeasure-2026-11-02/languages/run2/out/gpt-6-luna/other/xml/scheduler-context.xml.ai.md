### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:40:42Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Configures the reminder service’s database access, SMTP delivery, and weekday morning overdue-reminder schedule.

#### Purpose
- Wires the reminder repository, mail gateway, and overdue-reminder job.
- Loads reminder settings and schedules the job to send due reminders.

#### Type
- Spring application context; root element: `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource` — `com.zaxxer.hikari.HikariDataSource`; `destroy-method="close"`.
  - `reminderRepository` — `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
  - `mailGateway` — `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob` — `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
- Component scan: `org.example.reminder.web`.
- Scheduler: `reminderScheduler`; scheduled tasks use it.
- Scheduled task: `overdueReminderJob.sendDueReminders`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- Property file: `classpath:reminder.properties`.
- `reminderDataSource`: `jdbcUrl=${reminder.db.url}`; `username=${reminder.db.user}`; `maximumPoolSize=8`.
- `mailGateway`: `host=${reminder.smtp.host}`; `port=587`.
- `overdueReminderJob`: `graceDays=14`.
- `reminderScheduler`: `pool-size=2`.
- Schedule: `0 0 7 * * MON-FRI` (07:00, Monday–Friday).

#### Dependencies
- Referenced configuration: `classpath:reminder.properties`.
- Referenced classes: `com.zaxxer.hikari.HikariDataSource`; `org.example.reminder.JdbcReminderRepository`; `org.example.reminder.SmtpMailGateway`; `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
- Property placeholders: `${reminder.db.url}`, `${reminder.db.user}`, `${reminder.smtp.host}`.
