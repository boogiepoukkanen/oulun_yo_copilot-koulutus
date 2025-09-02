---
applyTo: "**"
---
# Project general coding standards

## Naming Conventions
- Use PascalCase for component names, interfaces, and type aliases
- Use camelCase for variables, functions, and methods
- Prefix private class members with underscore (_)
- Use ALL_CAPS for constants

## Error Handling
- Use try/catch blocks for async operations
- Implement proper error boundaries in React components
- Always log errors with contextual information

## Unit Testing

### Principles
- Test public **behavior**, not implementation details. Don’t test trivial getters/setters or generated code.
- Tests must be **fast, independent, and repeatable** — no real network/DB/time; inject `Clock`/`Random`.
- One clear behavior per test. Use AAA (Arrange–Act–Assert) / Given–When–Then.

### Structure & Naming
- Name tests like `method_shouldExpected_whenCondition()` and use `@DisplayName`.
- Mirror the package structure under `src/test/java` with `ClassNameTest`.
- Use parameterized tests for edge cases: `@ParameterizedTest` + `@CsvSource`.
- only one test file per class

### Coverage
- Targets: **≥ 80% line coverage**, **≥ 70% branch coverage** (JaCoCo).
- New/changed code (diff coverage): **≥ 90%**.
- Don’t chase 100%—prioritize risky branches and error paths.

### Assertions
- Prefer AssertJ for readable assertions.
- Compare **only what matters** (value, state, side effect).
- Avoid “monolithic” assertions; favor a few precise checks.
- Use `SoftAssertions` when validating many independent fields at once.

### Test Data & Setup
- Build data with a **Test Data Builder** or factories; keep `@BeforeEach` lightweight.
- Avoid logic (ifs/loops) inside tests; use parameterized tests instead.

### Mocking (Mockito)
- Mock **only external collaborators** (DB/HTTP/clock). Don’t mock value objects.
- Consider **in-memory fakes** instead of heavy mocks where it helps.
- Use `@Mock`, `@InjectMocks`, `ArgumentCaptor`; verify **only essential** interactions.
- Inject `Clock`/`IdGenerator`; avoid mocking static methods (use `mockito-inline` only if necessary).

### Copilot Usage
- Write behavior in comments and ask Copilot for **edge cases**, **negative paths**, and **parameterized tests**.
- Reject tests that depend on private details, use `sleep()`, or reflect into internals.
- Ask Copilot to refactor repetitive setup into Builders.

### Gradle (recommended)
```gradle
testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
testImplementation 'org.mockito:mockito-core:5.12.0'
testImplementation 'org.mockito:mockito-junit-jupiter:5.12.0'
testImplementation 'org.assertj:assertj-core:3.25.3'

plugins { id 'jacoco' }

jacocoTestCoverageVerification {
  violationRules {
    rule {
      limit { counter = 'LINE';   value = 'COVEREDRATIO'; minimum = 0.80 }
      limit { counter = 'BRANCH'; value = 'COVEREDRATIO'; minimum = 0.70 }
    }
  }
}
