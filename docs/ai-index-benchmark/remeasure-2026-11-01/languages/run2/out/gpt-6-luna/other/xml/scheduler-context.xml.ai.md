### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:39:14Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the weekday reminder scheduler, database connection pool, reminder repository, and SMTP mail gateway.

#### Purpose
- Wires the reminder job and its data and mail services.
- Runs due-reminder delivery on weekday mornings.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Bean `reminderDataSource`: `com.zaxxer.hikari.HikariDataSource`; `destroy-method="close"`.
- Bean `reminderRepository`: `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
- Bean `mailGateway`: `org.example.reminder.SmtpMailGateway`.
- Bean `overdueReminderJob`: `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
- Component scan: `org.example.reminder.web`.
- Scheduler `reminderScheduler`; pool size `2`.
- Scheduled task: `overdueReminderJob.sendDueReminders`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- Property-placeholder location: `classpath:reminder.properties`.
- `reminderDataSource.jdbcUrl`: `${reminder.db.url}`.
- `reminderDataSource.username`: `${reminder.db.user}`.
- `reminderDataSource.maximumPoolSize`: `8`.
- `mailGateway.host`: `${reminder.smtp.host}`.
- `mailGateway.port`: `587`.
- `overdueReminderJob.graceDays`: `14`.
- Scheduled task cron: `0 0 7 * * MON-FRI`.

#### Dependencies
- Configuration file: `classpath:reminder.properties`.
- Spring beans, context, and task schemas.
- Classes: `com.zaxxer.hikari.HikariDataSource`; `org.example.reminder.JdbcReminderRepository`; `org.example.reminder.SmtpMailGateway`; `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
- External services: database at `${reminder.db.url}`; SMTP host `${reminder.smtp.host}` on port `587`.
