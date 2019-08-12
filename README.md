## Vert.x Fun
A fun filled project exploring the vert.x framework using Java and Redis

### NOTE on my HTTP status codes
I know that a good RESTful implementation should have appropriate status codes. When I wrote this, I really thought 418 (I'm a teapot) was pretty much the funniest thing ever. That's why any !200 is a 418. Just fyi

### Getting started
```git clone <repo>```<br />
```mvn clean install```<br />
```java -jar target/vertx-fun-2.16.2018-fat.jar```

#### Redis dependency
```brew update```<br />
```brew upgrade```<br />
```brew install redis```<br />
```redis-server /usr/local/etc/redis.conf```<br />
```redis-cli ping```

#### Test the Http endpoints with Postman
see the items array in sample.json <br />
note: the port in the config might not match the ports used in the sample.json
