# Tasks — Autumn Framework v1

## Phase 1: Build Green (M1)

### TASK-001 — Fix B-05: Java 17 Build
Status: Draft for Approval
Target: `pom.xml` (root + autumn-core)
Autonomy Level: Level 1 - Single Task

#### Objective
Fix the broken build by ensuring pom.xml correctly configures Java 17 as source/target.

#### Dependencies
None

#### Source Documents
- PRD §8 (B-05), §10 (dependencies)

#### Scope
- Adjust `<maven.compiler.source>` and `<maven.compiler.target>` to 17
- Check `<release>` if present
- Validate that `mvn clean test` compiles

#### Out Of Scope
- Changing dependencies
- Modifying Java code

#### Acceptance Criteria
- [ ] `mvn clean compile` runs without error
- [ ] `mvn clean test` compiles (tests may fail, but compilation succeeds)

#### Verification Commands
```bash
mvn clean compile -q
```

#### Allowed Files
- `pom.xml`
- `autumn-core/pom.xml`

#### Stop Conditions
- If Maven is not available, stop and ask for installation

---

## Phase 2: Bugs Fixed (M2)

### TASK-002 — Fix B-01: BeanRegistry.resolveType()
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/registry/BeanRegistry.java`
Autonomy Level: Level 1 - Single Task

#### Objective
Fix `getBean(EmailSender.class)` failing because registry does not map concrete classes that implement interfaces.

#### Dependencies
TASK-001

#### Source Documents
- PRD §8 (B-01), §6.2 (Bean Lookup)

#### Scope
- Adjust `resolveType()` to return the concrete class when the type is directly registered
- Maintain interface → impl resolution

#### Out Of Scope
- Changing public API
- Creating new classes

#### Acceptance Criteria
- [ ] `getBean(EmailSender.class)` returns an instance when EmailSender is a registered concrete class
- [ ] `getBean(ServiceInterface.class)` continues resolving to the impl

#### Verification Commands
```bash
mvn test -pl autumn-core -Dtest=BeanRegistryTest
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/registry/BeanRegistry.java`

#### Stop Conditions
- If existing tests break, stop and review

---

### TASK-003 — Fix B-02: LifecycleManager Double Scan
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/lifecycle/LifecycleManager.java`
Autonomy Level: Level 1 - Single Task

#### Objective
Fix `postConstruct()` scanning methods twice (double loop over getDeclaredMethods()).

#### Dependencies
TASK-001

#### Source Documents
- PRD §8 (B-02)

#### Scope
- Merge the two loops into a single loop over getDeclaredMethods()
- Execute @PostConstruct and register @PreDestroy in the same pass

#### Out Of Scope
- Changing shutdown() behavior
- Creating new classes

#### Acceptance Criteria
- [ ] @PostConstruct is called exactly once
- [ ] @PreDestroy is registered correctly
- [ ] Existing tests continue passing

#### Verification Commands
```bash
mvn test -pl autumn-core -Dtest=LifecycleManagerTest
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/lifecycle/LifecycleManager.java`

#### Stop Conditions
- If shutdown behavior changes, stop

---

### TASK-004 — Fix B-03: ClassPathScanner Silent Failures
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/utils/ClassPathScanner.java`
Autonomy Level: Level 1 - Single Task

#### Objective
Fix `tryRegisterComponent()` swallowing exceptions with `catch (Throwable ignored)`.

#### Dependencies
TASK-001

#### Source Documents
- PRD §8 (B-03)

#### Scope
- Catch only `ClassNotFoundException` and `NoClassDefFoundError` (expected missing dependency errors)
- Log other exceptions at WARN level

#### Out Of Scope
- Changing scan logic
- Creating new classes

#### Acceptance Criteria
- [ ] Classes with missing dependencies are silently ignored
- [ ] Other exceptions are logged at WARN
- [ ] Existing tests continue passing

#### Verification Commands
```bash
mvn test -pl autumn-core -Dtest=ClassPathScannerTest
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/utils/ClassPathScanner.java`

#### Stop Conditions
- If scan behavior changes, stop

---

### TASK-005 — Fix B-04: BeanFactory Prototype Circular Dep
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/context/BeanFactory.java`
Autonomy Level: Level 1 - Single Task

#### Objective
Detect circular dependencies in prototype beans (currently only detected in singletons).

#### Dependencies
TASK-001

#### Source Documents
- PRD §8 (B-04), §6.1 (bootstrap flow)

#### Scope
- Ensure `creating.add(type)` is checked before creating a prototype (already exists, but needs verification for prototype)
- Verify `creating.remove(type)` is called in finally block

#### Out Of Scope
- Changing singleton cache logic
- Creating new classes

#### Acceptance Criteria
- [ ] Circular dependency in prototype throws CircularDependencyException
- [ ] Message includes the class name

#### Verification Commands
```bash
mvn test -pl autumn-core -Dtest=BeanFactoryTest
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/context/BeanFactory.java`

#### Stop Conditions
- If singleton tests break, stop

---

