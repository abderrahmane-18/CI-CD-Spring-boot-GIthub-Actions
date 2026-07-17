# Employee Manager - Spring Boot CI/CD

Employee Manager is a Spring Boot web application for adding, listing, editing, and deleting employees. The `feature/employee-manager` branch is a standalone application that reuses the Maven, Docker, and GitHub Actions foundations from `main`.

The repository has two Git branches, but the final branch contains one standalone Spring Boot project - not two nested applications. It has one root `pom.xml`, one `src` tree, one Spring Boot main class, one Dockerfile, and one CI/CD workflow.

## Technology

- Java 21
- Spring Boot 4.1.0
- Spring MVC, Thymeleaf, Spring Data JPA, and Bean Validation
- Embedded H2 database
- JUnit 5, Mockito, Selenium, and JMeter
- Maven Wrapper, Docker, and GitHub Actions

## Prerequisites

- Java 21
- Docker Desktop or Docker Engine
- Google Chrome for local Selenium execution
- JMeter 5.6.x for local performance-test execution

The Maven Wrapper downloads the required Maven version automatically.

## Run the application

Linux or macOS:

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). The root route displays the employee list.

Main routes:

| Method | Route | Purpose |
| --- | --- | --- |
| `GET` | `/` or `/employees` | Display employees |
| `GET` | `/showNewEmployeeForm` | Display the employee form |
| `POST` | `/saveEmployee` | Create or update an employee |
| `POST` | `/deleteEmployee/{id}` | Delete an employee |
| `GET` | `/hello` | Optional technical smoke endpoint |

## Unit tests

Run compilation and JUnit/Mockito tests:

```bash
./mvnw clean test
./mvnw clean verify
```

On Windows, replace `./mvnw` with `.\mvnw.cmd`. Unit tests cover `EmployeeService.addEmployee()`, `getAllEmployees()`, and `deleteEmployee()` without requiring a browser or external database.

## Selenium functional test

Start the application in one terminal. In another terminal, run:

```bash
./mvnw -Pselenium -Dselenium.base-url=http://localhost:8080 test-compile failsafe:integration-test failsafe:verify
```

Windows PowerShell:

```powershell
.\mvnw.cmd -Pselenium "-Dselenium.base-url=http://localhost:8080" test-compile failsafe:integration-test failsafe:verify
```

The headless Chrome test creates unique employee data, verifies the employee appears, deletes that employee, and verifies it disappears. On failure, a screenshot is written under `target/selenium-screenshots/`.

## JMeter performance test

With the application running, execute the plan in non-GUI mode:

```bash
jmeter -n \
  -t performance-tests/employee-manager.jmx \
  -l target/jmeter-results.jtl \
  -e \
  -o target/jmeter-report
```

The plan uses configurable `protocol`, `host`, and `port` properties, with defaults `http`, `localhost`, and `8080`. It starts exactly 50 virtual users over a 10-second ramp-up and exercises `GET /` and `GET /employees` with HTTP 200 assertions.

Override a target with JMeter properties, for example:

```bash
jmeter -n -t performance-tests/employee-manager.jmx -Jhost=127.0.0.1 -Jport=8080 -l target/jmeter-results.jtl
```

Generated JMeter results and reports are ignored by Git.

## Docker

Build the same image name used by CI/CD:

```bash
docker build -t abderrahmane18/spring_boot_app_ci_cd:latest .
```

Run a disposable local container:

```bash
docker run --rm --name employee-manager-test -p 8080:8080 abderrahmane18/spring_boot_app_ci_cd:latest
```

Open [http://localhost:8080](http://localhost:8080) and test the complete employee workflow. The runtime container uses a non-root user and contains the Employee Manager executable JAR, including Thymeleaf templates.

### Recreate a container after a new image is published

A running container does not change when the `latest` tag is updated. Pull the image and recreate the container:

```bash
docker stop employee-manager || true
docker rm employee-manager || true
docker pull abderrahmane18/spring_boot_app_ci_cd:latest
docker run -d --name employee-manager -p 8080:8080 abderrahmane18/spring_boot_app_ci_cd:latest
docker logs employee-manager
```

Windows PowerShell:

```powershell
docker stop employee-manager
docker rm employee-manager
docker pull abderrahmane18/spring_boot_app_ci_cd:latest
docker run -d --name employee-manager -p 8080:8080 abderrahmane18/spring_boot_app_ci_cd:latest
docker logs employee-manager
```

If the container does not already exist, the first two Windows commands may report `No such container`; continue with the pull and run commands.

## GitHub Actions CI/CD

The workflow is `.github/workflows/ci-cd.yml`. It runs on every push to `feature/employee-manager` and on pull requests targeting `main`.

The dependent jobs run in this order:

1. Maven compilation, unit tests, verification, and JAR packaging.
2. Selenium employee lifecycle and JMeter plan validation.
3. Docker image build after every required test succeeds.
4. Docker Hub publication only for direct pushes to `feature/employee-manager`.

Published tags are:

- `abderrahmane18/spring_boot_app_ci_cd:latest`
- `abderrahmane18/spring_boot_app_ci_cd:sha-<short-commit-sha>`

Configure these repository secrets:

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`

For compatibility with the original workflow, `DOCKER_USERNAME` and `DOCKER_PASSWORD` are also accepted as fallbacks. Never store Docker Hub credentials in repository files.

Pull requests build the Docker image but never publish it.

## Database

The application uses the embedded in-memory H2 database at `jdbc:h2:mem:employee-manager`. This keeps local, CI, Selenium, JMeter, and Docker runs self-contained and credential-free. Employee data is intentionally reset when the application stops.

## Troubleshooting

- Port 8080 already used: stop the process or container using it before starting Employee Manager.
- Selenium cannot start Chrome: install Google Chrome and confirm it is available on `PATH`; Selenium Manager resolves the matching driver.
- Selenium reports that the application is unavailable: start Employee Manager first or override `selenium.base-url`.
- Docker still shows the old application: pull the latest image and recreate the container; restarting an old container does not replace its image.
- JMeter report directory exists: remove the generated `target/jmeter-report` directory before using `-e -o` again.
- CI publication fails: confirm the Docker Hub secret names, token permissions, and access to `abderrahmane18/spring_boot_app_ci_cd`.
