# Test Plan — Autumn Core v1

## Test Categories

### Unit Tests
Individual tests per class, isolating dependencies with mocks or fixtures.

| Class | Test File | Coverage |
|---|---|---|
| BeanRegistry | `BeanRegistryTest.java` | Registration, resolution, @Primary, @Qualifier |
| BeanFactory | `BeanFactoryTest.java` | Creation, cache, circular dep, prototype |
| LifecycleManager | `LifecycleManagerTest.java` | @PostConstruct, @PreDestroy, shutdown |
| ClassPathScanner | `ClassPathScannerTest.java` | Directory scan, jar scan, errors |
| AutumnContext | `AutumnContextTest.java` | getBean(Class), getBean(String), close |
| DependencyResolver | `DependencyResolverTest.java` | Resolution with @Qualifier |
| AutumnLogger | `AutumnLoggerTest.java` | Output format, prefix |
| Autumn | `AutumnIntegrationTest.java` | Full bootstrap |

### Integration Tests
End-to-end tests verifying component integration.

| Scenario | File | What it verifies |
|---|---|---|
| Full bootstrap | `AutumnIntegrationTest.java` | Scan → register → inject → lifecycle |
| Multiple impls | `QualifierIntegrationTest.java` | @Primary + @Qualifier together |
| Shutdown | `ShutdownIntegrationTest.java` | @PreDestroy executed correctly |

## Test Cases

### TC-001 — Component Discovery
- **Feature:** F-01
- **Preconditions:** Class with @Component on classpath
- **Steps:**
  1. Call `ClassPathScanner.findComponentClasses("com.example")`
  2. Verify class is returned
- **Expected:** Set contains the annotated class
- **Type:** unit

### TC-002 — Non-Component Ignored
- **Feature:** F-01
- **Preconditions:** Class without @Component on classpath
- **Steps:**
  1. Call `findComponentClasses`
  2. Verify class is not returned
- **Expected:** Set does not contain non-annotated class
- **Type:** unit

### TC-003 — Constructor Injection
- **Feature:** F-02
- **Preconditions:** Bean with single constructor signature
- **Steps:**
  1. Register class with dependency
  2. Create bean via BeanFactory
- **Expected:** Bean created with dependency injected
- **Type:** unit

### TC-004 — Multiple Constructors Requires @Inject
- **Feature:** F-02
- **Preconditions:** Class with multiple constructors
- **Steps:**
  1. Register class without @Inject
- **Expected:** IllegalStateException thrown
- **Type:** unit

### TC-005 — Interface Resolution Single Impl
- **Feature:** F-03
- **Preconditions:** Interface with single implementation
- **Steps:**
  1. Register implementation
  2. getBean(interface.class)
- **Expected:** Returns implementation instance
- **Type:** unit

### TC-006 — Interface Resolution @Primary
- **Feature:** F-04
- **Preconditions:** Interface with multiple impls, one with @Primary
- **Steps:**
  1. Register impls, one with @Primary
  2. getBean(interface.class)
- **Expected:** Returns @Primary instance
- **Type:** unit

### TC-007 — NoUniqueBean Without @Primary
- **Feature:** F-04, F-11
- **Preconditions:** Interface with multiple impls, none with @Primary
- **Steps:**
  1. Register impls
  2. getBean(interface.class)
- **Expected:** NoUniqueBeanException thrown
- **Type:** unit

### TC-008 — @Qualifier Named Injection
- **Feature:** F-05
- **Preconditions:** Interface with multiple impls, one with @Component("email")
- **Steps:**
  1. Register impls
  2. Use constructor with `@Qualifier("email") EmailSender sender`
- **Expected:** Injects impl named "email"
- **Type:** unit

### TC-009 — @Qualifier Overrides @Primary
- **Feature:** F-05
- **Preconditions:** Interface with @Primary and @Qualifier pointing to different impls
- **Steps:**
  1. Use constructor with @Qualifier
- **Expected:** @Qualifier takes precedence
- **Type:** unit

### TC-010 — Singleton Default Scope
- **Feature:** F-06
- **Preconditions:** Bean without @Scope
- **Steps:**
  1. getBean twice
- **Expected:** Same instance
- **Type:** unit

### TC-011 — Prototype Scope
- **Feature:** F-06
- **Preconditions:** Bean with @Scope("prototype")
- **Steps:**
  1. getBean twice
- **Expected:** Different instances
- **Type:** unit

### TC-012 — Prototype No @PreDestroy
- **Feature:** F-06, F-07
- **Preconditions:** Prototype bean with @PreDestroy
- **Steps:**
  1. getBean (creates instance)
  2. close()