## Phase 3: New Features (M3)

### TASK-006 — Refactor BeanDefinition to Record
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/registry/BeanDefinition.java`
Autonomy Level: Level 1 - Single Task

#### Objective
Convert BeanDefinition from class to record, adding `qualifierNames` field for @Qualifier support.

#### Dependencies
TASK-001

#### Source Documents
- PRD §7.5, §6.1

#### Scope
- Convert to `record BeanDefinition(Class<?> type, String name, Constructor<?> constructor, List<Class<?>> constructorParamTypes, boolean singleton, List<String> qualifierNames)`
- Add `qualifierNames` field (default: empty list)
- Update all references

#### Out Of Scope
- Changing registry behavior
- Implementing @Qualifier (T-08)

#### Acceptance Criteria
- [ ] BeanDefinition is a record
- [ ] All existing tests continue passing
- [ ] qualifierNames field is accessible

#### Verification Commands
```bash
mvn test -pl autumn-core
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/registry/BeanDefinition.java`
- `autumn-core/src/main/java/io/autumn/core/registry/BeanRegistry.java`
- `autumn-core/src/main/java/io/autumn/core/context/BeanFactory.java`

#### Stop Conditions
- If more than 3 files need changes, stop

---

### TASK-007 — Extract DependencyResolver
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/injection/DependencyResolver.java`
Autonomy Level: Level 1 - Single Task

#### Objective
Extract constructor dependency resolution from BeanFactory into a dedicated class, preparing for @Qualifier support.

#### Dependencies
TASK-002

#### Source Documents
- PRD §6.1 (bootstrap flow), §7.5 (package structure)

#### Scope
- Create `io.autumn.core.injection.DependencyResolver`
- Move `resolveDependencies()` from BeanFactory to DependencyResolver
- DependencyResolver receives `BeanRegistry` and `BeanFactory` via constructor
- BeanFactory delegates to DependencyResolver

#### Out Of Scope
- Implementing @Qualifier (T-08)
- Changing resolution behavior

#### Acceptance Criteria
- [ ] DependencyResolver created with constructor receiving registry and factory
- [ ] resolveDependencies() works as before
- [ ] BeanFactory delegates to DependencyResolver
- [ ] Existing tests continue passing

#### Verification Commands
```bash
mvn test -pl autumn-core
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/injection/DependencyResolver.java` (new)
- `autumn-core/src/main/java/io/autumn/core/context/BeanFactory.java`

#### Stop Conditions
- If more than 2 files need changes, stop

---

### TASK-008 — Implement @Qualifier
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/annotations/Qualifier.java`, `autumn-core/src/main/java/io/autumn/core/injection/DependencyResolver.java`
Autonomy Level: Level 2 - Work Package

#### Objective
Implement @Qualifier for named injection in constructors.

#### Dependencies
TASK-007

#### Source Documents
- PRD §7.4, US-04, F-05, Q-01

#### Scope
- Create `@Qualifier` annotation (Target: PARAMETER)
- Update DependencyResolver to read @Qualifier on parameters
- Update BeanRegistry to index by qualifier name
- Update BeanDefinition to include qualifierNames

#### Out Of Scope
- @Qualifier on fields (Q-01: constructor only)
- Changing @Primary behavior

#### Acceptance Criteria
- [ ] `@Qualifier("email")` resolves to bean named "email"
- [ ] Without @Qualifier and without @Primary throws NoUniqueBeanException
- [ ] @Primary continues working as fallback

#### Verification Commands
```bash
mvn test -pl autumn-core
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/annotations/Qualifier.java` (new)
- `autumn-core/src/main/java/io/autumn/core/injection/DependencyResolver.java`
- `autumn-core/src/main/java/io/autumn/core/registry/BeanRegistry.java`
- `autumn-core/src/main/java/io/autumn/core/registry/BeanDefinition.java`

#### Stop Conditions
- If more than 4 files need changes, stop

---

### TASK-009 — Implement getBean(String)
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/context/AutumnContext.java`, `autumn-core/src/main/java/io/autumn/core/context/ApplicationContext.java`
Autonomy Level: Level 1 - Single Task

#### Objective
Implement bean lookup by name (`getBean(String name)`).

#### Dependencies
TASK-002

#### Source Documents
- PRD §6.2 (Bean Lookup), §7.3, US-08, F-10, Q-02

#### Scope
- Add `getBean(String name)` to ApplicationContext interface
- Implement in AutumnContext: search by @Component value, then by simple name
- Update BeanRegistry with `getByName(String)` and `nameIndex`

#### Out Of Scope
- Changing getBean(Class) behavior

#### Acceptance Criteria
- [ ] `getBean("MyService")` returns managed instance
- [ ] Searches @Component value first, then simple name
- [ ] Non-existent bean throws BeanNotFoundException

#### Verification Commands
```bash
mvn test -pl autumn-core
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/context/ApplicationContext.java`
- `autumn-core/src/main/java/io/autumn/core/context/AutumnContext.java`
- `autumn-core/src/main/java/io/autumn/core/registry/BeanRegistry.java`

