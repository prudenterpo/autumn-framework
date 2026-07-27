# API Contract — Autumn Core v1

## Public API

### Autumn (bootstrap)

```java
public final class Autumn {
    /**
     * Starts the IoC container by scanning the basePackage.
     * Registers all @Component classes, instantiates singletons, and returns a ready context.
     *
     * @param basePackage root package for scanning (e.g. "com.example.app")
     * @return AutumnContext ready for use
     * @throws IllegalArgumentException if basePackage is null or empty
     */
    public static AutumnContext start(String basePackage);
}
```

### ApplicationContext (interface)

```java
public interface ApplicationContext {
    /**
     * Looks up a bean by type.
     * Resolves interfaces to their registered implementation.
     *
     * @param type the desired bean type
     * @return managed instance
     * @throws BeanNotFoundException if no bean found for the type
     */
    <T> T getBean(Class<T> type);

    /**
     * Looks up a bean by name.
     * Searches @Component value first, then by simple class name.
     *
     * @param name the bean name
     * @return managed instance
     * @throws BeanNotFoundException if no bean found with the name
     */
    <T> T getBean(String name);

    /**
     * Closes the container, executing @PreDestroy on all singleton beans.
     */
    void close();

    /**
     * Returns the bean definition registry.
     */
    BeanRegistry getRegistry();
}
```

### AutumnContext (implementation)

```java
public class AutumnContext implements ApplicationContext {
    public AutumnContext(BeanRegistry registry, BeanFactory factory, LifecycleManager lifecycle);
    public <T> T getBean(Class<T> type);
    public <T> T getBean(String name);
    public void close();
    public BeanRegistry getRegistry();
}
```

### BeanFactory

```java
public class BeanFactory {
    public BeanFactory(BeanRegistry registry, LifecycleManager lifecycle);

    /**
     * Looks up or creates a bean by type.
     * Singletons are cached; prototypes create a new instance each time.
     *
     * @throws CircularDependencyException if circular dependency detected
     * @throws IllegalStateException if instantiation fails
     */
    public <T> T getOrCreateBean(Class<T> type);

    public boolean containsBean(Class<?> type);
}
```

### BeanRegistry

```java
public class BeanRegistry {
    public void register(BeanDefinition definition);
    public void registerClass(Class<?> clazz);
    public BeanDefinition getDefinition(Class<?> type);
    public BeanDefinition getByName(String name);
    public Class<?> resolveType(Class<?> type);
    public Collection<BeanDefinition> getAll();
    public boolean contains(Class<?> type);
}
```

### BeanDefinition

```java
public record BeanDefinition(
    Class<?> type,
    String name,
    Constructor<?> constructor,
    List<Class<?>> constructorParamTypes,
    boolean singleton,
    List<String> qualifierNames
) {}
```

### LifecycleManager

```java
public class LifecycleManager {
    public void postConstruct(Object bean);
    public void shutdown();
}
```

### ClassPathScanner

```java
public class ClassPathScanner {
    public Set<Class<?>> findComponentClasses(String basePackage);
}
```

### DependencyResolver (new)

```java
public class DependencyResolver {
    public DependencyResolver(BeanRegistry registry, BeanFactory factory);

    /**
     * Resolves constructor dependencies with @Qualifier support.
     *
     * @param paramTypes parameter types
     * @param parameters Parameter objects (to read @Qualifier)
     * @return array of resolved instances
     * @throws NoUniqueBeanException if multiple impls without @Primary/@Qualifier
     * @throws BeanNotFoundException if dependency not found
     */
    public Object[] resolveDependencies(List<Class<?>> paramTypes, java.lang.reflect.Parameter[] parameters);
}
```

### AutumnLogger (new)

```java
public final class AutumnLogger {
    public static void info(String message);
    public static void warn(String message);
    public static void error(String message, Throwable t);
}
```

## Annotations

### @Component

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    /** Custom bean name. Default: simple class name. */
    String value() default "";
}
```

### @Primary

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Primary {}
```

### @Scope

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    /** "singleton" (default) or "prototype". */
    String value() default "singleton";
}
```

### @Qualifier (new)

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    /** Name of the bean to inject. */
    String value();
}
```

## Exceptions

### BeanNotFoundException

```java
public class BeanNotFoundException extends RuntimeException {
    public BeanNotFoundException(String message);
    public BeanNotFoundException(String message, Throwable cause);
}
```

### CircularDependencyException

```java
public class CircularDependencyException extends RuntimeException {
    public CircularDependencyException(String message);
    public CircularDependencyException(String message, Throwable cause);
}
```

### NoUniqueBeanException

```java
public class NoUniqueBeanException extends RuntimeException {
    public NoUniqueBeanException(String message);
    public NoUniqueBeanException(String message, Throwable cause);
}
```

## Invariants

1. **Singleton uniqueness:** `getBean(Class)` always returns the same instance for singleton beans
2. **Prototype freshness:** `getBean(Class)` always returns a new instance for prototype beans
3. **@Primary exclusivity:** At most one implementation per interface may have @Primary
4. **@Qualifier precedence:** @Qualifier takes precedence over @Primary when present
5. **Constructor-only injection:** Framework supports only constructor injection (no field injection)
6. **No @PreDestroy for prototype:** Prototype beans are not tracked for shutdown
7. **Single package:** `Autumn.start()` accepts only one base package in v1
8. **Zero runtime deps:** No dependencies beyond Jakarta APIs (compile scope)
