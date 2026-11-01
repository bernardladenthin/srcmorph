### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:56:04Z
- G: 
- A: 0.0.0
- X: file
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire reminder scheduler components.
- Define data source, repository, mail gateway, job, and trigger.

#### Type
- Spring context XML.
- Root element: `beans`.
- Namespaces:
  - `xmlns="http://www.springframework.org/schema/beans"`
  - `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`
  - `xmlns:context="http://www.springframework.org/schema/context"`
  - `xmlns:task="http://www.springframework.org/schema/task"`

#### Definitions
- `context:property-placeholder` – loads `classpath:reminder.properties`.
- `context:component-scan` – scans `org.example.reminder.web`.
- Bean `reminderDataSource` – class `com.zaxxer.hikari.HikariDataSource`.
- Bean `reminderRepository` – class `org.example.reminder.JdbcReminderRepository`.
- Bean `mailGateway` – class `org.example.reminder.SmtpMailGateway`.
- Bean `overdueReminderJob` – class `org.example.reminder.OverdueReminderJob`.
- `task:scheduler` – id `reminderScheduler`, pool-size `2`.
- `task:scheduled-tasks` – scheduler `reminderScheduler`.
- `task:scheduled` – ref `overdueReminderJob`, method `sendDueReminders`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- `reminderDataSource` properties:
  - `jdbcUrl` = `${reminder.db.url}`
  - `username` = `${reminder.db.user}`
  - `maximumPoolSize` = `8`
- `mailGateway` properties:
  - `host` = `${reminder.smtp.host}`
  - `port` = `587`
- `overdueReminderJob` property:
  - `graceDays` = `14`

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource`
- `org.example.reminder.JdbcReminderRepository`
- `org.example.reminder.SmtpMailGateway`
- `org.example.reminder.OverdueReminderJob`
- `classpath:reminder.properties` (property file)
