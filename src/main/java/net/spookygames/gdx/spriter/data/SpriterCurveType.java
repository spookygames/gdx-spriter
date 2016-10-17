/**
 * Copyright (c) 2015-2016 Spooky Games
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgement in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package net.spookygames.gdx.spriter.data;

import net.spookygames.gdx.spriter.MathHelper;

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