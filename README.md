# LintfordLib

A Java game library containing LWJGL natives and a game state management framework.

## Notes on Usage

### Coordinate System

LintfordLib uses a right-handed coordinate system with an inverted Y-Axis. The View and Projection matrices are created and maintained from with the camera classes (`Camera.java`, `HUD.java` and `RTCamera.java`) - or you can create your own matrices using `Matrix4f.java`.

![right handed coordinate system with inverted Y-Axis](docs/images/coordsys.png?raw=true "Right-handed coordinate system with inverted y-axis")

The `HUD.java` class maintains the view and projection matrices for use within the screenmanager and 2d menu systems. The view matrix sits at (0,0,0) and is oriented along (0,0,1), with a zNear and zFar of (0, 10) respectively:

![the view matrix and its orientation along the z-axis](docs/images/viewmat.png?raw=true "The view matrix.")

The projection matrix is an orthogonal projection which aligns with the desired ui resolution as provided in the `GameInfo.java` instance used when instantiating the main `LintfordLib.java` instance.

[!NOTE]
The use of a right-handed coordinate system and an inverted Y-Axis means that rotations made in 2d along the Z-Axis will be CW as opposed to CCW.

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
 
 
