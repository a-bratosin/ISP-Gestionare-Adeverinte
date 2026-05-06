How to run JUnit tests (plain javac / jars)

1) Download required jars and place them in the lib/ folder:
   - junit-4.13.2.jar
   - hamcrest-core-1.3.jar

   Example downloads:
   curl -L -o lib/junit-4.13.2.jar https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
   curl -L -o lib/hamcrest-core-1.3.jar https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar

2) Make the runner script executable and run it:
   chmod +x run-tests.sh
   ./run-tests.sh

3) VS Code notes:
   - `.vscode/settings.json` already references `lib/**/*.jar` so jars will be on the project classpath.
   - `TEST` is added to `java.project.sourcePaths` so the Java extension can find test sources.
   - Install the "Java Extension Pack" and "Java Test Runner" extensions for test UI and discovery.

4) Manual compile/run (example):
   javac -cp "lib/*:src" -d bin $(find src -name '*.java') TEST/DecanTest.java
   java -cp "bin:lib/*" org.junit.runner.JUnitCore DecanTest
