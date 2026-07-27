# Architecture Spec — Autumn Core v1

## Overview

Architectural spec for the `autumn-core` module: a lightweight IoC/DI container for Java 17+. Covers 13 existing classes + 5 new classes to be created. Reference: PRD §7.5, §8.

## Package Structure

```
io.autumn.core
├── Autumn.java                    # Bootstrap entry point
├── annotations/                   # @Component, @Primary, @Qualifier, @Scope
├── context/                       # ApplicationContext (interface), AutumnContext, BeanFactory
├── registry/                      # BeanDefinition (record), BeanRegistry
├── injection/                     # DependencyResolver (new)
├── lifecycle/                     # LifecycleManager
├── utils/                         # ClassPathScanner, AutumnLogger (new)
└── exceptions/                    # BeanNotFound, CircularDependency, NoUniqueBean (new)
```

## Classes

### Autumn
- **Package:** `io.autumn.core`
- **Type:** class (final, utility)
- **Purpose:** Bootstrap entry point — F-01, F-02, F-03
- **Dependencies:** ClassPathScanner, BeanRegistry, BeanFactory, LifecycleManager, AutumnContext
- **Public Methods:** `static AutumnContext start(String basePackage)`
- **Fields:** (none — stateless)
- **Lifecycle:** Called by user, orchestrates scan → register → instantiate → context

### ApplicationContext (interface)
- **Package:** `io.autumn.core.context`
- **Type:** interface
- **Purpose:** Public contract for bean access — F-09, F-10
- **Dependencies:** BeanRegistry
- **Public Methods:** `<T> T getBean(Class<T> type)`, `<T> T getBean(String name)`, `void close()`, `BeanRegistry getRegistry()`
- **Fields:** (none)
- **Lifecycle:** Returned by `Autumn.start()`, used by framework user

### AutumnContext
- **Package:** `io.autumn.core.context`
- **Type:** class (implements ApplicationContext)
- **Purpose:** Concrete container implementation — F-09, F-10
- **Dependencies:** BeanRegistry, BeanFactory, LifecycleManager
- **Public Methods:** inherits `getBean(Class)`, `getBean(String)`, `close()`; `getRegistry()`
- **Fields:** `BeanRegistry registry`, `BeanFactory factory`, `LifecycleManager lifecycle`
- **Lifecycle:** Created by Autumn, alive until `close()`

### BeanFactory
- **Package:** `io.autumn.core.context`
- **Type:** class
- **Purpose:** Bean creation and caching — F-02, F-06, F-08
- **Dependencies:** BeanRegistry, LifecycleManager
- **Public Methods:** `<T> T getOrCreateBean(Class<T> type)`, `boolean containsBean(Class<?> type)`
- **Fields:** `Map<Class<?>, Object> singletons`, `Set<Class<?>> creating`
- **Lifecycle:** Instantiated at bootstrap, used throughout context lifetime

### BeanRegistry
- **Package:** `io.autumn.core.registry`
- **Type:** class
- **Purpose:** Bean definition registration and resolution — F-03, F-04, F-05
- **Dependencies:** BeanDefinition, @Primary, @Scope, @Qualifier
- **Public Methods:** `void register(BeanDefinition)`, `void registerClass(Class<?>)`, `BeanDefinition getDefinition(Class<?>)`, `Class<?> resolveType(Class<?>)`, `Collection<BeanDefinition> getAll()`, `boolean contains(Class<?>)`, `BeanDefinition getByName(String)`
- **Fields:** `Map<Class<?>, BeanDefinition> definitions`, `Map<Class<?>, Class<?>> interfaceToImpl`, `Map<String, BeanDefinition> nameIndex`
- **Lifecycle:** Populated at bootstrap, consulted by BeanFactory

### BeanDefinition
- **Package:** `io.autumn.core.registry`
- **Type:** record (refactored from class — T-06)
- **Purpose:** Immutable bean metadata — F-06
- **Dependencies:** (none)
- **Public Methods:** `type()`, `name()`, `constructor()`, `constructorParamTypes()`, `isSingleton()`
- **Fields:** (record components: `Class<?> type`, `String name`, `Constructor<?> constructor`, `List<Class<?>> constructorParamTypes`, `boolean singleton`, `List<String> qualifierNames`)
- **Lifecycle:** Created at registration, immutable

