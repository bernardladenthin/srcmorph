### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:12:47Z
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
- `context:property-placeholder` location `classpath:reminder.properties`
- `context:component-scan` base-package `org.example.reminder.web`
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
- Spring task scheduler (`task:scheduler`)
- Property file `classpath:reminder.properties`  
- Component scan package `org.example.reminder.web`
