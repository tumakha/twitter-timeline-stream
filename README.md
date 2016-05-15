## Twitter user timeline streaming

### Prerequisites
Java 8, Maven 3.1+

### Run/Debug main class     

    ua.tumakha.yuriy.twitter.timeline.WebApplication

### Run web application from mvn

    mvn spring-boot:run

### Web access by browser with [HTML5 WebSockets](https://html5test.com/compare/feature/communication-websocket.basic.html) support 

    http://localhost:8888

### Package project to the single jar twitter-timeline-stream.jar    

    mvn clean package

### Run web application jar from command line   

    java -jar twitter-timeline-stream.jar