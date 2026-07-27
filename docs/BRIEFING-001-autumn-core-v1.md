---
feature: Autumn Framework v1 — Core IoC Container
type: briefing
model: M2 (Standard)
date: 2026-07-27
status: draft
prd_ref: /home/rodrigopdo/Documents/sync-brain/personal-projects/autumn/prd-v1.md
execution_mode: implement or align
codebase_analysis:
  existing_code: yes
  existing_specs: no
  test_framework: junit-jupiter 5.10.0
  build_system: maven (multi-module)
  modules: autumn-core, autumn-examples
---

# Briefing — Autumn Framework v1

## 1. Objective

This briefing instructs the generation of spec-kit artifacts to complete Autumn Framework v1: fix 5 known bugs, implement 4 new features (@Qualifier, getBean(String), typed exceptions, internal logger), add tests for 100% coverage, and produce a README. The mode is "implement or align" because the codebase already exists with ~13 functional classes.

## 2. Classification

| Question | Answer | Model |
|---|---|---|
| Is it a bug? | Yes (5 bugs) + new features | — |
| Is it a spike/POC? | No | — |
| Is it a refactor? | Partial (bug fixes) | — |
| Isolated feature? | No (5 features + 5 bugs) | — |
| Has API/state? | Yes (BeanRegistry, BeanFactory) | — |
| Multi-repo? | No (single repo, multi-module) | — |

**Selected model: M2 — Standard**

**Justification:** Multi-feature with stateful components, public API with contract, multi-module Maven. Bug fixes + feature completions + tests + docs.

**Execution mode:** implement or align — existing code must be preserved and adjusted, not rewritten.

## 3. Codebase context

| Item | Value |
|---|---|
| Existing code | Yes — 13 classes in `autumn-core/src/main/java/io/autumn/core/` |
| Existing specs | None |
| Test location | `autumn-core/src/test/java/io/autumn/core/` |
| Test framework | JUnit Jupiter 5.10.0 |
| Build command | `mvn clean test` (project root) |
| Module structure | Multi-module: `autumn-core` (core), `autumn-examples` (examples) |
| Java version | 17+ (requires JDK 17) |

### Existing classes (autumn-core)

```
io.autumn.core
├── Autumn.java                    # Bootstrap entry point
├── annotations/
│   ├── Component.java             # @Component
│   ├── Primary.java               # @Primary
│   └── Scope.java                 # @Scope
├── context/
│   ├── ApplicationContext.java    # Interface
│   ├── AutumnContext.java         # Implementation
│   └── BeanFactory.java           # Bean creation + cache
├── registry/
│   ├── BeanDefinition.java        # Bean metadata
│   └── BeanRegistry.java          # Registration + resolution
├── lifecycle/
│   └── LifecycleManager.java      # @PostConstruct/@PreDestroy
└── utils/
    └── ClassPathScanner.java      # @Component discovery
```

### Missing components (to be created)

```
io.autumn.core
├── annotations/
│   └── Qualifier.java             # F-05: @Qualifier
├── injection/
│   └── DependencyResolver.java    # F-05: extracted resolver
├── exceptions/
│   ├── BeanNotFoundException.java       # F-11
│   ├── CircularDependencyException.java # F-11
│   └── NoUniqueBeanException.java       # F-11
└── utils/
    └── AutumnLogger.java          # F-12: internal logger
```

### Existing tests

| Test File | Coverage |
|---|---|
| `AutumnIntegrationTest.java` | Bootstrap + lookup |
| `BeanRegistryTest.java` | Registry + @Primary |
| `BeanFactoryTest.java` | Factory + circular deps |
| `LifecycleManagerTest.java` | @PostConstruct/@PreDestroy |
| `ClassPathScannerTest.java` | Package scanning |

## 4. Artifacts to generate

### Mandatory (spec-kit)

| Artifact | Path | Format | Source in PRD |
|---|---|---|---|
| Architecture Spec | `docs/specs/ARCHITECTURE-SPEC-001-autumn-core.md` | §5 | PRD §7.5, §8 |
| Tasks (consolidated) | `docs/tasks/TASK-001-through-013-autumn-core.md` | §5 | PRD §12, §13 |
| Contracts | `docs/contracts/CONTRACTS-001-autumn-core.md` | §5 | PRD §7.3, §7.4, §9 |
| Test Plan | `docs/tests/TEST-PLAN-001-autumn-core.md` | §5 | PRD §9, §6 |