- **Expected:** @PreDestroy not called
- **Type:** unit

### TC-013 — @PostConstruct Called
- **Feature:** F-07
- **Preconditions:** Bean with @PostConstruct
- **Steps:**
  1. Create bean via BeanFactory
- **Expected:** @PostConstruct executed after injection
- **Type:** unit

### TC-014 — @PreDestroy On Close
- **Feature:** F-07
- **Preconditions:** Singleton bean with @PreDestroy
- **Steps:**
  1. Create bean
  2. Call context.close()
- **Expected:** @PreDestroy executed
- **Type:** unit

### TC-015 — Lifecycle Error Logging
- **Feature:** F-07
- **Preconditions:** Bean with @PostConstruct that throws exception
- **Steps:**
  1. Create bean
- **Expected:** Exception logged, other beans unaffected
- **Type:** unit

### TC-016 — Circular Dependency Detection
- **Feature:** F-08
- **Preconditions:** Two beans with circular dependency
- **Steps:**
  1. Register circular beans
  2. getBean
- **Expected:** CircularDependencyException with class names
- **Type:** unit

### TC-017 — getBean By Type
- **Feature:** F-09
- **Preconditions:** Bean registered
- **Steps:**
  1. getBean(MyService.class)
- **Expected:** Returns instance
- **Type:** unit

### TC-018 — getBean By Name
- **Feature:** F-10
- **Preconditions:** Bean registered
- **Steps:**
  1. getBean("MyService")
- **Expected:** Returns instance
- **Type:** unit

### TC-019 — getBean By @Component Value
- **Feature:** F-10
- **Preconditions:** Bean with @Component("custom")
- **Steps:**
  1. getBean("custom")
- **Expected:** Returns instance
- **Type:** unit

### TC-020 — getBean Not Found
- **Feature:** F-10, F-11
- **Preconditions:** No bean registered
- **Steps:**
  1. getBean("NonExistent")
- **Expected:** BeanNotFoundException
- **Type:** unit

### TC-021 — BeanNotFoundException Message
- **Feature:** F-11
- **Preconditions:** Bean not found
- **Steps:**
  1. getBean("X")
- **Expected:** Message includes name "X"
- **Type:** unit

### TC-022 — CircularDependencyException Message
- **Feature:** F-11
- **Preconditions:** Circular dependency
- **Steps:**
  1. getBean with circular beans
- **Expected:** Message includes class names
- **Type:** unit

### TC-023 — NoUniqueBeanException Message
- **Feature:** F-11
- **Preconditions:** Multiple impls without @Primary
- **Steps:**
  1. getBean(interface)
- **Expected:** Message includes implementation names
- **Type:** unit

### TC-024 — AutumnLogger Prefix
- **Feature:** F-12
- **Preconditions:** None
- **Steps:**
  1. Call AutumnLogger.info("test")
- **Expected:** Output contains "[AUTUMN] test"
- **Type:** unit

### TC-025 — Full Bootstrap Integration
- **Feature:** F-01, F-02, F-03, F-06, F-07
- **Preconditions:** Multiple beans with dependencies
- **Steps:**
  1. Autumn.start("com.example")
  2. Verify beans created
  3. Verify @PostConstruct executed
- **Expected:** Functional context, beans with injected dependencies
- **Type:** integration

### TC-026 — B-01 Regression: Concrete Class Lookup
- **Feature:** B-01
- **Preconditions:** BeanRegistry with concrete class
- **Steps:**
  1. getBean(ConcreteClass.class)
- **Expected:** Returns instance (not null)
- **Type:** regression

### TC-027 — B-02 Regression: Single Scan
- **Feature:** B-02
- **Preconditions:** Bean with @PostConstruct and @PreDestroy
- **Steps:**
  1. postConstruct(bean)
  2. Verify @PostConstruct called once
- **Expected:** Method executed exactly once
- **Type:** regression

### TC-028 — B-04 Regression: Prototype Circular Dep
- **Feature:** B-04
- **Preconditions:** Two prototype beans with circular dep
- **Steps:**
  1. getBean
- **Expected:** CircularDependencyException thrown
- **Type:** regression

## Coverage Targets

| Component | Target | Rationale |
|---|---|---|
| BeanRegistry | 100% | Framework core |
| BeanFactory | 100% | Framework core |
| LifecycleManager | 100% | Critical for lifecycle |
| ClassPathScanner | 100% | Framework core |
| AutumnContext | 100% | Public API |
| DependencyResolver | 100% | New feature |
| AutumnLogger | 100% | Simple, easy to cover |
| Autumn | 90%+ | Integration |
| **Total** | **≥ 90%** | PRD §14 |
