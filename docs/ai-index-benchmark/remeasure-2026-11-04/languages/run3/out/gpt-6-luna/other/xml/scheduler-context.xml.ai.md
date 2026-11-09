### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:04:35Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Configures the reminder service’s database, repository, SMTP gateway, job, and weekday scheduler.

#### Purpose
- Wires components for sending reminders for overdue items.
- Loads reminder settings from a classpath properties file and schedules the job on weekdays.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource` — `com.zaxxer.hikari.HikariDataSource`; `destroy-method="close"`.
  - `reminderRepository` — `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
  - `mailGateway` — `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob` — `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
  - `reminderScheduler` — Spring task scheduler.
- Component scan: `org.example.reminder.web`.
- Scheduled task: `overdueReminderJob.sendDueReminders`, using `reminderScheduler`.

#### Configuration
- Property placeholder location: `classpath:reminder.properties`.
- `reminderDataSource`: `jdbcUrl=${reminder.db.url}`; `username=${reminder.db.user}`; `maximumPoolSize=8`.
- `mailGateway`: `host=${reminder.smtp.host}`; `port=587`.
- `overdueReminderJob`: `graceDays=14`.
- `reminderScheduler`: `pool-size=2`.
- Schedule: cron `0 0 7 * * MON-FRI` (07:00, Monday–Friday).

#### Dependencies
- Properties file: `reminder.properties`.
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Placeholder keys: `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host`.
