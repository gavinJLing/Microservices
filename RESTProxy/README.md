# RESTProxy:

A simple microservice to act as a proxy between internal microservices and external 3rd party API services. 
Where an internal service will POST a 'Proxy Request' object to the RESTProxy. 
A MongoDb 'services' collection is used to resolve the logical to physical service mapping, where additional static service details are recorded e.g. external 3rd party URL etc. 
The proxy will then invoke the external 3rd party service. 
The response being passed back to the internal client as a 'Proxy Response' object.
Where they should manage the response. A key features of the RESTProxy is that to add additional 
3rd party API's - only the MongoDb ProxyConfig.services collection needs to be updated.


## Assumptions:
A few assumption about this service:

1. This project uses a number of tools, understanding them at a high level should be sufficient to execute this service, following the developer instructions below. 
   Being aware of Git, Docker, MongoDb/ MongoCompass and Gradle build tool, would be an advantage.
2. The RESTProxy service does not currently support URL path parameter value requests 
   (only query params)
3. All services are REST/JSON based.
4. In testing this service, it was helpful to have 3rd party api that supported CRUD 
   operations to test the various HTTP operations.

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
curl -v -d  '{"name": "GetUser", "qparam":[{"key":"userid", "value":"1"}]}' -H "Content-Type: application/json" -X POST http://localhost:8080/remoteservicegateway/proxyService
```
where the expected response should appear as:
```
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> POST /remoteservicegateway/proxyService HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
> Content-Type: application/json
> Content-Length: 61
> 
* upload completely sent off: 61 out of 61 bytes
< HTTP/1.1 200 
< Content-Length: 0
< Date: Mon, 11 Feb 2019 12:30:51 GMT
< 
* Connection #0 to host localhost left intact
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