#### Stop Conditions
- If more than 3 files need changes, stop

---

### TASK-010 — Typed Exceptions (3 classes)
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/exceptions/`
Autonomy Level: Level 1 - Single Task

#### Objective
Create 3 unchecked exception classes with actionable messages.

#### Dependencies
TASK-001

#### Source Documents
- PRD §9, US-09, F-11

#### Scope
- Create `BeanNotFoundException` extends RuntimeException
- Create `CircularDependencyException` extends RuntimeException
- Create `NoUniqueBeanException` extends RuntimeException
- Each with constructor(String message) and constructor(String message, Throwable cause)
- Replace existing IllegalStateException with typed exceptions

#### Out Of Scope
- Changing exception behavior
- Creating complex hierarchy

#### Acceptance Criteria
- [ ] 3 exception classes created
- [ ] BeanNotFoundException used in getBean when bean does not exist
- [ ] CircularDependencyException used in BeanFactory
- [ ] NoUniqueBeanException used in BeanRegistry

#### Verification Commands
```bash
mvn test -pl autumn-core
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/exceptions/BeanNotFoundException.java` (new)
- `autumn-core/src/main/java/io/autumn/core/exceptions/CircularDependencyException.java` (new)
- `autumn-core/src/main/java/io/autumn/core/exceptions/NoUniqueBeanException.java` (new)
- `autumn-core/src/main/java/io/autumn/core/context/BeanFactory.java`
- `autumn-core/src/main/java/io/autumn/core/registry/BeanRegistry.java`

#### Stop Conditions
- If more than 5 files need changes, stop

---

### TASK-011 — Internal Logger
Status: Draft for Approval
Target: `autumn-core/src/main/java/io/autumn/core/utils/AutumnLogger.java`
Autonomy Level: Level 1 - Single Task

#### Objective
Create internal logger with [AUTUMN] prefix and INFO/WARN/ERROR levels.

#### Dependencies
TASK-001

#### Source Documents
- PRD §7.1, US-10, F-12

#### Scope
- Create `AutumnLogger` with static methods `info()`, `warn()`, `error()`
- [AUTUMN] prefix on all messages
- Replace existing System.out/err with the logger

#### Out Of Scope
- External logging library dependencies
- Log level configuration

#### Acceptance Criteria
- [ ] AutumnLogger created with info/warn/error
- [ ] [AUTUMN] prefix present on all messages
- [ ] System.out/err replaced with logger

#### Verification Commands
```bash
mvn test -pl autumn-core
```

#### Allowed Files
- `autumn-core/src/main/java/io/autumn/core/utils/AutumnLogger.java` (new)
- `autumn-core/src/main/java/io/autumn/core/Autumn.java`
- `autumn-core/src/main/java/io/autumn/core/context/BeanFactory.java`
- `autumn-core/src/main/java/io/autumn/core/lifecycle/LifecycleManager.java`

#### Stop Conditions
- If more than 4 files need changes, stop

---

## Phase 4: Tests + Docs (M4)

### TASK-012 — Additional Tests
Status: Draft for Approval
Target: `autumn-core/src/test/java/io/autumn/core/`
Autonomy Level: Level 2 - Work Package

#### Objective
Add tests for all new features and bug fixes, achieving 100% coverage.

#### Dependencies
TASK-008, TASK-009, TASK-010

#### Source Documents
- PRD §9, §14 (Q-05)

#### Scope
- Tests for @Qualifier (unit)
- Tests for getBean(String) (unit + integration)
- Tests for exceptions (unit)
- Tests for AutumnLogger (unit)
- Tests for bugs B-01 to B-05 (regression)
- Configure JaCoCo via Maven plugin

#### Out Of Scope
- Performance tests (benchmark is a separate task)
- End-to-end integration tests

#### Acceptance Criteria
- [ ] All tests pass
- [ ] Coverage ≥ 90% (target 100%)
- [ ] JaCoCo configured and generating report

#### Verification Commands
```bash
mvn test -pl autumn-core
mvn jacoco:report -pl autumn-core
```

#### Allowed Files
- `autumn-core/src/test/java/io/autumn/core/**/*.java`
- `autumn-core/pom.xml` (JaCoCo plugin only)

#### Stop Conditions
- If coverage < 80%, stop and review

---

### TASK-013 — README
Status: Draft for Approval
Target: `/README.md`
Autonomy Level: Level 1 - Single Task

#### Objective
Create README with description, features, examples, and build instructions.

#### Dependencies
All previous tasks

#### Source Documents
- PRD §5 (US-11), F-13

#### Scope
- Project description
- Implemented features
- Working code example
- Build instructions
- Annotations list

#### Out Of Scope
- Full API documentation
- Contributing guide

#### Acceptance Criteria
- [ ] README exists and is readable
- [ ] Code example compiles and runs
- [ ] Build instructions are correct

#### Verification Commands
```bash
cat README.md
```

#### Allowed Files
- `README.md`

#### Stop Conditions
- If example does not compile, stop
