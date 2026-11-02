### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:56:26Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire reminder scheduler components.
- Schedule overdue reminder job.

#### Type
- Spring XML configuration (`<beans>` root).
- Namespaces: `xmlns` (beans), `xmlns:xsi`, `xmlns:context`, `xmlns:task`.

#### Definitions
- **Property placeholder**: `reminder.properties`.
- **Component scan**: `org.example.reminder.web`.
- **Beans**:
  - `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`), properties: `jdbcUrl`, `username`, `maximumPoolSize`.
  - `reminderRepository` (`org.example.reminder.JdbcReminderRepository`), constructor arg: `reminderDataSource`.
  - `mailGateway` (`org.example.reminder.SmtpMailGateway`), properties: `host`, `port`.
  - `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`), constructor args: `reminderRepository`, `mailGateway`; property: `graceDays`.
- **Scheduler**: `reminderScheduler` (`task:scheduler`) with `pool-size="2"`.
- **Scheduled task**: reference `overdueReminderJob`, method `sendDueReminders`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- `reminderDataSource`:
  - `jdbcUrl` → `${reminder.db.url}`
  - `username` → `${reminder.db.user}`
  - `maximumPoolSize` → `8`
- `mailGateway`:
  - `host` → `${reminder.smtp.host}`
  - `port` → `587`
- `overdueReminderJob`:
  - `graceDays` → `14`
- `reminderScheduler`:
  - `pool-size` → `2`
- `overdueReminderJob` scheduled:
  - `method` → `sendDueReminders`
  - `cron` → `0 0 7 * * MON-FRI`

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource`
- `org.example.reminder.JdbcReminderRepository`
- `org.example.reminder.SmtpMailGateway`
- `org.example.reminder.OverdueReminderJob`
- External property file: `classpath:reminder.properties`
- Spring schemas: beans, context, task.
