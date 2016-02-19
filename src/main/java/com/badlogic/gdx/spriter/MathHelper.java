// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.
package com.badlogic.gdx.spriter;

/**
 * A handful of helpers for all things maths.
 * 
 * @author thorthur
 * 
 */
public class MathHelper {

	public static float angleLinear(float a, float b, int spin, float f) {
		if (spin == 0)
			return a;

		if (spin > 0 && (b - a) < 0)
			b += 360.0f;

		if (spin < 0 && (b - a) > 0)
			b -= 360.0f;

		return linear(a, b, f);
	}

	public static float closerAngleLinear(float a, float b, float factor) {
		if (Math.abs(b - a) < 180.0f)
			return linear(a, b, factor);

		if (a < b)
			a += 360.0f;
		else
			b += 360.0f;

		return linear(a, b, factor);
	}

	public static float reverseLinear(float a, float b, float v) {
		return (v - a) / (b - a);
	}

	public static float linear(float a, float b, float f) {
		return a + (b - a) * f;
	}

	public static float curve(float f, float... c) {
		for (int i = c.length - 1; i > 0; --i)
			for (int j = 0; j < i; ++j)
				c[j] = linear(c[j], c[j + 1], f);

		return c[0];
	}

	public static float bezier(float x1, float y1, float x2, float y2, float t) {
		float duration = 1;
		float cx = 3.0f * x1;
		float bx = 3.0f * (x2 - x1) - cx;
		float ax = 1.0f - cx - bx;
		float cy = 3.0f * y1;
		float by = 3.0f * (y2 - y1) - cy;
		float ay = 1.0f - cy - by;

		return solve(ax, bx, cx, ay, by, cy, t, solveEpsilon(duration));
	}

	static float sampleCurve(float a, float b, float c, float t) {
		return ((a * t + b) * t + c) * t;
	}

	static float sampleCurveDerivativeX(float ax, float bx, float cx, float t) {
		return (3.0f * ax * t + 2.0f * bx) * t + cx;
	}

	static float solveEpsilon(float duration) {
		return 1.0f / (200.0f * duration);
	}

	static float solve(float ax, float bx, float cx, float ay, float by, float cy, float x, float epsilon) {
		return sampleCurve(ay, by, cy, solveCurveX(ax, bx, cx, x, epsilon));
	}

	static float solveCurveX(float ax, float bx, float cx, float x, float epsilon) {
		float t0;
		float t1;
		float t2;
		float x2;
		float d2;
		int i;

		// First try a few iterations of Newton's method -- normally very fast.
		for (t2 = x, i = 0; i < 8; i++) {
			x2 = sampleCurve(ax, bx, cx, t2) - x;

			if (Math.abs(x2) < epsilon)
				return t2;

			d2 = sampleCurveDerivativeX(ax, bx, cx, t2);

			if (Math.abs(d2) < 1e-6)
				break;

			t2 = t2 - x2 / d2;
		}

		// Fall back to the bisection method for reliability.
		t0 = 0.0f;
		t1 = 1.0f;
		t2 = x;

		if (t2 < t0)
			return t0;

		if (t2 > t1)
			return t1;

		while (t0 < t1) {
			x2 = sampleCurve(ax, bx, cx, t2);

			if (Math.abs(x2 - x) < epsilon)
				return t2;

			if (x > x2)
				t0 = t2;
			else
				t1 = t2;

			t2 = (t1 - t0) * 0.5f + t0;
		}

		return t2; // Failure.
	}

}
