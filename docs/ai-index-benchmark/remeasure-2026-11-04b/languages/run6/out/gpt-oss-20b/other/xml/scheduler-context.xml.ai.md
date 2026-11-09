### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:45:52Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Configures a Spring context for a reminder scheduler, defining data source, repository, mail gateway, job, and scheduled trigger.

#### Purpose
- Wire components for reminder scheduling.
- Define data source, repository, mail gateway, job, and cron trigger.

#### Type
- Spring context XML (`<beans>` root).  
- Namespaces:  
  - `xmlns="http://www.springframework.org/schema/beans"`  
  - `xmlns:context="http://www.springframework.org/schema/context"`  
  - `xmlns:task="http://www.springframework.org/schema/task"`  
  - `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`

#### Definitions
- **Property placeholder**: `reminder.properties`  
- **Component scan**: `org.example.reminder.web`  
- **Beans**:  
  - `reminderDataSource` (`com.zaxxer.hikari.HikariDataSource`)  
  - `reminderRepository` (`org.example.reminder.JdbcReminderRepository`)  
  - `mailGateway` (`org.example.reminder.SmtpMailGateway`)  
  - `overdueReminderJob` (`org.example.reminder.OverdueReminderJob`)  
- **Scheduler**: `reminderScheduler` (`pool-size="2"`)  
- **Scheduled task**: `overdueReminderJob.sendDueReminders` with cron `0 0 7 * * MON-FRI`

#### Configuration
- `reminderDataSource`  
  - `jdbcUrl` = `${reminder.db.url}`  
  - `username` = `${reminder.db.user}`  
  - `maximumPoolSize` = `8`  
- `mailGateway`  
  - `host` = `${reminder.smtp.host}`  
  - `port` = `587`  
- `overdueReminderJob`  
  - `graceDays` = `14`  
- `reminderScheduler`  
  - `pool-size` = `2`  
- `overdueReminderJob.sendDueReminders`  
  - `cron` = `0 0 7 * * MON-FRI`

#### Dependencies
- `com.zaxxer.hikari.HikariDataSource`  
- `org.example.reminder.JdbcReminderRepository`  
- `org.example.reminder.SmtpMailGateway`  
- `org.example.reminder.OverdueReminderJob`  
- Spring `task` and `context` modules.
