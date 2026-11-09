### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:05:56Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Configures a Spring application context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire data source, repository, mail gateway, and reminder job.
- Schedule `OverdueReminderJob.sendDueReminders` to run every weekday at 7 AM.

#### Type
- Spring context XML (`<beans>` root).
- Namespaces: `xmlns` (beans), `xmlns:context`, `xmlns:task`.

#### Definitions
- **Data source**: bean `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`) with properties `jdbcUrl`, `username`, `maximumPoolSize`.
- **Repository**: bean `reminderRepository` (`org.example.reminder.JdbcReminderRepository`) constructor‑arg `reminderDataSource`.
- **Mail gateway**: bean `mailGateway` (`org.example.reminder.SmtpMailGateway`) properties `host`, `port`.
- **Job**: bean `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`) constructor‑args `reminderRepository`, `mailGateway`; property `graceDays`.
- **Scheduler**: bean `reminderScheduler` (`task:scheduler`) with `pool-size="2"`.
- **Scheduled task**: `task:scheduled` referencing `overdueReminderJob.sendDueReminders` with cron `0 0 7 * * MON-FRI`.

#### Configuration
- `context:property-placeholder` loads `classpath:reminder.properties`.
- `context:component-scan` base‑package `org.example.reminder.web`.
- `reminderDataSource` properties: `jdbcUrl="${reminder.db.url}"`, `username="${reminder.db.user}"`.
- `mailGateway` properties: `host="${reminder.smtp.host}"`, `port="587"`.
- `overdueReminderJob` property: `graceDays="14"`.
- Scheduler pool size: `2`.
- Cron expression: `0 0 7 * * MON-FRI`.

#### Dependencies
- External properties file: `reminder.properties`.
- JDBC driver implied by `com.zaxxer.hikari.HikariDataSource`.
- SMTP host from properties.
- Spring task scheduling framework.
