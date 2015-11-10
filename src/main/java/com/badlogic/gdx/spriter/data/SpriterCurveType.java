//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.spriter.MathHelper;

public enum SpriterCurveType {
	Instant {
		@Override
		public float applySpeedCurve(SpriterKey key, float factor) {
			return 0.0f;
		}
	},
	Linear {
		@Override
		public float applySpeedCurve(SpriterKey key, float factor) {
			return factor;
		}
	},
	Quadratic {
		@Override
		public float applySpeedCurve(SpriterKey key, float factor) {
			return MathHelper.curve(factor, 0.0f, key.c1, 1.0f);
		}
	},
	Cubic {
		@Override
		public float applySpeedCurve(SpriterKey key, float factor) {
			return MathHelper.curve(factor, 0.0f, key.c1, key.c2, 1.0f);
		}
	},
	Quartic {
		@Override
		public float applySpeedCurve(SpriterKey key, float factor) {
			return MathHelper.curve(factor, 0.0f, key.c1, key.c2, key.c3, 1.0f);
		}
	},
	Quintic {
		@Override
		public float applySpeedCurve(SpriterKey key, float factor) {
			return MathHelper.curve(factor, 0.0f, key.c1, key.c2, key.c3, key.c4, 1.0f);
		}
	},
	Bezier {
		@Override
		public float applySpeedCurve(SpriterKey key, float factor) {
			return MathHelper.bezier(key.c1, key.c2, key.c3, key.c4, factor);
		}
	};

	public static SpriterCurveType parse(String text) {
		if (text != null)
			for (SpriterCurveType t : values())
				if (text.equalsIgnoreCase(t.name()))
					return t;
		return null;
	}

	public abstract float applySpeedCurve(SpriterKey key, float factor);
}