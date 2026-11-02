### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:11:15Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the weekday reminder job, its database and SMTP services, and the scheduler that runs it.

#### Purpose
- Wires reminder persistence, email delivery, and scheduled overdue-reminder processing.
- Loads reminder settings from `reminder.properties`.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` — `http://www.w3.org/2001/XMLSchema-instance`; `context` — `http://www.springframework.org/schema/context`; `task` — `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource` — `com.zaxxer.hikari.HikariDataSource`; destroy method `close`.
  - `reminderRepository` — `org.example.reminder.JdbcReminderRepository`; constructor reference `reminderDataSource`.
  - `mailGateway` — `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob` — `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
  - `reminderScheduler` — task scheduler.
- Property placeholder file: `classpath:reminder.properties`.
- Component scan: `org.example.reminder.web`.
- Scheduled task: `overdueReminderJob.sendDueReminders`, using `reminderScheduler`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- `reminderDataSource.jdbcUrl` — `${reminder.db.url}`.
- `reminderDataSource.username` — `${reminder.db.user}`.
- `reminderDataSource.maximumPoolSize` — `8`.
- `mailGateway.host` — `${reminder.smtp.host}`.
- `mailGateway.port` — `587`.
- `overdueReminderJob.graceDays` — `14`.
- `reminderScheduler.pool-size` — `2`.
- Scheduled task cron — `0 0 7 * * MON-FRI`.

#### Dependencies
- Configuration file: `classpath:reminder.properties`.
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
- Spring Beans, Context, and Task schemas.
