package tests.net.lintfordlib.core.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.lintfordlib.core.geometry.Rectangle;

public class RectangleTests {

	@Test
	void intersectsAA_ChecksPointAtOriginIntersectsRectangle_ShouldReturnTrue() {
		// arrange
		final var pointX = 0.f;
		final var pointY = 0.f;
		final var rectangle = new Rectangle(-128.f, -128.f, 256.f, 256.f);

		// act
		final var result = rectangle.intersectsAA(pointX, pointY);

		// assert
		assertEquals(true, result, "intersectsAA didn't return the expected value.");
	}

	@Test
	void intersectsAA_ChecksPointIntersectsRectangle_ShouldReturnTrue() {
		// arrange
		final var pointX = -100.f;
		final var pointY = 50.f;
		final var rectangle = new Rectangle(-128.f, -128.f, 256.f, 256.f);

		// act
		final var result = rectangle.intersectsAA(pointX, pointY);

		// assert
		assertEquals(true, result, "intersectsAA didn't return the expected value.");
	}

	@Test
	void intersectsAA_ChecksPointIntersectsRectangle_ShouldReturnFalse_01() {
		// arrange
		final var pointX = 150.f;
		final var pointY = 50.f;
		final var rectangle = new Rectangle(-128.f, -128.f, 256.f, 256.f);

		// act
		final var result = rectangle.intersectsAA(pointX, pointY);

		// assert
		assertEquals(false, result, "intersectsAA didn't return the expected value.");
	}

	@Test
	void intersectsAA_ChecksPointIntersectsRectangle_ShouldReturnFalse_02() {
		// arrange
		final var pointX = -32.f;
		final var pointY = 250.f;
		final var rectangle = new Rectangle(-128.f, -128.f, 256.f, 256.f);

		// act
		final var result = rectangle.intersectsAA(pointX, pointY);

		// assert
		assertEquals(false, result, "intersectsAA didn't return the expected value.");
	}

	@Test
	void intersectsAA_ChecksRectangleIntersectsRectangle_ShouldReturnTrue_00() {
		// arrange
		final var pointX = -25.875f;
		final var pointY = 31.5f;
		final var pointW = 59.25f;
		final var pointH = 22.5f;
		final var rectangle = new Rectangle(-128.f, -128.f, 256.f, 256.f);

		// act
		final var result = rectangle.intersectsAA(pointX, pointY, pointW, pointH);

		// assert
		assertEquals(true, result, "intersectsAA didn't return the expected value.");
	}
	
	@Test
	void intersectsAA_ChecksRectangleIntersectsRectangle_ShouldReturnTrue_01() {
		// arrange
		final var pointX = -150.875f;
		final var pointY = 31.5f;
		final var pointW = 64.25f;
		final var pointH = 22.5f;
		final var rectangle = new Rectangle(-128.f, -128.f, 256.f, 256.f);

		// act
		final var result = rectangle.intersectsAA(pointX, pointY, pointW, pointH);

		// assert
		assertEquals(true, result, "intersectsAA didn't return the expected value.");
	}
	
	@Test
	void intersectsAA_ChecksRectangleIntersectsRectangle_ShouldReturnTrue_02() {
		// arrange
		final var pointX = -135.875f;
		final var pointY = 120.5f;
		final var pointW = 10.25f;
		final var pointH = 10.5f;
		final var rectangle = new Rectangle(-128.f, -128.f, 256.f, 256.f);

		// act
		final var result = rectangle.intersectsAA(pointX, pointY, pointW, pointH);

		// assert
		assertEquals(true, result, "intersectsAA didn't return the expected value.");
	}

}
