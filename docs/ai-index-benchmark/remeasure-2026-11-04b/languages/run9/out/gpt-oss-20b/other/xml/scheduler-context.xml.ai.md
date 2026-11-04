### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:35Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire reminder scheduler components.
- Schedule overdue reminder job.

#### Type
- Spring context XML.
- Root element: `beans`.
- Namespaces: `http://www.springframework.org/schema/beans` (default), `http://www.springframework.org/schema/context` (`context`), `http://www.springframework.org/schema/task` (`task`).

#### Definitions
- **Property placeholder**: `reminder.properties`.
- **Component scan**: `org.example.reminder.web`.
- **Beans**:
  - `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`), properties: `jdbcUrl`, `username`, `maximumPoolSize`.
  - `reminderRepository` (`org.example.reminder.JdbcReminderRepository`), constructor arg: `reminderDataSource`.
  - `mailGateway` (`org.example.reminder.SmtpMailGateway`), properties: `host`, `port`.
  - `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`), constructor args: `reminderRepository`, `mailGateway`; property: `graceDays`.
- **Scheduler**: `reminderScheduler` (`task:scheduler`), `pool-size="2"`.
- **Scheduled task**: reference `overdueReminderJob`, method `sendDueReminders`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- `reminder.db.url` → `${reminder.db.url}`
- `reminder.db.user` → `${reminder.db.user}`
- `reminder.smtp.host` → `${reminder.smtp.host}`
- `maximumPoolSize` = `8`
- `port` = `587`
- `graceDays` = `14`
- `cron` = `0 0 7 * * MON-FRI`

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource`
- `org.example.reminder.JdbcReminderRepository`
- `org.example.reminder.SmtpMailGateway`
- `org.example.reminder.OverdueReminderJob`
- Spring `task` and `context` modules.
