Feature: Constructor injection
  As a developer using Autumn
  I want constructor dependencies resolved with @Primary and @Qualifier
  So that I can wire the graph without calling new

  Scenario: Injects a dependency through the single constructor
    Given a bean whose constructor requires another registered bean
    When I request the bean by type
    Then the dependency is populated

  Scenario: Multiple constructors require @Inject
    Given a @Component with more than one constructor and no @Inject
    When the container registers the class
    Then registration fails with a message asking for @Inject

  Scenario: Interface with a single implementation
    Given an interface with exactly one @Component implementation
    When I request the interface
    Then I receive that implementation

  Scenario: Several implementations with neither @Primary nor @Qualifier
    Given an interface with two @Component implementations and no @Primary
    When the container registers the second one
    Then it throws NoUniqueBeanException
    And the message names both implementations

  Scenario: @Primary selects the implementation
    Given an interface with two @Component implementations and one marked @Primary
    When I request the interface
    Then I receive the @Primary implementation

  Scenario: Two @Primary on the same interface
    Given two @Component @Primary types for the same interface
    When the container registers the second one
    Then it throws NoUniqueBeanException

  Scenario: Singleton is the default instance
    Given a bean with no @Scope
    When I request the type twice
    Then I receive the same instance

  Scenario: Prototype returns a new instance
    Given a bean with @Scope("prototype")
    When I request the type twice
    Then I receive different instances

  Scenario: Singleton graph boots without deadlock
    Given singleton A whose constructor requires singleton B
    When I request A
    Then A and B are created
    And a later getBean(A) returns the same A

  Scenario: Circular dependency among singletons
    Given singleton A requiring B and singleton B requiring A
    When I request A
    Then it throws CircularDependencyException
    And the message names the class being created

  Scenario: Circular dependency among prototypes
    Given prototype A requiring B and prototype B requiring A
    When I request A
    Then it throws CircularDependencyException

  Scenario: @Qualifier selects by name
    Given EmailSender with @Component("email") and SmsSender with @Component("sms")
    And a constructor parameter annotated @Qualifier("email")
    When the bean that owns that constructor is created
    Then the dependency is EmailSender

  Scenario: @Qualifier wins over @Primary
    Given one @Primary implementation and another named "sms"
    And a constructor parameter annotated @Qualifier("sms")
    When the bean is created
    Then the dependency is the "sms" implementation
    And not the @Primary one
