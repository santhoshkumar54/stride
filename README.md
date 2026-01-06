# GoalNest

GoalNest is a lightweight manager application for tracking goals, leaves, and tasks, designed to help development teams manage their activities efficiently.

## Prerequisites for a New Apple Laptop

To run this application on a new Apple laptop (macOS), you will need to set up your development environment. We recommend using [Homebrew](https://brew.sh/) to manage your packages.

### 1. Install Homebrew
Open your Terminal and run the following command to install Homebrew if you haven't already:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

Follow the on-screen instructions to add Homebrew to your PATH.

### 2. Install Java 17
This project requires Java 17. You can install it using Homebrew:

```bash
brew install openjdk@17
```

After installation, you might need to symlink it so the system can find it:

```bash
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

Verify the installation:
```bash
java -version
```

### 3. Install Maven
Maven is used to build and manage the project's dependencies.

```bash
brew install maven
```

Verify the installation:
```bash
mvn -version
```

## Getting Started

### 1. Clone the Repository
Clone the project to your local machine:

```bash
git clone <repository-url>
```

### 2. Run the Application
Navigate to the project directory where the `pom.xml` file is located. Assuming you are in the directory where you cloned the repo:

```bash
cd <repository-name>/GoalNest
```

Start the application using the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

The application will start on port **8080**.

## Accessing the Application

Open your web browser and go to:
[http://localhost:8080](http://localhost:8080)

### Default Login Credentials
The application comes pre-seeded with the following users for testing purposes. The password for all accounts is `password`.

| Role | Username (Email) | Password |
|------|------------------|----------|
| **Manager** | `manager@stride.com` | `password` |
| **Scrum Master** | `sm@stride.com` | `password` |
| **Engineer** | `dev@stride.com` | `password` |

## Project Structure

*   **Java Version**: 17
*   **Framework**: Spring Boot 2.7.18
*   **Database**: H2 (In-memory) - No external database setup required.
*   **Build Tool**: Maven

## Troubleshooting

*   **Port in use**: If port 8080 is already in use, you can identify the process using `lsof -i :8080` and kill it, or configure a different port in `src/main/resources/application.properties`.
*   **Java Home**: If Maven cannot find your Java installation, ensure your `JAVA_HOME` environment variable is set correctly.
    ```bash
    export JAVA_HOME=$(/usr/libexec/java_home -v 17)
    ```
