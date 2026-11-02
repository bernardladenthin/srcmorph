### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:56:18Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the reminder service’s database, SMTP gateway, overdue-reminder job, and weekday scheduler.

#### Purpose
- Wires the reminder application’s data access and email delivery components.
- Schedules overdue reminders for weekday mornings.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Property placeholder loads `reminder.properties` from the classpath.
- Component scan covers `org.example.reminder.web`.
- Bean `reminderDataSource`: `com.zaxxer.hikari.HikariDataSource`; destroy method `close`.
- Bean `reminderRepository`: `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
- Bean `mailGateway`: `org.example.reminder.SmtpMailGateway`.
- Bean `overdueReminderJob`: `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
- Scheduler `reminderScheduler`; scheduled task invokes `overdueReminderJob.sendDueReminders`.

#### Configuration
- Property placeholders: `${reminder.db.url}`, `${reminder.db.user}`, `${reminder.smtp.host}`.
- `reminderDataSource.jdbcUrl`: `${reminder.db.url}`.
- `reminderDataSource.username`: `${reminder.db.user}`.
- `reminderDataSource.maximumPoolSize`: `8`.
- `mailGateway.host`: `${reminder.smtp.host}`.
- `mailGateway.port`: `587`.
- `overdueReminderJob.graceDays`: `14`.
- `reminderScheduler.pool-size`: `2`.
- Scheduled task `cron`: `0 0 7 * * MON-FRI`.

#### Dependencies
- Referenced configuration: `classpath:reminder.properties`.
- Scanned package: `org.example.reminder.web`.
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Spring schemas: `spring-beans.xsd`, `spring-context.xsd`, `spring-task.xsd`.
