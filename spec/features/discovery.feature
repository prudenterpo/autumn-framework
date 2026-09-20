Feature: Component discovery
  As a developer using Autumn
  I want the container to find only @Component classes in the base package
  So that I do not register beans by hand

  Scenario: Finds a class annotated with @Component
    Given a class annotated with @Component in the base package
    When the container scans that package
    Then that class is included in the component set

  Scenario: Ignores a class without @Component
    Given a class in the base package with no @Component
    When the container scans that package
    Then that class is not included in the component set

  Scenario: Class with a missing dependency is skipped
    Given a .class in the package whose load throws ClassNotFoundException or NoClassDefFoundError
    When the container scans
    Then that class is skipped
    And scanning of the remaining classes continues

  Scenario: Unexpected scan failure is logged at WARN
    Given a .class whose load throws an error that is not ClassNotFoundException or NoClassDefFoundError
    When the container scans
    Then AutumnLogger emits WARN with the class name
    And scanning of the remaining classes continues

  Scenario: Scan covers directories and JARs
    Given the base package is present as a classes directory or inside a JAR
    When the container scans
    Then @Component classes from both formats are found

  Scenario: start accepts a single package
    Given a valid base package
    When I call Autumn.start with that package
    Then only that package is scanned
