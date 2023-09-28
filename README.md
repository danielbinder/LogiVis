# <img src="https://github.com/danielbinder/LogiVis/assets/59804199/05a08228-5cf0-4d2f-8c0b-685ac991022f" alt="LogiVis Logo" width="25" height="25">  LogiVis
LogiVis is a teaching aid for concepts and algorithms in logic.
#### Recommended IDEs:
- `IntelliJ IDEA` for Java
- `WebStorm` for React
### For AlgorithmTesters:
1) Set your IDE to use `Java 21+`
   - You can download it from <a href='https://aws.amazon.com/corretto/'>here</a>
2) Start the run configuration `Servlet (DEV)`
   - It handles everything except the `Test your algorithm` card.
3) Start the front-end with `TODO`
4) Add your algorithm implementations in `./src/algorithmTester/YourImplementation`
   - Alternatively, run `Servlet.main()` with the argument `DEV`
   - In case you encounter an illegal state in your program, don't be hesitant to throw an Exception.
   - `throw new Exception(...)` will show up in the front-end as Error (RED)
   - `System.out.println(...)` will show up in the front-end as Warning (YELLOW)
5) Start the run configuration `AlgorithmTester (DEV)`
   - Alternatively, run `AlgorithmTester.main()` with the argument `DEV`
   - This runs your implementation in DEV mode
     - In DEV mode, all Exceptions are caught, but stacktraces are still printed.
6) Test your algorithm in the front-end
   - For this, use `Test your algorithm`
   - `Apply algorithm` applies the sample implementation.
### For contributors:
1) Set your IDE to use `Java 21+`
   - You can download it from <a href='https://aws.amazon.com/corretto/'>here</a>
2) Start `./src/main/Main.main(String[] args)`
   - You can use the existing run configuration `Main (DEV)`
     - In DEV mode, all Exceptions are caught, but stacktraces are still printed.
   - Starts `Servlet.main(String[] args)`, `AlgorithmTester.main(String[] args)` and `npm start`
     - There are also run configurations for that
     - `Servlet (DEV)`, `AlgorithmTester (DEV)` and `npm start`
#### Compile to JAR
Command: `./gradlew shadowJar` <br>
JAR Location: `./build/libs/` <br>
#### Compile to EXE
1) Download the GraalVM JDK 21 <a href='https://www.graalvm.org/downloads/'>here</a>
2) Follow the steps described <a href='https://www.graalvm.org/latest/reference-manual/native-image/#prerequisites'>here</a>
3) Run the command `./gradlew nativeCompile`
   - The location of the .exe is in `TODO`
#### Execute JAR
Command: `java -jar LogiVis-[VERSION]-all.jar` <br>