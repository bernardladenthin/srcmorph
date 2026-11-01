### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:16:15Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the reminder scheduler’s database access, SMTP delivery, and weekday overdue-reminder job.

#### Purpose
- Wires the reminder data source, repository, mail gateway, and overdue-reminder job.
- Runs `sendDueReminders` on weekdays at 07:00.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource` — `com.zaxxer.hikari.HikariDataSource`; `destroy-method="close"`.
  - `reminderRepository` — `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
  - `mailGateway` — `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob` — `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
  - `reminderScheduler` — task scheduler.
- Property placeholder loads `classpath:reminder.properties`.
- Component scan covers `org.example.reminder.web`.
- Scheduled task invokes `overdueReminderJob.sendDueReminders`.

#### Configuration
- `reminderDataSource.jdbcUrl`: `${reminder.db.url}`.
- `reminderDataSource.username`: `${reminder.db.user}`.
- `reminderDataSource.maximumPoolSize`: `8`.
- `mailGateway.host`: `${reminder.smtp.host}`.
- `mailGateway.port`: `587`.
- `overdueReminderJob.graceDays`: `14`.
- `reminderScheduler.pool-size`: `2`.
- Schedule: `cron="0 0 7 * * MON-FRI"`.

#### Dependencies
- Properties file: `classpath:reminder.properties`.
- Placeholder keys: `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host`.
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
- Spring schemas: `spring-beans.xsd`, `spring-context.xsd`, `spring-task.xsd`.
