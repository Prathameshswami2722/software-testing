# Team 12 - Employee Contact Information

Selenium + Java + TestNG mini-project for the public OrangeHRM demo, based on `miniproject.pdf`.

## Start here

- [Complete beginner's guide (PDF)](output/pdf/team12-complete-beginners-guide.pdf): explanations, commands, screenshots, troubleshooting, presentation questions, and full source reference.
- [Download the project ZIP](output/team12-employee-contact-information.zip).
- [Original assignment](miniproject.pdf).

## Assigned deliverables

| File | Responsibility |
| --- | --- |
| `src/main/java/pages/ContactDetailsPage.java` | Telephone and email fields, save, email validation |
| `src/main/java/pages/AddressInformationPage.java` | Postal address fields and country selection |
| `src/main/java/pages/EmergencyContactPage.java` | Add, read, edit, delete emergency contacts |
| `src/test/java/tests/ContactDetailsTest.java` | Data providers, test flows and assertions |

These are the four files listed in project 12: **three business page objects and one test class**. The test class contains four scenarios and two datasets per scenario, producing eight test executions.

## Requirements

- JDK 17 or later (`java -version`)
- Maven 3.9+ (`mvn -version`)
- Google Chrome
- Internet access to Maven Central, ChromeDriver distribution, and OrangeHRM

Selenium Manager resolves ChromeDriver automatically. No driver executable needs to be committed. Dependencies are pinned in `pom.xml`.

## Run

From this directory:

```sh
mvn test
```

To watch the browser:

```sh
mvn test -Dheadless=false
```

Compile without starting browser tests:

```sh
mvn test -DskipTests
```

Run one scenario (Maven Surefire's `-Dtest` overrides suite selection):

```sh
mvn test '-Dtest=tests.ContactDetailsTest#saveContactDetails'
```

For the workspace-local dependency and driver caches used during development:

```sh
SE_CACHE_PATH="$PWD/.cache/selenium" mvn -Dmaven.repo.local=.cache/m2 test
```

Optional properties:

| Property | Default | Purpose |
| --- | --- | --- |
| `headless` | `true` | Set `false` to display Chrome |
| `baseUrl` | `https://opensource-demo.orangehrmlive.com` | OrangeHRM base URL |
| `username` | `Admin` | Demo administrator username |
| `password` | `admin123` | Public demo password |
| `timeout` | `25` | Explicit wait timeout in seconds |
| `chromeBinary` | auto-detected | Optional Chrome executable path |

Use only a demo/test installation. All datasets are fictional; email addresses use `example.com`.

## Test cases

| ID | Scenario | Data variants | Main assertions |
| --- | --- | --- | --- |
| TC01 | Save contact details | Two sets of telephone/email values | All five values survive reload |
| TC02 | Save address | India and Sri Lanka | Street 1/2, city, state, postal code and country survive reload |
| TC03 | Reject invalid email | Invalid Work Email; invalid Other Email | Exact validation message; previously saved values remain unchanged |
| TC04 | Manage emergency contact | Sibling and Parent | All five values persist after add/edit; old name disappears; deletion survives reload |

Each invocation gets a fresh Chrome session, logs in, opens PIM, creates a uniquely named test employee, and opens its Contact Details screen. It exercises the assigned behavior through the UI. Teardown deletes that employee and closes Chrome, including after test failures.

Employee creation/deletion uses the same authenticated OrangeHRM session and `/web/index.php/api/v2/pim/employees`. This setup utility avoids introducing a fourth business page object and avoids changing shared demo employee records. Contact details, addresses, email validation, and emergency contacts are all tested through Selenium UI actions. API setup is an implementation choice; the PDF does not specify a fixture strategy.

A hard process kill or demo outage can interrupt cleanup. In that event, look for the uniquely named `Team12 Automation Test...` employee shown in the failure details and remove only that test record. Cleanup failures are reported as configuration failures rather than silently ignored.

## Reports

After a run:

- `target/extent-report/index.html`: Extent report with datasets, results, exception details, and screenshots.
- `target/extent-report/screenshots/`: screenshots linked from the report.
- `target/surefire-reports/`: TestNG/Maven reports and XML results.

Keep the whole `extent-report` directory when sharing so screenshot links continue to work. Maven returns a nonzero exit code for test or setup/cleanup failures. Reports are regenerated on subsequent runs; `mvn clean` removes `target`.

## Framework and POM

```text
src/main/java/
  base/BaseTest.java
  pages/
    LoginPage.java                 # shared framework
    NavigationBar.java             # shared framework
    ContactDetailsPage.java        # business 1
    AddressInformationPage.java    # business 2
    EmergencyContactPage.java      # business 3
  utils/
    Config.java
    DriverFactory.java
    UiActions.java
    TestEmployee.java
    ExtentManager.java
    ExtentTestListener.java
src/test/java/tests/ContactDetailsTest.java
testng.xml
pom.xml
```

`BaseTest`, `LoginPage`, and `NavigationBar` retain the PDF's responsibilities. Login and navigation gain explicit waits; the missing driver/report utilities are supplied. `UiActions` is a reusable framework helper. AddressInformationPage represents a section of the Contact Details screen, as required by the assigned class split.

Page objects own locators and interactions. Tests own expected values and assertions. Explicit waits handle form loaders, clickable controls, validation, success notifications and table changes; implicit waits and fixed sleeps are not used. Tests run sequentially and have no ordering dependencies.

## Troubleshooting

- Dependency resolution error: check access to Maven Central. Do not use offline mode before dependencies are downloaded.
- Driver creation error: check Chrome installation and access to ChromeDriver downloads; optionally set `-DchromeBinary=/path/to/chrome`.
- Login failure: verify the demo is available and its displayed credentials match the configured values.
- Fixture API failure: the demo's API or permissions may have changed. The configuration failure includes the HTTP status and response.
- Locator/validation failure: examine the failure screenshot and exception in the Extent report. The public demo can be changed or reset by other visitors.

## Verification on 25 November 2026

The full browser suite executed eight data cases: seven passed and one was skipped after OrangeHRM's login page timed out. The full Maven command correctly returned a failure because of that setup timeout. Both email-validation datasets were then rechecked successfully (two passed, zero failed/skipped), including the interrupted Work Email case. Thus all eight data cases have passed across these verification runs; this is not a claim of a single uninterrupted eight-pass run.

All temporary employees from both final verification runs were deleted. The final code also prevents employee setup from continuing after an unsuccessful login. The Maven compilation and single-scenario reporting path were verified during the email recheck.

- [Full-suite report, including the original timeout](output/full-suite-report/index.html)
- [Successful email-validation recheck](output/email-recheck-report/index.html)

The ZIP includes both report snapshots with their screenshots and TestNG XML. These snapshots remain available after `mvn clean`; new runs generate fresh reports under `target/`. Verification used JDK 23, Chrome 158, and a temporary Maven cache to bypass local cached-file read stalls. The project targets Java 17 or later.
