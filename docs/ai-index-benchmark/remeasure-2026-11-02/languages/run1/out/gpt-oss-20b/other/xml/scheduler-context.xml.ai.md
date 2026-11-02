### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:25:48Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire components for reminder scheduling.
- Define data source, repository, mail gateway, job, and trigger.

#### Type
- Spring XML configuration (`<beans>` root).
- Namespaces: `xmlns` (beans), `xmlns:xsi`, `xmlns:context`, `xmlns:task`.

#### Definitions
- `reminderDataSource`: `com.zaxxer.hikari.HikariDataSource`, destroy `close`.
- `reminderRepository`: `org.example.reminder.JdbcReminderRepository`, constructor `reminderDataSource`.
- `mailGateway`: `org.example.reminder.SmtpMailGateway`, properties `host`, `port`.
- `overdueReminderJob`: `org.example.reminder.OverdueReminderJob`, constructor `reminderRepository`, `mailGateway`; property `graceDays`.
- `reminderScheduler`: `task:scheduler`, pool-size `2`.
- Scheduled task: method `sendDueReminders` on `overdueReminderJob`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- `context:property-placeholder` location `classpath:reminder.properties`.
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
- `com.zaxxer.hikari.HikariDataSource`
- `org.example.reminder.JdbcReminderRepository`
- `org.example.reminder.SmtpMailGateway`
- `org.example.reminder.OverdueReminderJob`
- Spring task scheduler (`task:scheduler`)
- Properties file `classpath:reminder.properties`  
- Component scan package `org.example.reminder.web`
