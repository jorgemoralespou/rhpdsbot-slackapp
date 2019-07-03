= Simple bot to register clusters in slack


== To run
You need an infinispan cache running:

```
docker run -it -p 11222:11222 jboss/infinispan-server:latest
```

Then just run your application in regular mode with Quarkus:

```
mvn compile quarkus:dev
```

