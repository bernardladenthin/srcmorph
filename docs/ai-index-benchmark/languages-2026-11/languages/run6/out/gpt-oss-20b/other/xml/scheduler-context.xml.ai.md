### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:47:19Z
- G: 
- A: 0.0.0
- X: file
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire reminder scheduler components.
- Define data source, repository, mail gateway, job, and trigger.

#### Type
- Spring XML configuration (`<beans>` root).
- Namespaces:
  - `xmlns="http://www.springframework.org/schema/beans"`
  - `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`
  - `xmlns:context="http://www.springframework.org/schema/context"`
  - `xmlns:task="http://www.springframework.org/schema/task"`

#### Definitions
- `context:property-placeholder` – loads `classpath:reminder.properties`.
- `context:component-scan` – scans `org.example.reminder.web`.
- Bean `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`):
  - `jdbcUrl` `${reminder.db.url}`
  - `username` `${reminder.db.user}`
  - `maximumPoolSize` `8`
- Bean `reminderRepository` (`org.example.reminder.JdbcReminderRepository`):
  - Constructor arg `ref="reminderDataSource"`
- Bean `mailGateway` (`org.example.reminder.SmtpMailGateway`):
  - `host` `${reminder.smtp.host}`
  - `port` `587`
- Bean `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`):
  - Constructor args `ref="reminderRepository"`, `ref="mailGateway"`
  - `graceDays` `14`
- `task:scheduler` `reminderScheduler` – pool size `2`.
- `task:scheduled-tasks` – schedules `overdueReminderJob.sendDueReminders` with cron `0 0 7 * * MON-FRI`.

#### Configuration
- `reminder.db.url` – JDBC URL.
- `reminder.db.user` – DB username.
- `reminder.smtp.host` – SMTP host.
- `reminder.smtp.port` – SMTP port (587).
- `reminder.db.maximumPoolSize` – 8.
- `overdueReminderJob.graceDays` – 14.
- Scheduler pool size – 2.
- Cron expression – `0 0 7 * * MON-FRI`.

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource`
- `org.example.reminder.JdbcReminderRepository`
- `org.example.reminder.SmtpMailGateway`
- `org.example.reminder.OverdueReminderJob`
- Spring schemas: beans, context, task.
