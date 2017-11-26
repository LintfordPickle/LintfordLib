package net.lintford.library.core.noise;

import java.util.Random;

import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.Vector2f;

// http://mrl.nyu.edu/~perlin/paper445.pdf
// http://mrl.nyu.edu/~perlin/noise/
public final class ImprovedNoise {

	// --------------------------------------
	// Constants
	// --------------------------------------

	final static int TABLE_SIZE = 64;
	final int SCALE_WIDTH = 128;
	final float MIN_SIZE = 0.1f;
	final float MAX_SIZE = 16.0f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean tilable = false;
	private boolean turbulent = false;
	private int detail = 1;
	private float size = 8.0f;

	private static int clip;
	private static float offset, factor;
	static int[] perm_tab = new int[TABLE_SIZE];
	static Vector2f[] grad_tab = new Vector2f[TABLE_SIZE];
	Vector2f v = new Vector2f();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ImprovedNoise(long seed) {
		init(seed);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float noise2(float x, float y) {
		x /= 100;
		y /= 100;
		return noise(x, y);
	}

	public void init(long pSeed) {
		int i, j, k, t;
		float m;
		Random r = new Random(pSeed);

		/* Force sane parameters */
		detail = MathHelper.clampi(detail, 0, 15);
		size = MathHelper.clamp(size, MIN_SIZE, MAX_SIZE);

		/* Set scaling factors */
		if (tilable) {
			this.size = (float) Math.ceil(size);
			clip = (int) size;
		}

		/* Set totally empiric normalization values */
		if (turbulent) {
			offset = 0.0f;
			factor = 1.0f;
		} else {
			offset = 0.94f;
			factor = 0.526f;
		}

		/* Initialize the permutation table */
		for (i = 0; i < TABLE_SIZE; i++)
			perm_tab[i] = i;

		for (i = 0; i < (TABLE_SIZE >> 1); i++) {
			j = r.nextInt(TABLE_SIZE);
			k = r.nextInt(TABLE_SIZE);
			t = perm_tab[j];
			perm_tab[j] = perm_tab[k];
			perm_tab[k] = t;
		}

		/* Initialize the gradient table */
		for (i = 0; i < TABLE_SIZE; i++) {
			grad_tab[i] = new Vector2f();
			do {
				grad_tab[i].x = ((r.nextFloat() * 2f) - 1f);
				grad_tab[i].y = ((r.nextFloat() * 2f) - 1f);
				m = grad_tab[i].x * grad_tab[i].x + grad_tab[i].y * grad_tab[i].y;
			} while (m == 0.0 || m > 1.0);

			m = 1.0f / (float) Math.sqrt(m);
			grad_tab[i].x = (grad_tab[i].x * m);
			grad_tab[i].y = (grad_tab[i].y * m);
		}

		r = null;
	}

	private double plain_noise(float x, float y, int s) {
		int a, b, i, j, n;
		float sum;

		sum = 0.0f;
		x *= s;
		y *= s;
		a = (int) Math.floor(x);
		b = (int) Math.floor(y);

		for (i = 0; i < 2; i++) {
			for (j = 0; j < 2; j++) {
				if (tilable)
					n = perm_tab[betterMod((betterMod((a + i), (clip * s)) + perm_tab[betterMod(betterMod((b + j), (clip * s)), TABLE_SIZE)]), TABLE_SIZE)];
				else
					n = perm_tab[betterMod(a + i + perm_tab[betterMod(b + j, TABLE_SIZE)], TABLE_SIZE)];
				v.x = (x - a - i);
				v.y = (y - b - j);
				sum += MathHelper.weight(v.x) * MathHelper.weight(v.y) * (grad_tab[n].x * v.x + grad_tab[n].y * v.y);
			}

		}

		return sum / s;
	}

	private float noise(float x, float y) {
		int i;
		int s;
		float sum;

		s = 1;
		sum = 0.0f;
		x *= size;
		y *= size;

		for (i = 0; i <= detail; i++) {
			if (turbulent)
				sum += Math.abs(plain_noise(x, y, s));
			else
				sum += plain_noise(x, y, s);
			s <<= 1;
		}

		return (sum + offset) * factor;
	}

	/** Modified modulus, so that negative numbers wrap correctly! */
	private int betterMod(int val, int range) {
		return (val % range + range) % range;
	}

}