### Mandatory (operational — agent harness)

| Artifact | Path | Format | Purpose |
|---|---|---|---|
| AGENTS.md | `AGENTS.md` | §5 | Root instructions for implementing agents |
| Spec Map | `docs/specs/spec-map.md` | §5 | Documentation graph connecting all artifacts by stable IDs |
| Harness | `harness/HARNESS-001-agent-execution.md` | §5 | Engineering loop, autonomy levels, stop conditions |
| Execution Status | `docs/EXECUTION-STATUS.md` | §5 | Operational checklist tracking task progress |
| Individual Tasks | `tasks/TASK-{NNN}.md` | §5 | One file per task (split from consolidated) |
| Test-First Packs | `test-first/TEST-FIRST-{NNN}.md` | §5 | Test-first instructions per task |
| Prompts | `prompts/PROMPT-{NNN}.md` | §5 | Implementation prompts per task |

### Conditional

| Artifact | Needed? | Reason |
|---|---|---|
| API Reference | Yes | Public API must be documented for spec-kit generation |
| Risk Register | No | PRD §11 already covers risks |

## 5. Artifact formats

### Architecture Spec

```markdown
# Architecture Spec — Autumn Core v1

## Overview
{paragraph: what this spec covers}

## Package Structure
{tree of packages with purpose of each}

## Classes

### {ClassName}
- **Package:** {package}
- **Type:** {class/interface/record}
- **Purpose:** {one line}
- **Dependencies:** {other classes it depends on}
- **Public Methods:** {method signatures}
- **Fields:** {state}
- **Lifecycle:** {creation, usage, destruction}

## Annotations
{for each annotation: name, target, retention, attributes}

## Exceptions
{hierarchy, when thrown, message format}

## Design Decisions
{key decisions and rationale}
```

**Rules:**
- Every class must reference PRD feature ID (F-01 to F-13) or bug ID (B-01 to B-05)
- Interface-first for ApplicationContext
- Exceptions extend RuntimeException (unchecked)
- No external dependencies beyond Jakarta APIs

### Tasks

```markdown
# TASK-{ID} — {Title}

Status: {Draft for Approval | In Progress | Done}
Target: {module or directory}
Autonomy level: {Level 1 - Single Task | Level 2 - Work Package}

## Objective
{one paragraph}

## Dependencies
{upstream tasks}

## Source Documents
{full paths to PRD, existing code files}

## Scope
{what this task implements}

## Out Of Scope
{what this task must NOT touch}

## Acceptance Criteria
{checklist from PRD §9}

## Verification Commands
{exact shell commands}

## Allowed Files
{whitelist of modifiable paths}

## Stop Conditions
{when to stop and ask for review}
```

### Contracts

```markdown
# API Contract — Autumn Core v1

## Public API

### Autumn (bootstrap)
{static method signatures}

### AutumnContext
{interface methods with types}

### Annotations
{attribute definitions}

### Exceptions
{class hierarchy, constructor signatures, message format}

## Invariants
{things that must always be true}
```

### Test Plan

```markdown
# Test Plan — Autumn Core v1

## Test Categories

### Unit Tests
{per class, what to test}

### Integration Tests
{end-to-end scenarios}

## Test Cases

### TC-{ID} — {Name}
- **Feature:** {F-xx or B-xx}
- **Preconditions:** {setup}
- **Steps:** {numbered}
- **Expected:** {assertion}
- **Type:** {unit/integration}

## Coverage Targets
{per component}
```

### AGENTS.md

```markdown
# Agent Instructions — Autumn Framework

This repository contains the Autumn IoC framework and its spec-kit documentation.

## Repository Boundaries

- Core module: `autumn-core/` — all Java source and tests
- Examples module: `autumn-examples/` — demo usage
- Spec-kit: `docs/`, `specs/`, `tasks/`, `contracts/`, `harness/`, `test-first/`, `prompts/`

## Product Workflow

- Use `docs/BRIEFING-001-autumn-core-v1.md` as the planning source of truth
- Use `docs/specs/spec-map.md` to understand the documentation graph
- Use `docs/EXECUTION-STATUS.md` to check task progress before starting work
- Use `harness/HARNESS-001-agent-execution.md` as the operating system for task execution

## Execution Rules

- Read the relevant task file before changing code
- Run verification commands after each task
- Update `docs/EXECUTION-STATUS.md` after each task
- Commit per task, not per batch
- Do not merge PRs automatically
```

