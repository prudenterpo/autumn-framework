Feature: Bean lookup and container errors
  As a developer using Autumn
  I want to find beans by type or name and get typed failures with useful messages
  So that I can use the context without guessing what went wrong

  Scenario: getBean by concrete class
    Given a registered concrete bean
    When I call getBean with that class
    Then I receive the managed instance

  Scenario: getBean by interface
    Given an interface with a registered implementation
    When I call getBean with the interface
    Then I receive the implementation

  Scenario: getBean by @Component value
    Given a bean with @Component("email")
    When I call getBean("email")
    Then I receive that instance

  Scenario: getBean by simple name when value is empty
    Given a @Component with no value whose simple name is OrderService
    When I call getBean("OrderService")
    Then I receive that instance

  Scenario: simple name still resolves when a custom value is set
    Given a bean @Component("email") whose simple name is EmailSender
    When I call getBean("EmailSender")
    Then I receive that EmailSender instance

  Scenario: missing bean by type
    When I call getBean with an unregistered type
    Then it throws BeanNotFoundException
    And the message contains the type name

  Scenario: missing bean by name
    When I call getBean("unknown")
    Then it throws BeanNotFoundException
    And the message contains "unknown"

  Scenario: containsBean for an unknown type does not throw
    When I call containsBean with an unregistered type
    Then the result is false
    And no NullPointerException is thrown

  Scenario: close is on ApplicationContext
    Given a started context
    When I call close through the ApplicationContext interface
    Then singleton shutdown runs

  Scenario: internal logger uses the [AUTUMN] prefix
    When the container emits info, warn, or error
    Then each line starts with "[AUTUMN]"

  Scenario: core does not write straight to System.out
    Given bootstrap of a package with components
    Then framework messages go through AutumnLogger
    And autumn-core does not call System.out or System.err
