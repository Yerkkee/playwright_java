# Project: playwright-java (UI test automation portfolio)

## Context
Portfolio project of a QA Automation Engineer. Target app: https://www.saucedemo.com
Goal: clean, idiomatic, interview-ready framework. I am learning — explain non-obvious decisions briefly.

## Stack
Java 17, Maven, Playwright for Java, TestNG, Allure, AssertJ, Lombok, SLF4J.

## Rules
- Page Object Model: pages in com.yerkkee.pages, tests in com.yerkkee.tests, infra in com.yerkkee.base / config / listeners / utils.
- Locators only via getByTestId (testIdAttribute = "data-test") or getByRole. No XPath, no CSS chains unless unavoidable.
- Tests never touch raw Page/Locator — only page-object methods.
- Assertions: AssertJ in tests; Playwright assertThat() for UI waiting assertions. No Thread.sleep, no hard waits.
- Every public page method has @Step; every test has @Feature/@Story/@Severity/@Description.
- No secrets/credentials hardcoded in tests — test data in a dedicated class/provider.
- Code, comments, commit messages, README — in English.
- Small commits, Conventional Commits style (feat:, fix:, test:, ci:, docs:, chore:).
- Before saying a stage is done: run `mvn clean test` and make sure the build is green.