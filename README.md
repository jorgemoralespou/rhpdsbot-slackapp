= Simple bot to register clusters in slack


== To run with Infinispan
You need an infinispan cache running:

```
docker run -it -p 11222:11222 jboss/infinispan-server:latest
```

Then just run your application in regular mode with Quarkus:

```
mvn compile quarkus:dev 
```

== To run with postgresql
You need a postgreSQL running:

```
docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name rhpdsbot-database -e POSTGRES_USER=rhpdsbot -e POSTGRES_PASSWORD=rhpdsbot -e POSTGRES_DB=rhpdsbot -p 5432:5432 postgres:10.5
```

Then just run your application in regular mode with Quarkus:

```
mvn compile quarkus:dev -Dquarkus.datasource.url=jdbc:postgresql://localhost:5432/rhpdsbot
```
