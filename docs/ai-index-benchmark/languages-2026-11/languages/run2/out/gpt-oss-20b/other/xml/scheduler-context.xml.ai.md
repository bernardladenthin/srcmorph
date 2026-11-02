### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:34:53Z
- G: 
- A: 0.0.0
- X: file
---
> Configures a Spring reminder scheduler with data source, repository, mail gateway, job, and cron trigger.

#### Purpose
- Wire reminder scheduler components.
- Define data source, repository, mail gateway, job, and scheduled task.

#### Type
- Spring XML configuration (`<beans>` root).
- Namespaces: `xmlns` (beans), `xmlns:xsi`, `xmlns:context`, `xmlns:task`.

#### Definitions
- `reminderDataSource`: `com.zaxxer.hikari.HikariDataSource`, properties `jdbcUrl`, `username`, `maximumPoolSize`.
- `reminderRepository`: `org.example.reminder.JdbcReminderRepository`, constructor arg `reminderDataSource`.
- `mailGateway`: `org.example.reminder.SmtpMailGateway`, properties `host`, `port`.
- `overdueReminderJob`: `org.example.reminder.OverdueReminderJob`, constructor args `reminderRepository`, `mailGateway`, property `graceDays`.
- `reminderScheduler`: `task:scheduler` with `pool-size="2"`.
- Scheduled task: method `sendDueReminders` on `overdueReminderJob`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- `context:property-placeholder` location `classpath:reminder.properties`.
- `context:component-scan` base-package `org.example.reminder.web`.
- `reminderDataSource` properties: `${reminder.db.url}`, `${reminder.db.user}`, `maximumPoolSize="8"`.
- `mailGateway` properties: `${reminder.smtp.host}`, `port="587"`.
- `overdueReminderJob` property: `graceDays="14"`.
- Scheduler cron: `0 0 7 * * MON-FRI`.

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource`.
- `org.example.reminder.JdbcReminderRepository`.
- `org.example.reminder.SmtpMailGateway`.
- `org.example.reminder.OverdueReminderJob`.
- Spring schemas: beans, context, task.