### Spec Map

```markdown
# Spec Map — Autumn Core v1

## Purpose
Connects all artifacts by stable IDs so agents can navigate the documentation graph.

## Source Of Truth
- Planning: `docs/BRIEFING-001-autumn-core-v1.md`
- Product: PRD (external)

## Artifact Order
1. PRD (external)
2. Briefing
3. Architecture Spec
4. Contracts
5. Test Plan
6. Tasks (consolidated)
7. Individual task files
8. Test-first packs
9. Prompts
10. Execution status

## Artifact Graph
| ID | Artifact | Path | Depends On |
|---|---|---|---|
| SPEC-001 | Architecture Spec | `docs/specs/ARCHITECTURE-SPEC-001-autumn-core.md` | PRD |
| CONTRACTS-001 | API Contract | `docs/contracts/CONTRACTS-001-autumn-core.md` | SPEC-001 |
| TEST-PLAN-001 | Test Plan | `docs/tests/TEST-PLAN-001-autumn-core.md` | SPEC-001, CONTRACTS-001 |
| TASKS-001 | Tasks (consolidated) | `docs/tasks/TASK-001-through-013-autumn-core.md` | SPEC-001, TEST-PLAN-001 |
| TASK-{NNN} | Individual task | `tasks/TASK-{NNN}.md` | TASKS-001 |
| TEST-FIRST-{NNN} | Test-first pack | `test-first/TEST-FIRST-{NNN}.md` | TASK-{NNN} |
| PROMPT-{NNN} | Implementation prompt | `prompts/PROMPT-{NNN}.md` | TASK-{NNN}, TEST-FIRST-{NNN} |
```

### Harness

```markdown
# HARNESS-001 — Agent Execution Harness

## Purpose
Defines how AI agents execute work on Autumn Framework v1.

## Source Documents
- `docs/BRIEFING-001-autumn-core-v1.md`
- `docs/specs/spec-map.md`
- `docs/EXECUTION-STATUS.md`
- the relevant task file
- the relevant test-first pack

## Engineering Loop
Each task follows: Context → Plan → Implement → Verify → Review → Integrate → Learn

### 1. Context
Read PRD sections, specs, contracts, tests, and task instructions before changing files.

### 2. Plan
Create implementation plan: files, risk level, tests, unknowns, stop conditions.

### 3. Implement
Change only files required by the task. Preserve existing patterns.

### 4. Verify
Run smallest meaningful verification set (tests, build, lint).

### 5. Review
Review diff against acceptance criteria, specs, contracts, test results.

### 6. Integrate
Commit from correct location. Create task branch. Push when verified. Open PR.

### 7. Learn
If implementation exposes gaps, update relevant docs before continuing.

## Autonomy Levels
- Level 1: Single task, stops after completion
- Level 2: Work package (max 5 related tasks)

## Stop Conditions
- Product behavior not defined
- Conflicting specs
- Missing domain state
- Verification failure
- Security concern

## Merge Gates
- Acceptance criteria satisfied
- Verification passed
- No unrelated changes
- Correct commit location
```

### Execution Status

