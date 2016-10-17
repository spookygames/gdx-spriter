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

public enum SpriterVarType {
	String {
		@Override
		protected void fillVarValue(SpriterVarValue value, java.lang.String string) {
		}
	},
	Int {
		@Override
		protected void fillVarValue(SpriterVarValue value, java.lang.String string) {
			value.intValue = Integer.parseInt(string, 10);
		}
	},
	Float {
		@Override
		protected void fillVarValue(SpriterVarValue value, java.lang.String string) {
			value.floatValue = java.lang.Float.parseFloat(string);
		}
	};

	public static SpriterVarType parse(String text) {
		if (text != null)
			for (SpriterVarType t : values())
				if (text.equalsIgnoreCase(t.name()))
					return t;
		return null;
	}

	public SpriterVarValue buildVarValue(String value) {

		SpriterVarValue result = new SpriterVarValue();
		result.type = this;
		result.stringValue = value;
		fillVarValue(result, value);

		return result;
	}

	protected abstract void fillVarValue(SpriterVarValue value, String string);
}