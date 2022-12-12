Related jira item: https://hibernate.atlassian.net/browse/HHH-15852

General comments:
1. The tests will create DB under location: `hibernate.connection.url jdbc:h2:file:/tmp/hhh15852`

Reproduction steps:
1. Run test `hhh15852Test()` with VM option `-Xmx256M`