```markdown
# EXECUTION-STATUS — Autumn Core v1 Task Progress

Status: Active
Last updated: {date}

## Status Legend
- `[ ] Not started`
- `[~] In progress`
- `[>] In review`
- `[x] Done`
- `[!] Blocked`

## Phase 1: Build Green
- `[ ]` `TASK-001` — Fix B-05: Java 17 Build

## Phase 2: Bugs Fixed
- `[ ]` `TASK-002` — Fix B-01: BeanRegistry.resolveType()
- `[ ]` `TASK-003` — Fix B-02: LifecycleManager Double Scan
- `[ ]` `TASK-004` — Fix B-03: ClassPathScanner Silent Failures
- `[ ]` `TASK-005` — Fix B-04: BeanFactory Prototype Circular Dep

## Phase 3: New Features
- `[ ]` `TASK-006` — Refactor BeanDefinition to Record
- `[ ]` `TASK-007` — Extract DependencyResolver
- `[ ]` `TASK-008` — Implement @Qualifier
- `[ ]` `TASK-009` — Implement getBean(String)
- `[ ]` `TASK-010` — Typed Exceptions (3 classes)
- `[ ]` `TASK-011` — Internal Logger

## Phase 4: Tests + Docs
- `[ ]` `TASK-012` — Additional Tests
- `[ ]` `TASK-013` — README
```

### Individual Task File

```markdown
# TASK-{NNN} — {Title}

Status: {Draft for Approval | In Progress | Done}
Phase: {1-4}
Target: {module or directory}
Autonomy Level: {Level 1 | Level 2}
Depends On: {upstream task IDs}

## Objective
{one paragraph}

## Source Documents
- PRD: {sections}
- Spec: `docs/specs/ARCHITECTURE-SPEC-001-autumn-core.md`
- Contract: `docs/contracts/CONTRACTS-001-autumn-core.md`

## Scope
{what this task implements}

## Out Of Scope
{what this task must NOT touch}

## Acceptance Criteria
- [ ] {criterion 1}
- [ ] {criterion 2}

## Verification Commands
```bash
{commands}
```

## Allowed Files
- {path 1}
- {path 2}

## Stop Conditions
- {when to stop}

## Test-First Pack
`test-first/TEST-FIRST-{NNN}.md`

## Prompt
`prompts/PROMPT-{NNN}.md`
```

### Test-First Pack

```markdown
# TEST-FIRST-{NNN} — {Task Title}

## Pre-Implementation Tests
Write these tests BEFORE implementing the feature/fix.

### Test Files to Create or Modify
- `autumn-core/src/test/java/io/autumn/core/{TestFile}.java`

### Test Cases

#### TC-{NNN}-01 — {Test Name}
- **Type:** unit
- **Preconditions:** {setup}
- **Steps:**
  1. {step}
- **Expected:** {assertion}

#### TC-{NNN}-02 — {Test Name}
...

## Post-Implementation Verification
After implementing, run:
```bash
mvn test -pl autumn-core -Dtest={TestClass}
```

## Coverage Target
{per component}
```

### Implementation Prompt

```markdown
# PROMPT-{NNN} — {Task Title}

## Task
TASK-{NNN}: {title}

## Context
{one paragraph summary}

## Files to Change
- {file 1}: {what to change}
- {file 2}: {what to change}

## Acceptance Criteria
1. {criterion}
2. {criterion}

## Verification
```bash
{commands}
```

## Do NOT
- {restriction 1}
- {restriction 2}
```

## 6. Task rules

### Context budget rule

Every task must not exceed ~40% of the agent's context window. If a task requires more than 3 source files or 500 lines of code, split into sub-tasks.

### Task dependency chain (from PRD §12)

```
T-01 (B-05 fix) → T-02 (B-01) → T-07 (DependencyResolver) → T-08 (@Qualifier)
                → T-03 (B-02)
                → T-04 (B-03)
                → T-05 (B-04)
                → T-06 (BeanDefinition record) → T-09 (getBean(String))
                → T-10 (exceptions)
                → T-11 (logger)
                → T-12 (tests) — depends on T-08, T-09, T-10
                → T-13 (README) — depends on all
```

### Execution mode rules (implement or align)

- **Preserve:** All existing passing tests must continue to pass
- **Preserve:** Public API of AutumnContext, BeanFactory, BeanRegistry
- **Adjust:** BeanRegistry.resolveType() to fix B-01
- **Adjust:** LifecycleManager.postConstruct() to fix B-02
- **Adjust:** ClassPathScanner to fix B-03
- **Implement:** @Qualifier annotation + DependencyResolver
- **Implement:** getBean(String) in AutumnContext
- **Implement:** 3 exception classes
- **Implement:** AutumnLogger
- **Implement:** Tests for new features
- **Implement:** README

### Deferral rules

Tasks can be deferred ONLY when:
- A dependency is blocking (upstream task not complete)
- External infrastructure is unavailable

