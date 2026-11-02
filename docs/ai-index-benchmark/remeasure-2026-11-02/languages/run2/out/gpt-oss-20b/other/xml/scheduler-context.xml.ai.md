### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:41:59Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Configures a Spring application context that wires a data source, repository, mail gateway, reminder job, and a scheduled task to send overdue reminders.

#### Purpose
- Sets up data access and email components for a reminder system.
- Schedules the `OverdueReminderJob` to run weekly on weekdays at 7 AM.

#### Type
- Spring XML configuration (`<beans>` root).
- Namespaces: `xmlns` (beans), `xmlns:xsi`, `xmlns:context`, `xmlns:task`.

#### Definitions
- **Beans**
  - `reminderDataSource`: `com.zaxxer.hikari.HikariDataSource`
  - `reminderRepository`: `org.example.reminder.JdbcReminderRepository`
  - `mailGateway`: `org.example.reminder.SmtpMailGateway`
  - `overdueReminderJob`: `org.example.reminder.OverdueReminderJob`
- **Scheduler**
  - `reminderScheduler`: `task:scheduler` with `pool-size="2"`
- **Scheduled Task**
  - Reference `overdueReminderJob`, method `sendDueReminders`, cron `0 0 7 * * MON-FRI`

#### Configuration
- `context:property-placeholder` loads `classpath:reminder.properties`.
- `context:component-scan` base-package `org.example.reminder.web`.
- `reminderDataSource` properties:
  - `jdbcUrl` `${reminder.db.url}`
  - `username` `${reminder.db.user}`
  - `maximumPoolSize` `8`
- `mailGateway` properties:
  - `host` `${reminder.smtp.host}`
  - `port` `587`
- `overdueReminderJob` property:
  - `graceDays` `14`

#### Dependencies
- External properties file: `classpath:reminder.properties`.
- JDBC driver implied by `com.zaxxer.hikari.HikariDataSource`.
- SMTP server defined by `${reminder.smtp.host}`.
- Spring task scheduling (`task:scheduler`, `task:scheduled-tasks`).
