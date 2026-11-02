### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:40:38Z
- G: 
- A: 0.0.0
- X: file
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire reminder scheduler components.
- Load properties from `reminder.properties`.
- Scan `org.example.reminder.web` for components.

#### Type
- Spring XML configuration (`<beans>` root).
- Namespaces:
  - `xmlns="http://www.springframework.org/schema/beans"`
  - `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`
  - `xmlns:context="http://www.springframework.org/schema/context"`
  - `xmlns:task="http://www.springframework.org/schema/task"`

#### Definitions
- **Beans**
  - `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`)
  - `reminderRepository` (`org.example.reminder.JdbcReminderRepository`)
  - `mailGateway` (`org.example.reminder.SmtpMailGateway`)
  - `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`)
- **Scheduler**
  - `reminderScheduler` (`task:scheduler`, pool-size `2`)
- **Scheduled Task**
  - `overdueReminderJob.sendDueReminders` with cron `0 0 7 * * MON-FRI`

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
- Property placeholder location: `classpath:reminder.properties`
- Component scan base-package: `org.example.reminder.web`

#### Dependencies
- External libraries:
  - HikariCP (`com.zaxxer.hikari.HikariDataSource`)
  - Spring Beans, Context, Task schemas
- External classes:
  - `org.example.reminder.JdbcReminderRepository`
  - `org.example.reminder.SmtpMailGateway`
  - `org.example.reminder.OverdueReminderJob`
- External configuration file: `reminder.properties` (classpath)
