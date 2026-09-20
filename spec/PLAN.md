# Autumn Core v1

Fonte da verdade: `spec/features/*.feature`.
Se o comportamento mudar, o `.feature` muda primeiro. Sem spec-kit, sem task files, sem harness.

## Como implementar com IA

1. Pegue a próxima fase abaixo.
2. Escreva (ou ajuste) o teste JUnit com `@DisplayName` igual ao nome do cenário.
3. Faça o cenário passar. Não adicione Cucumber nesta v1: o Gherkin é o spec, o JUnit é o executor.
4. Commit por fase. Não abra docs novos para “organizar o trabalho”.

Idioma do spec: português. Nomes de tipo, anotação e método: iguais ao código.

## Recorte

Tudo o que falta para a v1 funcional:

- Trazer o patch de lifecycle que hoje está só em `develop` (scan único + prototype sem `@PreDestroy`).
- Corrigir bootstrap de prototype, factory reentrante, scanner que engole erro, `containsBean` com NPE, `close()` fora da interface, `@Component.value()` ignorado.
- Completar a API: `getBean(String)`, `@Qualifier`, `DependencyResolver`, exceções tipadas, `AutumnLogger`.
- Fechar com JaCoCo, README e LICENSE (MIT, já declarado no POM).

Fora desta v1: field/setter injection, vários pacotes no `start()`, AOP, XML, scopes além de singleton/prototype.

## Fases

Cada fase lista os cenários que precisam ficar verdes. Ordem importa: cada uma assume a anterior.

### 0. Alinhar lifecycle do develop

`LifecycleManager.postConstruct` faz um único scan dos métodos. Prototype nunca entra na lista de `@PreDestroy`. Falha em `@PostConstruct` é logada e não derruba os outros beans.

Cenários: `ciclo-de-vida.feature` → scan único, prototype sem destroy, falha isolada no `@PostConstruct`.

### 1. Container estável

- `Autumn.start` instancia só singletons. Prototype nasce só no `getBean`.
- `BeanFactory` não usa `ConcurrentHashMap.computeIfAbsent` para criar bean (o mapa não é reentrante). Grafo de singletons com construtor tem que subir.
- `containsBean` não lança NPE se o tipo não existe.
- `ApplicationContext.close()` existe e delega o shutdown.
- Scanner: `ClassNotFoundException` / `NoClassDefFoundError` são ignorados; qualquer outro erro vai para WARN.

Cenários: `descoberta.feature` (erros de scan), `injecao.feature` (grafo reentrante), `ciclo-de-vida.feature` (prototype não nasce no boot), `consulta-e-erros.feature` (`close` e `containsBean`).

### 2. Falhas explícitas e log

Três unchecked: `BeanNotFoundException`, `CircularDependencyException`, `NoUniqueBeanException`. Mensagens trazem tipo, nome ou impls.

`AutumnLogger` com prefixo `[AUTUMN]` e `info` / `warn` / `error`. O core para de falar via `System.out` / `System.err`.

Cenários: `consulta-e-erros.feature` (exceções e logger) e os de ciclo circular / impl duplicada em `injecao.feature`.

### 3. Lookup por nome

`@Component("email")` vira o nome do bean. Sem value, o nome é o simple name. `getBean(String)` procura value primeiro, depois simple name. Não achar → `BeanNotFoundException`.

Cenários: `consulta-e-erros.feature` → lookup por tipo, por value, por simple name, miss.

### 4. Qualifier

`DependencyResolver` sai do `BeanFactory`. `@Qualifier` só em parâmetro de construtor. Qualifier ganha de `@Primary`. Sem qualifier e sem primary, com várias impls → `NoUniqueBeanException`. `BeanDefinition` ganha `qualifierNames`.

Cenários: `injecao.feature` → qualifier, qualifier vs primary, várias impls.

### 5. Fechar a v1

- JaCoCo no `autumn-core`, alvo ≥ 90%.
- README na raiz: o que é, como buildar, anotações, exemplo que roda.
- LICENSE MIT.
- Example continua demonstrando injection, `@Primary`, prototype e lifecycle, agora sem instanciar prototype no boot.

Cenários: todos verdes + `mvn test` + relatório JaCoCo.

## Mapa rápido

| Feature | Código que deve mudar |
|---|---|
| `descoberta.feature` | `ClassPathScanner`, `Autumn` |
| `injecao.feature` | `BeanFactory`, `BeanRegistry`, `BeanDefinition`, `DependencyResolver`, `@Qualifier` |
| `ciclo-de-vida.feature` | `LifecycleManager`, `Autumn.start`, `ApplicationContext.close` |
| `consulta-e-erros.feature` | `AutumnContext`, `BeanRegistry` (índice por nome), exceções, `AutumnLogger` |

## Critério de pronto

- Todos os cenários têm teste JUnit correspondente e passam.
- Example: `mvn -pl autumn-examples -am exec` (ou `main` do `Application`) mostra 2 `AuditLogger` distintos e nenhum terceiro criado no boot.
- README descreve o recorte real, não o recorte desejado.
