# LintfordLib

A Java game library containing LWJGL natives and a game state management framework.

## Usuage

TODO :)


## Unittests

Unit tests are written using the [JUnit 5 testing framework](https://junit.org/junit5/).

A unit test's method name should contain the following three parts:

 * The unit under test
 * The scenario that's being tested
 * The expected return value

Each part should be separated by an underscore:

``` Java
MethodUnderTest_ScenarioBeingTested_ExpectedResult()
```

For Example 

``` Java
clampi_ChecksIntegerValueAgainstLowerAndUpperBound_ShouldReturnTheUnmodifiedInputValue()
```


Unittests are written using the AAA (arrange, act, assert) pattern, with each section:

 * The Arrange section of a unit test method initializes objects and sets the value of the data that is passed to the method under test.
 * The Act section invokes the method under test with the arranged parameters.
 * The Assert section verifies that the action of the method under test behaves as expected. For .NET, methods in the Assert class are often used for verification.
 
 
