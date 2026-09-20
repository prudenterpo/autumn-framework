Feature: Bean lifecycle
  As a developer using Autumn
  I want @PostConstruct and @PreDestroy to follow bean scope
  So that the container initializes and shuts down only what it actually manages

  Scenario: @PostConstruct runs after injection
    Given a singleton with a dependency and a @PostConstruct method
    When the bean is created
    Then the dependency is already populated
    And @PostConstruct ran exactly once

  Scenario: Lifecycle methods are scanned once
    Given a bean with both @PostConstruct and @PreDestroy on the same type
    When postConstruct is called
    Then each method on the type is inspected once
    And @PostConstruct ran once

  Scenario: Singleton @PreDestroy runs on close
    Given a singleton with @PreDestroy
    When the context is closed
    Then @PreDestroy ran

  Scenario: Prototype is not tracked for @PreDestroy
    Given a prototype with @PreDestroy
    When I request the bean and then close the context
    Then @PreDestroy did not run

  Scenario: Prototype is not created at bootstrap
    Given a package with a singleton and a prototype
    When I call Autumn.start
    Then the singleton already exists
    And no prototype instance was created
    And getBean of the prototype creates the first instance on demand

  Scenario: @PostConstruct failure does not take down other beans
    Given two singletons in the same package and one @PostConstruct that throws
    When I call Autumn.start
    Then the failure is logged through AutumnLogger
    And the other singleton is still available from the context
