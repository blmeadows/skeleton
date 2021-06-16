# A Skeleton for FP Scala Microservices on the Typelevel Stack.

![](https://static.wikia.nocookie.net/survive-the-disasters-fanon/images/0/0f/Spooky_Scary_Skeleton.gif/revision/latest?cb=20201112143346)

## WARNING:
This is very much a work-in-progress and NOT yet ready to consume. The overall goal is to have a "real-world" project 
demonstrating the use of and interaction between various Typelevel projects, that is documented and easily consumable.

### TODO:
- add rho docs? or simple swagger docs
- add more tests, of course
- other middlewares
  - add Auth to routes
  - add CORS
  - add CSRF
  - add more to Metrics beyond default
  - add x-request-id middleware
- add health checks support
- add circuit breaking example
- add kafka example
- add error handling to routes
- http4s headers
- http4s cookies
- http4s streaming bodies w/fs2
- http4s /: route (arbitrary depth)
- http4s path parameters
- http4s query parameters
- add redis example
- add at least one more db migration
- add docs for site in docs/
- scala.js?
- add pagination
- review scalafmt.conf

### To run from scratch
#### (NEED TO TEST THIS IN CLEAN ENVIRONMENT TO DOUBLE CHECK)
- if needed, install coursier: https://get-coursier.io/docs/cli-installation.html#native-launcher
  and run the `setup` command: https://get-coursier.io/docs/cli-setup
- install postgres: https://postgresapp.com/ (install and start)
- install docker: https://www.docker.com/get-started
- start postgres (in postgresapp or manually)
- start jaeger: jaeger instructions in Tracing.scala (copy that here)
- sbt core/run
- localhost:8080 for server, in browser or curl/etc
- localhost:16686 for jaeger, in browser

### curl commands examples
#### (expand and refine this later)
- curl http://localhost:8080/ping
- curl -X POST http://localhost:8080/user -d '{"firstName": "Britany", "lastName": "Meadows", "username": "britany123"}'
- curl http://localhost:8080/user/britany123
- curl -X PUT http://localhost:8080/user -d '{"firstName": "Britany", "lastName": "Meadows", "username": "britany456"}'
- curl -X DELETE http://localhost:8080/user/britany123

### checking local database:
#### (expand and refine this later)
- psql postgres (or double click in Postgres App)
- list tables: \dt
- SELECT * FROM users WHERE username='britany123';
- SELECT username FROM users;
- etc.

### Typelevel projects that have a channel in discord:
- cats
- cats-effect
- cats-stm
- davenverse
- doobie
- finch
- fs2
- frameless
- fs2-grpc
- http4s
- jawn
- monix
- natchez
- scodec
- shapeless
- skunk
- scalacheck 
- kittens