### LifecycleManager
- **Package:** `io.autumn.core.lifecycle`
- **Type:** class
- **Purpose:** Executes @PostConstruct/@PreDestroy — F-07
- **Dependencies:** (none — uses reflection)
- **Public Methods:** `void postConstruct(Object bean)`, `void shutdown()`
- **Fields:** `List<Object> preDestroyCandidates`
- **Lifecycle:** Called by BeanFactory (postConstruct) and AutumnContext (shutdown)

### ClassPathScanner
- **Package:** `io.autumn.core.utils`
- **Type:** class
- **Purpose:** @Component discovery — F-01
- **Dependencies:** @Component
- **Public Methods:** `Set<Class<?>> findComponentClasses(String basePackage)`
- **Fields:** (none — stateless)
- **Lifecycle:** Used once at bootstrap

### DependencyResolver (new — T-07)
- **Package:** `io.autumn.core.injection`
- **Type:** class
- **Purpose:** Resolves constructor dependencies with @Qualifier support — F-05
- **Dependencies:** BeanRegistry, BeanFactory
- **Public Methods:** `Object[] resolveDependencies(List<Class<?>> paramTypes, Parameter[] parameters)`
- **Fields:** `BeanRegistry registry`, `BeanFactory factory`
- **Lifecycle:** Called by BeanFactory during bean creation

### AutumnLogger (new — T-11)
- **Package:** `io.autumn.core.utils`
- **Type:** class
- **Purpose:** Internal logging with [AUTUMN] prefix — F-12
- **Dependencies:** (none — uses System.out/err)
- **Public Methods:** `static void info(String message)`, `static void warn(String message)`, `static void error(String message, Throwable t)`
- **Fields:** (none — stateless)
- **Lifecycle:** Used by all framework classes

## Annotations

### @Component
- **Target:** TYPE
- **Retention:** RUNTIME
- **Attributes:** `String value() default ""`
- **Reference:** F-01

### @Primary
- **Target:** TYPE
- **Retention:** RUNTIME
- **Attributes:** (none)
- **Reference:** F-04

### @Scope
- **Target:** TYPE
- **Retention:** RUNTIME
- **Attributes:** `String value() default "singleton"`
- **Reference:** F-06

### @Qualifier (new — T-08)
- **Target:** PARAMETER
- **Retention:** RUNTIME
- **Attributes:** `String value()`
- **Reference:** F-05

## Exceptions (new — T-10)

### BeanNotFoundException
- **Extends:** RuntimeException
- **When:** Bean not found by type or name
- **Message format:** `"No bean found for type: {typeName}"` or `"No bean found with name: {name}"`

### CircularDependencyException
- **Extends:** RuntimeException
- **When:** Circular dependency detected during creation
- **Message format:** `"Circular dependency detected while creating: {typeName}"`

### NoUniqueBeanException
- **Extends:** RuntimeException
- **When:** Multiple implementations without @Primary and without @Qualifier
- **Message format:** `"Multiple implementations found for {interfaceName}: {impl1}, {impl2}. Use @Primary or @Qualifier."`

## Design Decisions

1. **Record for BeanDefinition (T-06):** Immutability and zero boilerplate. Rationale: class is purely a data carrier.
2. **Unchecked exceptions (T-10):** All exceptions extend RuntimeException. Rationale: container errors are unrecoverable, checked exceptions would add boilerplate for users.
3. **AutumnLogger with no external deps (T-11):** Uses System.out/err directly. Rationale: zero runtime dependencies is a PRD §7.1 requirement.
4. **@Qualifier constructor-only (Q-01):** Consistent with v1 scope. Rationale: field injection is an anti-pattern, v1 supports only constructor injection.
5. **getBean(String) dual lookup (Q-02):** Searches @Component value first, then simple name. Rationale: flexibility without breaking backward compatibility.
6. **Prototype without @PreDestroy (Q-03):** Prototype beans are not tracked. Rationale: lifecycle management for transient objects would be unnecessary overhead.
