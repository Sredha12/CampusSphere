# CampusSphere
> **Every Event. One Campus. One Platform.**

CampusSphere is a modern, lightweight College Event Management System built with pure Java web technologies. It provides college clubs with an intuitive interface to publish and administer campus activities, while empowering students to explore events, manage registrations, and track participation history seamlessly.

---

## Approved Technology Stack & Environment
* **Platform & Runtime:** Java 21 LTS (`jdk-21.0.12`)
* **Build Automation:** Apache Maven 3.9.16 (WAR packaging)
* **Web Container:** Apache Tomcat 10.1.61 (Standalone)
* **Specifications:** Jakarta EE 10 Core (Jakarta Servlet 6.0, Jakarta JSP 3.1, JSTL 3.0)
* **Database Engine:** MySQL Server 8.4 Community Edition (Service `MySQL84`)
* **Database Driver:** Direct JDBC via `com.mysql:mysql-connector-j:8.4.0`
* **Frontend:** Clean Semantic HTML5, Vanilla CSS3 (Custom Design System), Vanilla JavaScript

---

## Prerequisites
Before launching CampusSphere, ensure the following are available on your Windows system:
1. Java 21 JDK (`java -version` and `javac -version` working)
2. Maven 3.9+ (`mvn -v` working)
3. Apache Tomcat 10.1.x extracted locally (e.g., `C:\Users\Sreddha\tools\apache-tomcat-10.1.61`)
4. MySQL Server 8.4 running on port 3306

---

## Windows PowerShell Quick Start Guide

> **SECURITY WARNING:**
> * Never commit database passwords, API tokens, or secret keys to Git.
> * Never paste database passwords or secret keys into chat tools or public forums.

### Step 1: Set Up External Application Configuration
CampusSphere reads configuration from outside the repository to ensure credentials are never accidentally tracked.
```powershell
# 1. Create the external configuration directory
New-Item -ItemType Directory -Path "$HOME\.campussphere" -Force

# 2. Copy the template into place
Copy-Item "config\campussphere.properties.example" -Destination "$HOME\.campussphere\campussphere.properties"

# 3. Open and edit the properties file with your preferred editor
notepad "$HOME\.campussphere\campussphere.properties"
```
*In the file, set your `db.password` and a random `setup.key`.*

### Step 2: Initialize Database and Schema in MySQL
Copy the database creation template into the git-ignored local directory, configure your application user password, and execute the scripts:
```powershell
# 1. Create the local script folder (git-ignored)
New-Item -ItemType Directory -Path "database\local" -Force

# 2. Copy the user creation template
Copy-Item "database\00-create-database-and-user.sql.template" -Destination "database\local\00-create-database-and-user.sql"

# 3. Open and set your application user password in the local copy
notepad "database\local\00-create-database-and-user.sql"

# 4. Execute user creation as root (PowerShell requires -e source; '<' is not supported)
& "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "source database/local/00-create-database-and-user.sql"
# (Enter your MySQL root password when prompted)

# 5. Execute table schema creation
& "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "source database/01-schema.sql"
```

### Step 3: Build the Application WAR
From the project root (`C:\Users\Sreddha\CampusSphere`), build the WAR file:
```powershell
mvn clean package
```
Verify that the output displays `BUILD SUCCESS`.

### Step 4: Deploy and Run on Apache Tomcat
Deploy the built WAR artifact into Tomcat's `webapps` directory:
```powershell
# Copy WAR to Tomcat webapps
Copy-Item "target\campussphere.war" -Destination "C:\Users\Sreddha\tools\apache-tomcat-10.1.61\webapps\"

# Start Tomcat in a new console window
& "C:\Users\Sreddha\tools\apache-tomcat-10.1.61\bin\startup.bat"
```

### Step 5: Verify in Browser
Open your web browser and navigate to:
* **Home Page:** [http://localhost:8080/campussphere/](http://localhost:8080/campussphere/)
* **System Health Diagnostic:** [http://localhost:8080/campussphere/health](http://localhost:8080/campussphere/health)

### Step 6: Stopping Tomcat
When you are done testing, stop the server gracefully:
```powershell
& "C:\Users\Sreddha\tools\apache-tomcat-10.1.61\bin\shutdown.bat"
```

---

*Note: This README will be expanded with comprehensive user guides and test cases in Phase 10.*
