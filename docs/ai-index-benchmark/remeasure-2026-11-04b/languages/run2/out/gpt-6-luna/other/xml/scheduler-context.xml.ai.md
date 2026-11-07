### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:16:55Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Configures the reminder service’s database access, SMTP delivery, and weekday morning overdue-reminder schedule.

#### Purpose
- Wires the reminder repository and mail gateway into the overdue-reminder job.
- Schedules reminder delivery on weekdays.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Property placeholder loads `classpath:reminder.properties`; keys used: `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host`.
- Component scan: `org.example.reminder.web`.
- Beans:
  - `reminderDataSource`: `com.zaxxer.hikari.HikariDataSource`; destroy method `close`.
  - `reminderRepository`: `org.example.reminder.JdbcReminderRepository`; constructor reference `reminderDataSource`.
  - `mailGateway`: `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob`: `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository`, `mailGateway`.
  - `reminderScheduler`: task scheduler.
- Scheduled task: `overdueReminderJob.sendDueReminders`, using `reminderScheduler`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- `reminderDataSource.jdbcUrl`: `${reminder.db.url}`.
- `reminderDataSource.username`: `${reminder.db.user}`.
- `reminderDataSource.maximumPoolSize`: `8`.
- `mailGateway.host`: `${reminder.smtp.host}`.
- `mailGateway.port`: `587`.
- `overdueReminderJob.graceDays`: `14`.
- `reminderScheduler.pool-size`: `2`.
- Schedule: `0 0 7 * * MON-FRI` (07:00, Monday–Friday).

#### Dependencies
- `classpath:reminder.properties`.
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
