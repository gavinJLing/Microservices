# RESTProxy:

A simple microservice to act as a proxy between internal microservices and external 3rd party API services. Where an internal service will create a logical service request object and POST this to the RESTProxy. A MongoDb 'services' collection is used to resolve the logical to physical service mapping, where additional static service details are recorded e.g. external 3rd party URL etc. The proxy will then invoke the external 3rd party service. The response being passed back to the internal client of this proxy, where they should mange the response. The assumption here is that this is a highly REST centric landscape. However the system could be extended to handle different message payload types (XML/SOAP or text) and transport implementations e.g. async RabbitMQ or Kafka service invocation passing back a correlation id upon the msg being published to the queue/topic.

## Assumptions:
This project uses a number of tools, understanding them at a high level should be sufficient to execute this service, following the developer instructions below. Being aware of Git, Docker, MongoDb/ MongoCompass, a REST test client (e.g. Postman) and Gradle build tool, would be an advantage.

## Get the example
```
git clone git@github.com:gavinJLing/Microservices.git
cd RESTProxy
```


## MongoDb launch
This project requires a MongoDb store. The express way to establish a MongoDb server is leverage an off-the-self prebuilt [Offical Docker MongoDb Image](https://hub.docker.com/_/mongo).  The following steps assume that you have Docker installed. The latest 'mongo' image is pulled from the internet (Docker Hub). The execution container 'myMongo' has port 27017 exposed to your host environment. Then populate the Mongo Database 'ProxyConfig' with a collection of service definitions held as json file.

```
docker pull mongo
docker run --name myMongo -p 27017:27017 -d mongo 
mongoimport --host=localhost:27017 --db=ProxyConfig --type=json --file=./data/services.json --jsonArray

```

## Building and launching the Proxy service
This is a standard Gradle build where Gradle actions such as ... 
```
./gradlew clean
./gradlew build
./gradlew bootRun
```
Will perform a clean build of the RESTProxy service and execute it using default configuration.  The 'build' command will create a standard Java .jar deliverable which is a self executing .jar e.g.
```
java -jar build/libs/RESTProxy-0.0.1-SNAPSHOT.jar
```


## Test and code coverage reports
To run the unit tests, or produce a code coverage report (Jacoco)
```
 ./gradlew clean test jacocoTestReport    
```
To see the Gradle test html report 
```
open  ./build/reports/tests/test/index.html
```

To see the Jacoco Codecoverage report
```
open ./build/reports/jacoco/test/html/index.html 
```

## POST a RESTProxy Request
With the MongoDb and RESTProxy server launched as described above, perform a HTTP POST with a body JSON to the RESTProxy service endpoint to trigger a 3rd Party service and obtain a response. Various tools can be used for this 'PostMan' plugin for the Chrome browser is propular, however other tools exist such as 'curl' and 'wget'.  See below for an example of 'curl' POST'ing a logical request as a JSON body to the RESTProxy endpoint.
```
curl -d '{"name": "GetUser", "qparam":[{"key":"userid", "value":"1"}]}' -H "Content-Type: application/json" -X POST http://localhost:8080/serviceproxy
```

### Configuration overrides
The standard code is defaulting to Dev default values. These can be overridden typically for HTTP Listener & MongoDb ports.

To apply different runtime configuration e.g.  

Service Property     | Default | Alternative Value
-------------|-----------|----------
server.port  | 8080  | 8081
spring.data.mongodb.host | localhost | myhost
spring.data.mongodb.port | 27017 | 27666
spring.data.mongodb.database | ProxyConfig | someOtherDb



use the following command line switches

```
java -jar build/libs/RESTProxy-0.0.1-SNAPSHOT.jar  --spring.data.mongodb.host=myhost  --spring.data.mongodb.port=27666  --spring.data.mongodb.database=someOtherDb   --server.port=8081 
```