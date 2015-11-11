//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

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
		result.floatValue = java.lang.Float.MIN_VALUE;
		result.intValue = Integer.MIN_VALUE;
		result.stringValue = value;
		fillVarValue(result, value);

		return result;
	}

	protected abstract void fillVarValue(SpriterVarValue value, String string);
}