// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class IntPairMap<T> {

	private final IntMap<IntMap<T>> items = new IntMap<IntMap<T>>();

	public T get(int key1, int key2) {
		IntMap<T> subItems = items.get(key1);

		if (subItems == null)
			return null;

		return subItems.get(key2);
	}

	public T put(int key1, int key2, T value) {
		IntMap<T> subItems = items.get(key1);

		if (subItems == null) {
			subItems = new IntMap<T>();
			items.put(key1, subItems);
		}

		return subItems.put(key2, value);
	}

	public Array<T> values() {
		Array<T> values = new Array<T>();
		for (IntMap<T> subMap : items.values())
			for (T value : subMap.values())
				values.add(value);
		return values;
	}

}
