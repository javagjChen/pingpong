requirement
  Imaging a simplified scenario where you are asked to implement 2 microservices called “ping” and “pong” respectively and integrates with each other as illustrated below – 
    Both services must be implemented using Spring WebFlux and can be packaged & run as executable jar.
    Both services are running locally in same device. The integration goes as simple as – Ping Service attempts to says “Hello” to Pong Service around every 1 secondand then Pong service should respond with “World”.
    The Pong Service should be implemented with a Throttling Control limited with value 1, meaning that – 
    For any given second, there is only 1 requests can be handled by it.
    For those additional requests coming in the given second, Pong Service should return 429 status code.
    Multiple Ping Services should be running as separate JVM Process with capability of Rate Limit Control across all processes with only 2 RPS (hint: consider using Java FileLock), meaning that - 
    If all processes attempt to triggers Pong Service at the same time, only 2 requests are allowed to go out to Pong Service.
    Among the 2 outgoing requests to Pong, if they reach Pong Service within the same second, one of them are expected to be throttled with 429 status code.
    Each Ping service process must log the request attempt with result (*)  in separate logs per instance for review. The result includes:
    Request sent & Pong Respond.
    Request not send as being “rate limited”.
    Request send & Pong throttled it.
    Increase the number of running Ping processes locally and review the logs for each.
 

  BE Code Quality Acceptance Criteria:
    Using Spring Boot and Spring Webflux Framework is a must.
    Using Message queue Experiences(Kafka, Zero MQ/Rocket MQ),Hazalcast/K8S,Postgre SQL,MongoDB is a must.
    Using Spring Spock Framework in Groovy for Unit Test.
    Unit Test with Coverage >= 80%. (hint: Maven Jacoco Plugin should be used).
    Completion of the Challenge should not take longer than 3 days.
