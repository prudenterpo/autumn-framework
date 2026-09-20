# Autumn Core v1

Source of truth: `spec/features/*.feature`.
If behavior changes, change the `.feature` first. No spec-kit, no task files, no harness.

## How to implement with an AI agent

1. Take the next phase below.
2. Write (or adjust) the JUnit test with `@DisplayName` equal to the scenario name.
3. Make the scenario pass. Do not add Cucumber in this v1: Gherkin is the spec, JUnit is the runner.
4. One commit per phase. Do not add process docs to "organize the work".

## Language and git

Chat language does not set artifact language. Slack (or any review thread) may be Portuguese. **Everything that lands in git is English.** No exceptions for "the user wrote in Portuguese".

| Surface | Language | Rule |
|---|---|---|
| `spec/`, README, LICENSE notes, code comments | English | Spec already says this; keep it |
| Identifiers, annotations, log lines | English / code names | Match the type in source |
| Commit subject and body | English | Conventional Commits, imperative mood |
| PR title and description | English | Same standard as commits; fill the PR template |

Commits:

```
type(scope): short summary
```

- `type`: `feat` `fix` `test` `docs` `refactor` `build`
- `scope`: area (`lifecycle`, `factory`, `scanner`, `context`, `spec`, `examples`)
- Subject: imperative, lowercase after the colon, no period, ≤ 72 chars
- Body (optional): why, not how. English. No Slack dump.
- One commit per phase. Do not mix a feature with formatting or extra docs.

Examples:

```
fix(lifecycle): skip prototypes in PreDestroy tracking
feat(context): resolve beans by component value
docs(spec): add English git conventions
```

PRs:

- Title is an English Conventional Commit subject (what merged, not "updates" or "ajustes").
- Body uses `.github/pull_request_template.md`: what changed, which Gherkin scenarios, how to verify.
- Do not paste the Slack thread. Do not write the description in Portuguese.

Reject your own commit or PR if any user-facing git text is not English. Rewrite it before push.

## Scope

Everything still needed for a working v1:

- Land the lifecycle patch that currently lives only on `develop` (single method scan + prototypes skipped for `@PreDestroy`).
- Fix prototype eager-init at bootstrap, reentrant `BeanFactory`, scanner swallowing unexpected errors, `containsBean` NPE, `close()` missing from the interface, unused `@Component.value()`.
- Finish the API: `getBean(String)`, `@Qualifier`, `DependencyResolver`, typed exceptions, `AutumnLogger`.
- Close with JaCoCo, README, and MIT LICENSE (already declared in the POM).

Out of this v1: field/setter injection, multiple packages in `start()`, AOP, XML, scopes other than singleton/prototype.

## Phases

Each phase lists the scenarios that must go green. Order matters: each phase assumes the previous one.

### 0. Align lifecycle with develop

`LifecycleManager.postConstruct` scans methods once. Prototypes are never added to the `@PreDestroy` list. A failing `@PostConstruct` is logged and does not take down other beans.

Scenarios: `lifecycle.feature` → single scan, prototype not destroyed, isolated `@PostConstruct` failure.

### 1. Stable container

- `Autumn.start` instantiates singletons only. Prototypes are created on `getBean`.
- `BeanFactory` must not create beans via `ConcurrentHashMap.computeIfAbsent` (that map is not reentrant). A constructor-injected singleton graph must boot.
- `containsBean` does not throw NPE for an unknown type.
- `ApplicationContext.close()` exists and runs shutdown.
- Scanner: `ClassNotFoundException` / `NoClassDefFoundError` are ignored; any other load error is logged at WARN.

Scenarios: `discovery.feature` (scan errors), `injection.feature` (reentrant graph), `lifecycle.feature` (prototype not created at boot), `lookup-and-errors.feature` (`close` and `containsBean`).

### 2. Explicit failures and logging

Three unchecked types: `BeanNotFoundException`, `CircularDependencyException`, `NoUniqueBeanException`. Messages include the type, name, or implementations.

`AutumnLogger` prefixes `[AUTUMN]` and exposes `info` / `warn` / `error`. Core code stops talking through `System.out` / `System.err`.

Scenarios: `lookup-and-errors.feature` (exceptions and logger) plus circular / duplicate-impl cases in `injection.feature`.

### 3. Lookup by name

`@Component("email")` becomes the bean name. With an empty value, the name is the simple class name. `getBean(String)` looks up the value first, then the simple name. A miss throws `BeanNotFoundException`.

Scenarios: `lookup-and-errors.feature` → lookup by type, by value, by simple name, miss.

### 4. Qualifier

`DependencyResolver` moves out of `BeanFactory`. `@Qualifier` is constructor-parameter only. Qualifier wins over `@Primary`. Several implementations with neither qualifier nor primary throw `NoUniqueBeanException`. `BeanDefinition` gains `qualifierNames`.

Scenarios: `injection.feature` → qualifier, qualifier vs primary, several implementations.

### 5. Close v1

- JaCoCo on `autumn-core`, target ≥ 90%.
- Root README: what it is, how to build, annotations, a running example.
- MIT LICENSE.
- The example still shows constructor injection, `@Primary`, prototype, and lifecycle, and no longer creates a prototype at boot.

Scenarios: all green + `mvn test` + JaCoCo report.

## Quick map

| Feature | Code that should change |
|---|---|
| `discovery.feature` | `ClassPathScanner`, `Autumn` |
| `injection.feature` | `BeanFactory`, `BeanRegistry`, `BeanDefinition`, `DependencyResolver`, `@Qualifier` |
| `lifecycle.feature` | `LifecycleManager`, `Autumn.start`, `ApplicationContext.close` |
| `lookup-and-errors.feature` | `AutumnContext`, `BeanRegistry` (name index), exceptions, `AutumnLogger` |

## Done when

- Every scenario has a matching JUnit test and it passes.
- Example: `mvn -pl autumn-examples -am exec` (or `Application.main`) shows two distinct `AuditLogger` instances and no third instance created at boot.
- README describes the shipped scope, not the wished-for scope.
