### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:34:31Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the reminder service’s database, mail gateway, job, and weekday scheduling.

#### Purpose
- Wires reminder persistence and SMTP delivery into a scheduled overdue-reminder job.
- Loads reminder settings from `reminder.properties` and scans the reminder web package.

#### Type
- Spring application context; root element: `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` — `http://www.w3.org/2001/XMLSchema-instance`; `context` — `http://www.springframework.org/schema/context`; `task` — `http://www.springframework.org/schema/task`.

#### Definitions
- Property placeholder loads `classpath:reminder.properties`.
- Component scan covers `org.example.reminder.web`.
- Bean `reminderDataSource`: class `com.zaxxer.hikari.HikariDataSource`; destroy method `close`.
- Bean `reminderRepository`: class `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
- Bean `mailGateway`: class `org.example.reminder.SmtpMailGateway`.
- Bean `overdueReminderJob`: class `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
- Scheduler `reminderScheduler`; pool size `2`.
- Scheduled task references `overdueReminderJob`, invokes `sendDueReminders`, and uses cron schedule `0 0 7 * * MON-FRI`.

#### Configuration
- `reminderDataSource.jdbcUrl`: `${reminder.db.url}`
- `reminderDataSource.username`: `${reminder.db.user}`
- `reminderDataSource.maximumPoolSize`: `8`
- `mailGateway.host`: `${reminder.smtp.host}`
- `mailGateway.port`: `587`
- `overdueReminderJob.graceDays`: `14`
- Scheduled task `cron`: `0 0 7 * * MON-FRI`

#### Dependencies
- Configuration file: `classpath:reminder.properties`
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`
- Scanned package: `org.example.reminder.web`
- Database connection settings: `reminder.db.url`, `reminder.db.user`
- SMTP host setting: `reminder.smtp.host`
