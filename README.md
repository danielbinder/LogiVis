# <img src="https://github.com/danielbinder/LogiVis/blob/main/visualisation/public/LogiVis.png" alt="LogiVis Logo" width="25" height="25">  LogiVis
LogiVis is a teaching aid for concepts and algorithms in logic.

<img src="https://github.com/danielbinder/LogiVis/blob/main/screenshot.png" alt="LogiVis screenshot">

* View the latest features in the <a href='https://github.com/danielbinder/LogiVis/blob/main/Changelog.md'>Changelog</a>
* **On Windows**, simply download and run the `LogiVis.exe` file
* You can run the jar with `java -jar LogiVis-[VERSION].jar` from the folder where the jar is located
   * You can see how the setup is done in <a href='https://youtu.be/lzKHhATYbmM'>this video</a>
   * Requires <a href='https://aws.amazon.com/corretto/'>Java 21</a>
   * Set your `JAVA_HOME` environment variable to the downloaded JDK folder (don't forget to unzip it after downloading) e.g. `C:\Users\[yourUser]\.jdks\[name of the downloaded jdk folder]`
   * Set your `PATH` environment variable to the JDK/bin folder (where the java.exe lies) e.g. `C:\Users\[yourUser]\.jdks\[name of the downloaded jdk folder]\bin`
   * There might be other java paths in your `PATH` variable that you need to remove!

---
### Recommended development software:
- `IntelliJ IDEA` for Java back-end
- `WebStorm` for React front-end
- `Git Extensions` for Git operations
### For algorithm testers:
You can see how the setup is done in <a href='https://youtu.be/UXu0TBOh53U'>this video</a>
1) Set your IDE to use `Java 21+`
   - You can download it from <a href='https://aws.amazon.com/corretto/'>here</a>
2) Set the downloaded JDK to your project JDK
   - In IntelliJ `File -> Project Structure -> Project`
3) Set up your gradle in the following way (IntelliJ)
   - In `File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle`
   - Build and run using: `Gradle (Default)`
   - Run tests using: `IntelliJ IDEA`
   - Distribution: `Wrapper`
   - Gradle JVM: `Project SDK`
4) Add your algorithm implementations in `./src/main/java/algorithmTester/YourImplementation`
   - In case you encounter an illegal state in your program, don't be hesitant to throw an Exception.
   - `throw new Exception(...)` will show up in the front-end as Error (RED)
   - `System.out.println(...)` will show up in the front-end as Warning (YELLOW)
5) Start the run configuration `Main (DEV)`
   - This runs your implementation in DEV mode
6) Test your algorithm in the front-end
   - For this, use `Apply algorithm` or `Apply your own`
   - `Apply algorithm` applies the sample implementation - NOT your implementation.
### For contributors:
#### Back-end:
1) Set your IDE to use `Java 21+`
   - You can download it from <a href='https://aws.amazon.com/corretto/'>here</a>
2) Set your `JAVA_HOME` environment variable to the JDK folder
   - NOT the `/bin` folder!
3) Set the downloaded JDK to your project JDK
   - In IntelliJ `File -> Project Structure -> Project`
4) Set up your gradle in the following way (IntelliJ)
   - In `File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle`
   - Build and run using: `Gradle (Default)`
   - Run tests using: `IntelliJ IDEA`
   - Distribution: `Wrapper`
   - Gradle JVM: `Project SDK`
5) Make your changes
   - Don't forget to also write Tests!!!
   - At least every time you encounter a bug in your code, write a test case for it that tests this specific bug. This way, you and other will never miss this bug in the future.
   - You can find the full dev guidelines  with explanations here: `Development.md`
6) Start `./src/main/java/main/Main.main(String[] args)`
   - You can use the existing run configuration `Main (DEV)`
     - In DEV mode, all Exceptions are caught, but stacktraces are still printed.
7) ALWAYS* increment AT LEAST the patch version number e.g. `5.2.3 -> 5.2.4` in the `./build.gradle`
   - *Except for bug fixes - they just accumulate until the next version is released
   - If you added an entire module, increase the minor version e.g. `5.2.3 -> 5.3.0`
   - If the whole application is different, increase the major version e.g. `5.2.3 -> 6.0.0`
   - Don't forget to reload your gradle changes (in IntelliJ, there is a pop-up on the top right with the gradle elephant)
   - Don't ignore gradle build errors (visible in IntelliJ on the bottom under the `build` tab)!
#### Compile to JAR
- Open the Terminal (on the bottom or left side in IntelliJ) run `./gradlew booJar`
   - It compiles the front-end and pulls it into the JAR when compiling the back-end.
   - If your front-end does not build, consider downloading the latest version of node (manually from the website)
   - The JAR is located in `./build/libs/`
   - You can test your Jar with `java -jar LogiVis-[VERSION].jar`
     - If this does not work, your `path` environment variable is messed up. You can still run it using the full paths of the `java.exe` in your JDK and the full path of the `LogiVis.jar`, both wrapped in double quotes.
   - Or by right-clicking it in IntelliJ: `RMB -> Run '[NAME].jar'`
#### Compile to Executable
- Use the graalvm 21+ as your JDK (e.g. from <a href='https://www.graalvm.org/downloads/'>here</a>)
- <a href='https://www.graalvm.org/22.0/reference-manual/native-image/#prerequisites'>Prerequisites</a>
- On windows, <a href='https://stackoverflow.com/a/75348946'>this may help</a>
- Open the Terminal (on the bottom or left side in IntelliJ) run `./gradlew nativeCompile`
- Test your executable by running it and verify that the frontend is served and all REST controllers are reachable
#### Front-end:
1) Open the `./src/main/javascript` folder as your project
2) Run `npm start` to start the dev environment
    - This may take a minute
3) Make your changes
    - NO need to run `npm start` every time you make changes. Just saving your changes (`CTRL + S`) should be enough to apply them in the front-end.
4) To compile it into a JAR, follow the steps above.
