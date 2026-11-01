### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:27:43Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the weekday morning reminder scheduler, its database and mail services, and the overdue-reminder job.

#### Purpose
- Wires the reminder data source, repository, SMTP gateway, and scheduled job.
- Runs `sendDueReminders` on weekdays at 07:00.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Property placeholder loads `classpath:reminder.properties`; component scan covers `org.example.reminder.web`.
- Beans:
  - `reminderDataSource`: `com.zaxxer.hikari.HikariDataSource`; destroy method `close`.
  - `reminderRepository`: `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
  - `mailGateway`: `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob`: `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
- Scheduler `reminderScheduler`; pool size `2`.
- Scheduled task references `overdueReminderJob`; method `sendDueReminders`; cron `0 0 7 * * MON-FRI`.

#### Configuration
- `jdbcUrl`: `${reminder.db.url}`; `username`: `${reminder.db.user}`; `maximumPoolSize`: `8`.
- `host`: `${reminder.smtp.host}`; `port`: `587`.
- `graceDays`: `14`.
- Scheduled execution: `0 0 7 * * MON-FRI`; scheduler `reminderScheduler`, pool size `2`.
- Placeholder location: `classpath:reminder.properties`.

#### Dependencies
- Referenced classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
- Property file: `reminder.properties`.
- Spring schemas: `spring-beans.xsd`, `spring-context.xsd`, `spring-task.xsd`.
