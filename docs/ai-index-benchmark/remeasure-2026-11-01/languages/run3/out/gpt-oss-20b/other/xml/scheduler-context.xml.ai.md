### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:57:49Z
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
- `bean` `reminderDataSource` – class `com.zaxxer.hikari.HikariDataSource`.
- `bean` `reminderRepository` – class `org.example.reminder.JdbcReminderRepository`.
- `bean` `mailGateway` – class `org.example.reminder.SmtpMailGateway`.
- `bean` `overdueReminderJob` – class `org.example.reminder.OverdueReminderJob`.
- `task:scheduler` `reminderScheduler` – pool size `2`.
- `task:scheduled-tasks` – schedules `overdueReminderJob.sendDueReminders` with cron `0 0 7 * * MON-FRI`.

#### Configuration
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
- `com.zaxxer.hikari.HikariDataSource`
- `org.example.reminder.JdbcReminderRepository`
- `org.example.reminder.SmtpMailGateway`
- `org.example.reminder.OverdueReminderJob`
- `classpath:reminder.properties` (property file)
- Spring schemas: beans, context, task.
