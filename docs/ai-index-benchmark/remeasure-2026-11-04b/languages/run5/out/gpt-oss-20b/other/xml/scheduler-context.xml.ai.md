### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:46Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire reminder scheduler components.
- Define scheduled task for sending due reminders.

#### Type
- Spring context XML (`<beans>` root).  
- Namespaces:  
  - `xmlns="http://www.springframework.org/schema/beans"`  
  - `xmlns:context="http://www.springframework.org/schema/context"`  
  - `xmlns:task="http://www.springframework.org/schema/task"`

#### Definitions
- **Property placeholder**: `reminder.properties` (classpath).
- **Component scan**: `org.example.reminder.web`.
- **Beans**:
  - `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`):  
    - `jdbcUrl` → `${reminder.db.url}`  
    - `username` → `${reminder.db.user}`  
    - `maximumPoolSize` → `8`
  - `reminderRepository` (`org.example.reminder.JdbcReminderRepository`): constructor arg `reminderDataSource`.
  - `mailGateway` (`org.example.reminder.SmtpMailGateway`):  
    - `host` → `${reminder.smtp.host}`  
    - `port` → `587`
  - `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`):  
    - constructor args: `reminderRepository`, `mailGateway`  
    - `graceDays` → `14`
- **Scheduler**: `reminderScheduler` (`task:scheduler`) with `pool-size="2"`.
- **Scheduled task**: `overdueReminderJob.sendDueReminders` triggered by cron `0 0 7 * * MON-FRI`.

#### Configuration
- `reminder.db.url` – JDBC URL (placeholder).
- `reminder.db.user` – DB username (placeholder).
- `reminder.smtp.host` – SMTP host (placeholder).
- `reminder.db.url`, `reminder.db.user`, `reminder.smtp.host` are resolved from `reminder.properties`.

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource` (HikariCP).
- `org.example.reminder.JdbcReminderRepository`.
- `org.example.reminder.SmtpMailGateway`.
- `org.example.reminder.OverdueReminderJob`.
- Spring `task` and `context` modules.
