// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import static com.badlogic.gdx.spriter.FrameData.adjustTime;
import static com.badlogic.gdx.spriter.FrameData.getFactor;
import static com.badlogic.gdx.spriter.FrameData.getNextXLineKey;
import static com.badlogic.gdx.spriter.FrameData.lastKeyForTime;

import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterElement;
import com.badlogic.gdx.spriter.data.SpriterEventline;
import com.badlogic.gdx.spriter.data.SpriterKey;
import com.badlogic.gdx.spriter.data.SpriterMeta;
import com.badlogic.gdx.spriter.data.SpriterObjectInfo;
import com.badlogic.gdx.spriter.data.SpriterSound;
import com.badlogic.gdx.spriter.data.SpriterSoundline;
import com.badlogic.gdx.spriter.data.SpriterSoundlineKey;
import com.badlogic.gdx.spriter.data.SpriterTag;
import com.badlogic.gdx.spriter.data.SpriterTagline;
import com.badlogic.gdx.spriter.data.SpriterTaglineKey;
import com.badlogic.gdx.spriter.data.SpriterTimeline;
import com.badlogic.gdx.spriter.data.SpriterVarDef;
import com.badlogic.gdx.spriter.data.SpriterVarValue;
import com.badlogic.gdx.spriter.data.SpriterVarline;
import com.badlogic.gdx.spriter.data.SpriterVarlineKey;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class FrameMetadata {

	public static FrameMetadata create(SpriterAnimation first, SpriterAnimation second, float targetTime, float deltaTime, float factor) {
		return create(factor < 0.5f ? first : second, targetTime, deltaTime);
	}

	public static FrameMetadata create(SpriterAnimation animation, float targetTime, float deltaTime) {
		FrameMetadata metadata = new FrameMetadata();
		metadata.addVariableAndTagData(animation, targetTime);
		metadata.addEventData(animation, targetTime, deltaTime);
		metadata.addSoundData(animation, targetTime, deltaTime);
		return metadata;
	}

	public final ObjectMap<String, SpriterVarValue> animationVars = new ObjectMap<String, SpriterVarValue>();
	public final ObjectMap<String, ObjectMap<String, SpriterVarValue>> objectVars = new ObjectMap<String, ObjectMap<String, SpriterVarValue>>();
	public final Array<String> animationTags = new Array<String>();
	public final ObjectMap<String, Array<String>> objectTags = new ObjectMap<String, Array<String>>();
	public final Array<String> events = new Array<String>();
	public final Array<SpriterSound> sounds = new Array<SpriterSound>();

	private void addObjectVar(String objectName, String varName, SpriterVarValue value) {
		ObjectMap<String, SpriterVarValue> values = objectVars.get(objectName);
		if (values == null) {
			values = new ObjectMap<String, SpriterVarValue>();
			objectVars.put(objectName, values);
		}
		values.put(varName, value);
	}

	private void addObjectTag(String objectName, String tag) {
		Array<String> tags = objectTags.get(objectName);
		if (tags == null) {
			tags = new Array<String>();
			objectTags.put(objectName, tags);
		}
		tags.add(tag);
	}

	private void addVariableAndTagData(SpriterAnimation animation, float targetTime) {
		if (animation.meta == null)
			return;

		for (SpriterVarline varline : animation.meta.varlines) {
			SpriterVarDef variable = animation.entity.variables.get(varline.def);
			this.animationVars.put(variable.name, getVariableValue(animation, variable, varline, targetTime));
		}

		Array<SpriterElement> tags = animation.entity.data.tags;
		SpriterTagline tagline = animation.meta.tagline;

		if (tagline != null) {
			SpriterTaglineKey key = lastKeyForTime(tagline.keys, targetTime);

			if (key != null)
				for (SpriterTag tag : key.tags)
					this.animationTags.add(tags.get(tag.tagId).name);
		}

		for (SpriterTimeline timeline : animation.timelines) {
			SpriterMeta meta = timeline.meta;

			if (meta == null)
				continue;

			SpriterObjectInfo objInfo = getObjectInfo(animation, timeline.name);

			if (objInfo == null)
				continue;

			if (meta.varlines != null) {
				for (SpriterVarline varline : timeline.meta.varlines) {
					SpriterVarDef variable = objInfo.variables.get(varline.def);
					this.addObjectVar(objInfo.name, variable.name, getVariableValue(animation, variable, varline, targetTime));
				}
			}

			if (meta.tagline != null) {
				SpriterTaglineKey key = lastKeyForTime(tagline.keys, targetTime);

				if (key != null && key.tags != null)
					for (SpriterTag tag : key.tags)
						this.addObjectTag(objInfo.name, tags.get(tag.tagId).name);
			}
		}
	}

	private static SpriterVarValue getVariableValue(SpriterAnimation animation, SpriterVarDef varDef, SpriterVarline varline, float targetTime) {
		Array<SpriterVarlineKey> keys = varline.keys;

		if (keys == null)
			return varDef.variableValue;

		SpriterVarlineKey keyA = lastKeyForTime(keys, targetTime);

		if (keyA == null)
			keyA = keys.peek();

		if (keyA == null)
			return varDef.variableValue;

		SpriterVarlineKey keyB = getNextXLineKey(keys, keyA, animation.looping);

		if (keyB == null)
			return keyA.variableValue;

		float adjustedTime = keyA.time == keyB.time ? targetTime : adjustTime(keyA, keyB, animation.length, targetTime);
		float factor = getFactor(keyA, keyB, animation.length, adjustedTime);

		return interpolate(keyA.variableValue, keyB.variableValue, factor);
	}

	private void addEventData(SpriterAnimation animation, float targetTime, float deltaTime) {
		if (animation.eventlines == null)
			return;

		float previousTime = targetTime - deltaTime;
		for (SpriterEventline eventline : animation.eventlines)
			for (SpriterKey key : eventline.keys)
				if (isTriggered(key, targetTime, previousTime, animation.length))
					this.events.add(eventline.name);
	}

	private void addSoundData(SpriterAnimation animation, float targetTime, float deltaTime) {
		if (animation.soundlines == null)
			return;

		float previousTime = targetTime - deltaTime;
		for (SpriterSoundline soundline : animation.soundlines) {
			for (SpriterSoundlineKey key : soundline.keys) {
				SpriterSound sound = key.soundObject;
				if (sound.trigger && isTriggered(key, targetTime, previousTime, animation.length))
					this.sounds.add(sound);
			}
		}
	}

	private static boolean isTriggered(SpriterKey key, float targetTime, float previousTime, float animationLength) {
		float min = Math.min(previousTime, targetTime);
		float max = Math.max(previousTime, targetTime);

		if (min > max) {
			if (min < key.time)
				max += animationLength;
			else
				min -= animationLength;
		}
		return min <= key.time && max >= key.time;
	}

	private static SpriterObjectInfo getObjectInfo(SpriterAnimation animation, String name) {
		SpriterObjectInfo objInfo = null;
		for (SpriterObjectInfo info : animation.entity.objectInfos) {
			if (info.name.equals(name)) {
				objInfo = info;
				break;
			}
		}

		return objInfo;
	}

	private static SpriterVarValue interpolate(SpriterVarValue valA, SpriterVarValue valB, float factor) {
		SpriterVarValue value = new SpriterVarValue();

		value.type = valA.type;
		value.stringValue = valA.stringValue;
		value.floatValue = MathHelper.linear(valA.floatValue, valB.floatValue, factor);
		value.intValue = (int) MathHelper.linear(valA.intValue, valB.intValue, factor);

		return value;
	}

	@Override
	public String toString() {
		return "FrameMetadata [animationVars=" + animationVars + ", objectVars=" + objectVars + ", animationTags=" + animationTags + ", objectTags=" + objectTags + ", events=" + events + ", sounds=" + sounds + "]";
	}

}
