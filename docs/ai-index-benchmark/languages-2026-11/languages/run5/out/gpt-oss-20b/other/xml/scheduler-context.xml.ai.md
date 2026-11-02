### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:36:11Z
- G: 
- A: 0.0.0
- X: file
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire reminder scheduler components.
- Provide data source, repository, mail gateway, job, and trigger.

#### Type
- Spring context XML.
- Root element: `beans`.
- Namespaces:
  - `xmlns="http://www.springframework.org/schema/beans"`
  - `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`
  - `xmlns:context="http://www.springframework.org/schema/context"`
  - `xmlns:task="http://www.springframework.org/schema/task"`

#### Definitions
- **Property placeholder**: `location="classpath:reminder.properties"`.
- **Component scan**: `base-package="org.example.reminder.web"`.
- **Beans**:
  - `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`), `destroy-method="close"`.
  - `reminderRepository` (`org.example.reminder.JdbcReminderRepository`), constructor arg `ref="reminderDataSource"`.
  - `mailGateway` (`org.example.reminder.SmtpMailGateway`), properties `host`, `port`.
  - `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`), constructor args `ref="reminderRepository"`, `ref="mailGateway"`, property `graceDays`.
- **Scheduler**: `reminderScheduler` (`task:scheduler`), `pool-size="2"`.
- **Scheduled task**: `ref="overdueReminderJob"`, method `sendDueReminders`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- `reminderDataSource`:
  - `jdbcUrl` `${reminder.db.url}`
  - `username` `${reminder.db.user}`
  - `maximumPoolSize` `8`
- `mailGateway`:
  - `host` `${reminder.smtp.host}`
  - `port` `587`
- `overdueReminderJob`:
  - `graceDays` `14`
- Scheduler cron: `0 0 7 * * MON-FRI`

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource`
- `org.example.reminder.JdbcReminderRepository`
- `org.example.reminder.SmtpMailGateway`
- `org.example.reminder.OverdueReminderJob`
- `org.example.reminder.web` package (component scan)
- `reminder.properties` (property file)
