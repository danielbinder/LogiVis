## Development explanation and basic guidelines
### 'I want to make a change' checklist
1) Make your changes on a **new branch** (don't forget about tests)
    - Setup information can be viewed in the <a href='https://github.com/danielbinder/LogiVis/blob/main/README.md'>README</a>
    - Make sure your changes don't violate our <a href='https://github.com/danielbinder/LogiVis/blob/main/LICENSE'>LICENSE</a>
2) Run `./gradlew shadowJar` to compile changes
    - It only compiles if all tests run through
    - Test the `LogiVis.jar` you created
3) Push the branch to this repository and open a pull request
    - You need at least `1` approval from file owners
    - A linear history is required, so `git rebase` on the `remote/main` branch to avoid pushing any merges
4) Also change the following documents on your branch:
    - Version in the `build.gradle`
    - <a href='https://github.com/danielbinder/LogiVis/blob/main/Changelog.md'>Changelog</a> both your version and accumulated changes if necessary
    - <a href='https://github.com/danielbinder/LogiVis/blob/main/README.md'>README</a> if you changed anything about how the project is built or run
    - `screenshot.png` if necessary, but make it look a little interesting :)
    - <a href='https://github.com/danielbinder/LogiVis/blob/main/Development.md'>THIS FILE</a> if necessary
5) Choose the option with `Rebase` to merge the accepted pull request
### Code guidelines:
1) Your code should be
   - Self explaining, adding clarifying comments only where necessary
   - Object Oriented
   - Expandable
   - Usable for a purpose that is as generic as possible/sensible
   - Self-contained - it should not depend on implementation details of other packages and other packages should also not depend on your implementation. This is especially true for the information being sent over REST i.e. your implementation should not be specifically designed to be a String - **modeling your object structure according to the underlying concepts comes first and converting it from and to a String should be the afterthought!**
2) Write Tests!
   - Whenever you encounter a bug in your code, write a test case that reproduces it, so no one will ever run into that same bug! This also makes testing your fix faster.
### Explanation:
Write your code without thinking about sending it over REST.
The `Result` class in `./src/servlet` will take care of that.
- Use `System.out.println("My warning message");` for warnings
- Use `throw new [whatever type you want]Exception("My error message");` for errors i.e. states from which you can't recover

`Result` will capture your warnings and errors and send them to the front-end where they are displayed as such.
To send something over REST, as done in `Servlet` and `AlgorithmTester`, do the following:
- If your method is not in `Servlet` or `AlgorithmTester`, you need to add a main method which calls `REST.start()` to make the REST framework aware of your method.
  - Don't forget to also add your REST APIs everywhere, especially in `Main.main()` where everything is called from one place and all the READMEs.
  - It is highly encouraged to add `if(args.length > 0 && args[0].equals("DEV")) Result.DEV = true;` to your `main()` method to automatically print stack traces if your main is called in `DEV` mode.
  - Also add a run configuration to `./.run` to make it easier for others to execute your method. (In IntelliJ) you can do this by clicking on your run configuration on the top right next to the green play button `[Your run configuration] -> Edit Configurations -> Application -> [Your application configuration] -> Store as project file (on the top right)`
- Annotate your method with `@GET("/myUniquePath/:param1/:param2/:param3")`
  - Your method name and path name should be the same to avoid confusion and to avoid name conflicts.
- Create your method according to your parameters
  - Your parameters MUST BE in alphabetical order!!!
  - E.g. `public String myUniquePath(param1, param2, param3)`
- In the method, use `Result` for calling whatever you implemented
  - `Result` works with `Supplier<String>`, meaning anything that takes nothing and produces a `String`
  - To send it over REST, it needs to be converted to JSON format. This is done automatically in the `Result.computeJSON()` method. It also takes care of any newline characters you want to send over REST.
  - Be sure to never use illegal characters over REST, especially not `;`
  - The `REST.preprocess(String raw)` method takes care of preserving newline characters over REST, so use it! If you don't you might have a bunch of `$` symbols in your parameters.
  - E.g. `return new Result(() -> MyClass.myAwesomeMethod(preprocess(param1), preprocess(param2), preprocess(param2)).toString()).computeJSON();`
  - To also encode solution information, you can use a preliminary result state:
  - ```
    return new Result(() -> MyClass.myAwesomeMethod(preprocess(param1), preprocess(param2), preprocess(param2)),
                      result -> result.toString(),
                      result -> result.solutionInformationString())
                          .computeJSON();
    ```
  - If your solution should also be different in some cases, you can also add a Map of conditions and alternative strings like in this example taken from `Servlet.solveAll(formula)`:
  - ```
    return new Result(() -> new BruteForceSolver(preprocess(formula)),
                          BruteForceSolver::solveAll,
                          solver -> String.join("\n", solver.solutionInfo),
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable",
                                 solver -> solver.valid, "valid"))
                .computeJSON();
    ```
- In the front-end, the `Result JSON` is automatically parsed and filled into the respective fields
  - This is done in `./visualisation/src/App.jsx`
  - `setFormulaTab(data)` puts the result into the formula field
  - `setSolutionTab(data)` puts the result into the solution field
  - `setModelTab(data)` puts the result into the model field
  - Warnings and Errors are visible as status messages
  - The solution information, if present, is always put into the solution information field
  - If the solution information is so big that it could lag the front-end (> 500 lines), an automatic status message is added