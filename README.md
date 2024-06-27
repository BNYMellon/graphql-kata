# GraphQL Composers Kata

## What is GraphQL?

[GraphQL](https://graphql.org) is a typed query language for APIs. It includes a specification for making requests as
well as a runtime environment for handling incoming requests. It includes many robust features including, but not
limited to:

- Extensible, statically typed [schemas](https://spec.graphql.org/October2021/#sec-Type-System)
- Client-side schema [introspection](https://spec.graphql.org/October2021/#sec-Introspection)
- First class [error handling](https://spec.graphql.org/October2021/#sec-Errors) and partial results
- Built-in [deprecation mechanism](https://spec.graphql.org/October2021/#sec-Field-Deprecation)
- Real-time [subscriptions](https://spec.graphql.org/October2021/#sec-Subscription)

GraphQL has implementations in [many languages](https://graphql.org/code/#language-support), can be served over
different protocols ([typically HTTP](https://graphql.org/learn/serving-over-http)), and also
facilitates [powerful tooling](https://graphql.org/code/#generic-tools). See
the [introduction to GraphQL](https://graphql.org/learn) for a basic overview of the specification and its usage.

## GraphQL and Java

This kata is built and run with Java 17 and uses [GraphQL Java](https://www.graphql-java.com/) as its GraphQL
implementation for the basic `query`
and `mutation` packages. GraphQL Java is a framework-agnostic runtime that provides an API for defining GraphQL servers
which can be integrated with other technologies such
as [Spring Boot](https://docs.spring.io/spring-graphql/docs/1.0.0/reference/html/).

### JS GraphQL plugin for IntelliJ

JS GraphQL is a very useful plugin for GraphQL that is available from the IntelliJ marketplace.

The `graphql-kata` module includes a configuration file for JS GraphQL named `.graphqlconfig`; JS GraphQL will
automatically pick up this configuration file. After installing JS GraphQL, the plugin will detect schema files
contained in the project, which allows IntelliJ to provide syntax highlighting and completions in `*.graphql` files.

See the [JS GraphQL](https://jimkyndemeyer.github.io/js-graphql-intellij-plugin/docs/developer-guide) documentation for
more information.

## Kata Packages

The GraphQL Kata is separated into a few modules. `graphql-kata-exercises` contains the kata exercises that need to be
solved. `graphql-kata-solutions` contains the same exercises, but they are already solved. This can be referenced when stuck, or used to double-check solutions. The `graphql-spring-boot-example` module is [explained below](#spring-boot-example).

The kata is divided into a `query` package and a `mutation` package, the main two types of GraphQL operations. The packages should be done in order: `query` first and `mutation` second. Some of the `query` solutions will be used in the `mutation` tests.

### Query Package

GraphQL queries are read-only operations sent by a client to retrieve data from a GraphQL server. Queries allow clients
to request, aggregate, and filter server-side data and receive a JSON formatted response.

The package `bny.jpe.graphql.kata.query` in the `src/test` directory contains a series of failing query related
unit tests designed to teach the basics of making GraphQL queries. The `src/test/resources/queries` directory contains
several `*.graphql` files which need to be modified in order to make the tests pass. The schema for the query tests can
be found in `graphql-composers-domain/src/main/resources/schema.graphqls`.

### Mutation Package

GraphQL mutations represent the "C" (create), "U" (update), and "D" (delete) operations of the standard CRUD model.
Mutations allow clients to perform these operations against the server and receive customizable JSON formatted responses
in the same way they would from a query operation. Once you are familiar with making queries you are well on your way to
understanding mutations and many of the same concepts and features still apply.

In the `src/test` directory there is a `bny.jpe.graphql.kata.mutation` package which contains failing unit tests
designed to teach the basics of performing GraphQL mutations. The `src/test/resources/mutation` directory contains
several `*.graphql` files which need to be modified in order to make the tests pass. The schema for the mutation tests
is the same as for the query tests and can be found in `graphql-composers-domain/src/main/resources/schema.graphqls`.

## Spring Boot Example
There is a sample application contained in the `graphql-spring-boot-example` module. The application is a Spring Boot server using [Spring for GraphQL](https://spring.io/projects/spring-graphql). It continues using the same composers domain as the kata. You can run the server and execute operations against it to continue learning about the query language or browse through the code and learn how to spin up your own server.

The example app also exposes some tooling options that can be explored: 
- [GraphiQL](https://github.com/graphql/graphiql) is a browser based client with hints, completions, formatting, and a schema browser built in. It can be found at http://localhost:8080/graphiql while the sample application is running.

## Links

- [How to GraphQL](https://www.howtographql.com)
- [Official GraphQL Spec as of October 2021](https://spec.graphql.org/October2021/)