Tasks CANNOT be deferred when:
- They are critical path (T-01 blocks everything)
- They block other tasks

## 7. Validation gates

| Gate | Required? | Action |
|---|---|---|
| Scope validation (IN/OUT) | Yes | Compare PRD §4.1 vs artifact scope |
| Cross-validation (spec vs tasks) | Yes | Each task references PRD feature/bug |
| Dependency check | Yes | No circular deps, all deps valid |
| Context budget check | Yes | Each task < 40% context |
| Maker/checker | No | Solo project |
| Human sign-off | Yes | User approves before implementation |

## 8. Execution order

```
Phase A — Spec-kit generation:
1. Read this briefing
2. Read the PRD (prd-v1.md)
3. Analyze codebase (§3 — read all existing classes)
4. Generate Architecture Spec (format: §5)
5. Generate Contracts (format: §5)
6. Generate Test Plan (format: §5)
7. Generate Tasks consolidated (format: §5, following dependency chain §6)
8. Run validation gates (§7)

Phase B — Agent harness generation:
9.  Generate AGENTS.md (format: §5)
10. Generate Spec Map (format: §5)
11. Generate Harness (format: §5)
12. Generate Execution Status (format: §5)
13. Split consolidated tasks into individual files (tasks/TASK-{NNN}.md)
14. Generate Test-First Packs per task (test-first/TEST-FIRST-{NNN}.md)
15. Generate Implementation Prompts per task (prompts/PROMPT-{NNN}.md)

Phase C — Approval:
16. Request human approval (CP-2)
```

## 9. Approval checkpoints

| Checkpoint | When | What to present |
|---|---|---|
| CP-1 | After classification | Model + justification ✅ (done) |
| CP-2 | After all artifacts generated | List of artifacts + validation results |
| CP-3 | Before implementation begins | Final review of all specs |

## 10. Stop conditions

The agent must STOP and ask for approval when:

- [ ] PRD is incomplete (missing scope, metrics, acceptance criteria)
- [ ] Classification is ambiguous
- [ ] Existing code conflicts with PRD requirements
- [ ] A task requires more than 3 source files
- [ ] A task would modify files outside its Allowed Files list
- [ ] A dependency creates a circular reference
- [ ] The agent is unsure about a domain concept
- [ ] A validation gate fails

The agent may continue without asking when:

- [ ] The issue is a local implementation detail
- [ ] Existing patterns clearly answer the question
- [ ] The change is cosmetic

## 11. Gap ledger

### Open (blocking)
- [ ] JDK 17 availability — B-05 fix requires JDK 17, environment may have JDK 11 (PRD §10)

### Open (non-blocking)
- [ ] Q-01: @Qualifier in constructors only or also fields? PRD says "constructor only" but not enforced in code
- [ ] Q-02: getBean(String) uses simple name or @Component value? PRD says "both: @Component first, else simple name"
- [ ] Q-03: Prototype beans and @PreDestroy? PRD says "no"
- [ ] Q-04: Multiple packages in Autumn.start()? PRD says "no in v1"
- [ ] Q-05: Coverage tool — JaCoCo via Maven plugin (PRD §14)
- [ ] No `injection/` package exists yet — DependencyResolver is new
- [ ] No `exceptions/` package exists yet — 3 exception classes are new

### Resolved
- [x] Classification confirmed as M2

## 12. References

| Document | Path | Usage |
|---|---|---|
| PRD | `/home/rodrigopdo/Documents/sync-brain/personal-projects/autumn/prd-v1.md` | Source of truth |
| Project root | `/home/rodrigopdo/personalProjects/backend/autumn-framework` | Codebase |
| Core module | `/home/rodrigopdo/personalProjects/backend/autumn-framework/autumn-core` | Main code |
| Tests | `/home/rodrigopdo/personalProjects/backend/autumn-framework/autumn-core/src/test/java/io/autumn/core` | Test patterns |
| POM (root) | `/home/rodrigopdo/personalProjects/backend/autumn-framework/pom.xml` | Build config |
| POM (core) | `/home/rodrigopdo/personalProjects/backend/autumn-framework/autumn-core/pom.xml` | Core dependencies |
