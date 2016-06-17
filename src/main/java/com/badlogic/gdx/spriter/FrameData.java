// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterElement;
import com.badlogic.gdx.spriter.data.SpriterEventline;
import com.badlogic.gdx.spriter.data.SpriterFileInfo;
import com.badlogic.gdx.spriter.data.SpriterKey;
import com.badlogic.gdx.spriter.data.SpriterMainlineKey;
import com.badlogic.gdx.spriter.data.SpriterMeta;
import com.badlogic.gdx.spriter.data.SpriterObject;
import com.badlogic.gdx.spriter.data.SpriterObjectInfo;
import com.badlogic.gdx.spriter.data.SpriterObjectRef;
import com.badlogic.gdx.spriter.data.SpriterRef;
import com.badlogic.gdx.spriter.data.SpriterSound;
import com.badlogic.gdx.spriter.data.SpriterSoundline;
import com.badlogic.gdx.spriter.data.SpriterSoundlineKey;
import com.badlogic.gdx.spriter.data.SpriterSpatial;
import com.badlogic.gdx.spriter.data.SpriterTag;
import com.badlogic.gdx.spriter.data.SpriterTagline;
import com.badlogic.gdx.spriter.data.SpriterTaglineKey;
import com.badlogic.gdx.spriter.data.SpriterTimeline;
import com.badlogic.gdx.spriter.data.SpriterTimelineKey;
import com.badlogic.gdx.spriter.data.SpriterVarDef;
import com.badlogic.gdx.spriter.data.SpriterVarValue;
import com.badlogic.gdx.spriter.data.SpriterVarline;
import com.badlogic.gdx.spriter.data.SpriterVarlineKey;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * The {@code FrameData} class represents data to be displayed on a single frame
 * by a {@link SpriterAnimator}: sprites, points, boxes, sounds, events,
 * variables and tags.
 * 
 * {@code FrameData} instance is refreshed by
 * {@link SpriterAnimator#update(float deltaTime)} and displayed by
 * {@link SpriterAnimator#draw(Batch batch, ShapeRenderer renderer)}. As such,
 * any intended modification to a {@code FrameData} instance should be performed
 * between these two calls.
 * 
 * @see SpriterAnimator
 * 
 * @author thorthur
 * 
 */
public class FrameData {

	/**
	 * Update an instance of {@code FrameData} for blended display given the two
	 * {@link SpriterAnimation}s to blend, the target time and a weight factor
	 * between the two animations.
	 * 
	 * @param frameData
	 *            Instance of {@code FrameData} that will contain blended
	 *            information between first and second, weighted by factor and
	 *            targeted at targetTime with current deltaTime.
	 * @param configuration
	 *            Update configuration, specifying which fields of frameData
	 *            should actually be updated
	 * @param first
	 *            First animation to display
	 * @param second
	 *            Second animation to display, if first == second then no
	 *            blending takes place and factor is of no use
	 * @param targetTime
	 *            Target animation time (Spriter time)
	 * @param deltaTime
	 *            Current delta time (Gdx delta time)
	 * @param factor
	 *            Weight factor between first and second, should be between 0
	 *            (display first only) and 1 (display second only)
	 */
	static void update(FrameData frameData, FrameDataUpdateConfiguration configuration, SpriterAnimation first, SpriterAnimation second, float targetTime, float deltaTime, float factor) {

		if (first == second) {
			// Don't bother blending if the two animations are equal
			update(frameData, configuration, first, targetTime, deltaTime);
			return;
		}

		float targetTimeSecond = targetTime / first.length * second.length;

		SpriterMainlineKey[] keys = getMainlineKeys(first.mainline.keys, targetTime);
		SpriterMainlineKey firstKeyA = keys[0];
		SpriterMainlineKey firstKeyB = keys[1];

		keys = getMainlineKeys(second.mainline.keys, targetTimeSecond);
		SpriterMainlineKey secondKeyA = keys[0];
		SpriterMainlineKey secondKeyB = keys[1];

		if (firstKeyA.boneRefs.size != secondKeyA.boneRefs.size || firstKeyB.boneRefs.size != secondKeyB.boneRefs.size || firstKeyA.objectRefs.size != secondKeyA.objectRefs.size || firstKeyB.objectRefs.size != secondKeyB.objectRefs.size) {
			// Cannot blend if the two animations are not blendable
			update(frameData, configuration, first, targetTime, deltaTime);
			return;
		}

		frameData.clear();

		// Define reference animation
		SpriterAnimation currentAnimation = factor < 0.5f ? first : second;

		if (configuration.spatial) {
			float adjustedTimeFirst = adjustTime(firstKeyA, firstKeyB, first.length, targetTime);
			float adjustedTimeSecond = adjustTime(secondKeyA, secondKeyB, second.length, targetTimeSecond);

			SpriterSpatial[] boneInfosA = getBoneInfos(firstKeyA, first, adjustedTimeFirst);
			SpriterSpatial[] boneInfosB = getBoneInfos(secondKeyA, second, adjustedTimeSecond);
			SpriterSpatial[] boneInfos = null;

			if (boneInfosA != null && boneInfosB != null) {
				boneInfos = new SpriterSpatial[boneInfosA.length];
				for (int i = 0; i < boneInfosA.length; ++i) {
					SpriterSpatial boneA = boneInfosA[i];
					SpriterSpatial boneB = boneInfosB[i];
					SpriterSpatial interpolated = interpolate(boneA, boneB, factor, 1);
					interpolated.angle = MathHelper.closerAngleLinear(boneA.angle, boneB.angle, factor);
					boneInfos[i] = interpolated;
				}
			}

			SpriterMainlineKey baseKey = factor < 0.5f ? firstKeyA : firstKeyB;

			for (int i = 0; i < baseKey.objectRefs.size; ++i) {
				SpriterObjectRef objectRefFirst = baseKey.objectRefs.get(i);
				SpriterObject interpolatedFirst = getObjectInfo(objectRefFirst, first, adjustedTimeFirst);

				SpriterObjectRef objectRefSecond = secondKeyA.objectRefs.get(i);
				SpriterObject interpolatedSecond = getObjectInfo(objectRefSecond, second, adjustedTimeSecond);

				SpriterObject info = interpolate(interpolatedFirst, interpolatedSecond, factor, 1);
				info.angle = MathHelper.closerAngleLinear(interpolatedFirst.angle, interpolatedSecond.angle, factor);
				info.pivotX = MathHelper.linear(interpolatedFirst.pivotX, interpolatedSecond.pivotX, factor);
				info.pivotY = MathHelper.linear(interpolatedFirst.pivotY, interpolatedSecond.pivotY, factor);

				if (boneInfos != null && objectRefFirst.parentId >= 0)
					applyParentTransform(info, boneInfos[objectRefFirst.parentId]);

				frameData.addSpatialData(configuration, info, currentAnimation.timelines.get(objectRefFirst.timelineId), currentAnimation.entity.data, deltaTime);
			}
		}

		if (configuration.tagsAndVariables)
			frameData.addVariableAndTagData(currentAnimation, targetTime);

		if (configuration.events)
			frameData.addEventData(currentAnimation, targetTime, deltaTime);

		if (configuration.sounds)
			frameData.addSoundData(currentAnimation, targetTime, deltaTime);

	}

	/**
	 * Update an instance of {@code FrameData} to display given
	 * {@link SpriterAnimation} at given time.
	 * 
	 * @param frameData
	 *            Instance of {@code FrameData} that will contain display
	 *            information for animation at targetTime with current
	 *            deltaTime.
	 * @param configuration
	 *            Update configuration, specifying which fields of frameData
	 *            should actually be updated
	 * @param animation
	 *            Animation to display
	 * @param targetTime
	 *            Target animation time (Spriter time)
	 * @param deltaTime
	 *            Current delta time (Gdx delta time)
	 */
	static void update(FrameData frameData, FrameDataUpdateConfiguration configuration, SpriterAnimation animation, float targetTime, float deltaTime) {
		update(frameData, configuration, animation, targetTime, deltaTime, null);
	}

	/**
	 * Update an instance of {@code FrameData} to display given
	 * {@link SpriterAnimation} at given time, relative to given
	 * {@link SpriterSpatial} parent information.
	 * 
	 * @param frameData
	 *            Instance of {@code FrameData} that will contain display
	 *            information for animation at targetTime with current
	 *            deltaTime, relative to parentInfo.
	 * @param configuration
	 *            Update configuration, specifying which fields of frameData
	 *            should actually be updated
	 * @param animation
	 *            Animation to display
	 * @param targetTime
	 *            Target animation time (Spriter time)
	 * @param deltaTime
	 *            Current delta time (Gdx delta time)
	 * @param parentInfo
	 *            Spatial information that acts as reference for animation
	 */
	static void update(FrameData frameData, FrameDataUpdateConfiguration configuration, SpriterAnimation animation, float targetTime, float deltaTime, SpriterSpatial parentInfo) {

		frameData.clear();

		if (configuration.spatial) {
			Array<SpriterMainlineKey> keys = animation.mainline.keys;
			SpriterMainlineKey[] someKeys = getMainlineKeys(keys, targetTime);
			SpriterMainlineKey keyA = someKeys[0];
			SpriterMainlineKey keyB = someKeys[1];

			float adjustedTime = adjustTime(keyA, keyB, animation.length, targetTime);

			SpriterSpatial[] boneInfos = getBoneInfos(keyA, animation, adjustedTime, parentInfo);

			for (SpriterObjectRef objectRef : keyA.objectRefs) {
				SpriterObject interpolated = getObjectInfo(objectRef, animation, adjustedTime);

				if (boneInfos != null && objectRef.parentId >= 0)
					applyParentTransform(interpolated, boneInfos[objectRef.parentId]);

				frameData.addSpatialData(configuration, interpolated, animation.timelines.get(objectRef.timelineId), animation.entity.data, deltaTime);
			}
		}

		if (configuration.tagsAndVariables)
			frameData.addVariableAndTagData(animation, targetTime);
		if (configuration.events)
			frameData.addEventData(animation, targetTime, deltaTime);
		if (configuration.sounds)
			frameData.addSoundData(animation, targetTime, deltaTime);
	}

	private static final FrameData tempData = new FrameData();

	/**
	 * Frame data related to sprites.
	 * 
	 * Sprite data are displayed by a call to
	 * {@link SpriterAnimator#draw(Batch batch, ShapeRenderer renderer)}.
	 */
	public final Array<SpriterObject> spriteData = new Array<SpriterObject>();

	/**
	 * Frame data related to points.
	 * 
	 * Point data are only displayed if a call to
	 * {@link SpriterAnimator#drawDebug(ShapeRenderer renderer)} is issued. They
	 * may however be used for other purposes like collision detection.
	 */
	public final ObjectMap<String, SpriterObject> pointData = new ObjectMap<String, SpriterObject>();

	/**
	 * Frame data related to boxes.
	 * 
	 * Box data are only displayed if a call to
	 * {@link SpriterAnimator#drawDebug(ShapeRenderer renderer)} is issued. They
	 * may however be used for other purposes like collision detection.
	 */
	public final IntMap<SpriterObject> boxData = new IntMap<SpriterObject>();

	/**
	 * Animation-related variables, indexed by variable name.
	 * 
	 * Like objectVars, these variables are not used by {@link SpriterAnimator}.
	 */
	public final ObjectMap<String, SpriterVarValue> animationVars = new ObjectMap<String, SpriterVarValue>();

	/**
	 * Object-related variables, indexed by object name and variable name.
	 * 
	 * Like animationVars, these variables are not used by
	 * {@link SpriterAnimator}.
	 */
	public final ObjectMap<String, ObjectMap<String, SpriterVarValue>> objectVars = new ObjectMap<String, ObjectMap<String, SpriterVarValue>>();

	/**
	 * Animation-related tags.
	 * 
	 * Like objectTags, these tags are not used by {@link SpriterAnimator}.
	 */
	public final Array<String> animationTags = new Array<String>();

	/**
	 * Object-related tags, indexed by object name.
	 * 
	 * Like animationTags, these tags are not used by {@link SpriterAnimator}.
	 */
	public final ObjectMap<String, Array<String>> objectTags = new ObjectMap<String, Array<String>>();

	/**
	 * Events are triggered by {@link SpriterAnimator} and can be caught with a
	 * {@link SpriterAnimationListener}.
	 */
	public final Array<String> events = new Array<String>();

	/**
	 * Sounds are automatically played by
	 * {@link SpriterAnimator#draw(Batch batch, ShapeRenderer renderer)}.
	 */
	public final Array<SpriterSound> sounds = new Array<SpriterSound>();

	private void clear() {
		spriteData.clear();
		pointData.clear();
		boxData.clear();
		animationVars.clear();
		objectVars.clear();
		animationTags.clear();
		objectTags.clear();
		events.clear();
		sounds.clear();
	}

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
		if (animation.soundlines.size == 0)
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

	private void addSpatialData(FrameDataUpdateConfiguration configuration, SpriterObject info, SpriterTimeline timeline, SpriterData spriter, float deltaTime) {
		switch (timeline.objectType) {
		case Sprite:
			this.spriteData.add(info);
			break;
		case Entity:
			SpriterAnimation newAnim = spriter.entities.get(info.entityId).animations.get(info.animationId);
			float newTargetTime = info.t * newAnim.length;
			FrameData.update(tempData, configuration, newAnim, newTargetTime, deltaTime, info);
			this.spriteData.addAll(tempData.spriteData);
			break;
		case Point:
			this.pointData.put(timeline.name, info);
			break;
		case Box:
			this.boxData.put(timeline.objectId, info);
			break;
		default:
			break;
		}
	}

	private static SpriterSpatial[] getBoneInfos(SpriterMainlineKey key, SpriterAnimation animation, float targetTime) {
		return getBoneInfos(key, animation, targetTime, null);
	}

	private static SpriterSpatial[] getBoneInfos(SpriterMainlineKey key, SpriterAnimation animation, float targetTime, SpriterSpatial parentInfo) {
		if (key.boneRefs.size == 0)
			return null;

		SpriterSpatial[] ret = new SpriterSpatial[key.boneRefs.size];

		for (int i = 0; i < key.boneRefs.size; ++i) {
			SpriterRef boneRef = key.boneRefs.get(i);
			SpriterSpatial interpolated = getBoneInfo(boneRef, animation, targetTime);

			if (boneRef.parentId >= 0)
				applyParentTransform(interpolated, ret[boneRef.parentId]);
			else if (parentInfo != null)
				applyParentTransform(interpolated, parentInfo);
			ret[i] = interpolated;
		}

		return ret;
	}

	private static SpriterMainlineKey[] getMainlineKeys(Array<SpriterMainlineKey> keys, float targetTime) {
		SpriterMainlineKey keyA = lastKeyForTime(keys, targetTime);

		int nextKey = keyA.id + 1;
		if (nextKey >= keys.size)
			nextKey = 0;
		SpriterMainlineKey keyB = keys.get(nextKey);

		return new SpriterMainlineKey[] { keyA, keyB };
	}

	private static SpriterSpatial getBoneInfo(SpriterRef spriterRef, SpriterAnimation animation, float targetTime) {
		SpriterTimeline timeline = animation.timelines.get(spriterRef.timelineId);
		Array<SpriterTimelineKey> keys = timeline.keys;
		SpriterTimelineKey keyA = keys.get(spriterRef.keyId);
		SpriterTimelineKey keyB = getNextXLineKey(keys, keyA, animation.looping);

		if (keyB == null)
			return new SpriterSpatial(keyA.boneInfo);

		float factor = getFactor(keyA, keyB, animation.length, targetTime);
		return interpolate(keyA.boneInfo, keyB.boneInfo, factor, keyA.spin);
	}

	private static SpriterObject getObjectInfo(SpriterRef spriterRef, SpriterAnimation animation, float targetTime) {
		Array<SpriterTimelineKey> keys = animation.timelines.get(spriterRef.timelineId).keys;
		SpriterTimelineKey keyA = keys.get(spriterRef.keyId);
		SpriterTimelineKey keyB = getNextXLineKey(keys, keyA, animation.looping);

		if (keyB == null)
			return new SpriterObject(keyA.objectInfo);

		float factor = getFactor(keyA, keyB, animation.length, targetTime);
		return interpolate(keyA.objectInfo, keyB.objectInfo, factor, keyA.spin);
	}

	private static SpriterSpatial interpolate(SpriterSpatial a, SpriterSpatial b, float f, int spin) {
		SpriterSpatial spatial = new SpriterSpatial();

		spatial.angle = MathHelper.angleLinear(a.angle, b.angle, spin, f);
		spatial.x = MathHelper.linear(a.x, b.x, f);
		spatial.y = MathHelper.linear(a.y, b.y, f);
		spatial.scaleX = MathHelper.linear(a.scaleX, b.scaleX, f);
		spatial.scaleY = MathHelper.linear(a.scaleY, b.scaleY, f);

		return spatial;
	}

	private static SpriterObject interpolate(SpriterObject a, SpriterObject b, float f, int spin) {
		SpriterObject object = new SpriterObject();

		object.angle = MathHelper.angleLinear(a.angle, b.angle, spin, f);
		object.alpha = MathHelper.linear(a.alpha, b.alpha, f);
		object.x = MathHelper.linear(a.x, b.x, f);
		object.y = MathHelper.linear(a.y, b.y, f);
		object.scaleX = MathHelper.linear(a.scaleX, b.scaleX, f);
		object.scaleY = MathHelper.linear(a.scaleY, b.scaleY, f);
		object.pivotX = a.pivotX;
		object.pivotY = a.pivotY;
		object.file = new SpriterFileInfo(a.file);
		object.entityId = a.entityId;
		object.animationId = a.animationId;
		object.t = MathHelper.linear(a.t, b.t, f);

		return object;
	}

	private static float adjustTime(SpriterKey keyA, SpriterKey keyB, float animationLength, float targetTime) {
		float nextTime = keyB.time > keyA.time ? keyB.time : animationLength;
		float factor = getFactor(keyA, keyB, animationLength, targetTime);
		return MathHelper.linear(keyA.time, nextTime, factor);
	}

	private static float getFactor(SpriterKey keyA, SpriterKey keyB, float animationLength, float targetTime) {
		float timeA = keyA.time;
		float timeB = keyB.time;

		if (timeA > timeB) {
			timeB += animationLength;
			if (targetTime < timeA)
				targetTime += animationLength;
		}

		float factor = MathHelper.reverseLinear(timeA, timeB, targetTime);
		factor = keyA.curveType.applySpeedCurve(keyA, factor);
		return factor;
	}

	private static <T extends SpriterKey> T lastKeyForTime(Array<T> keys, float targetTime) {
		T current = keys.peek();
		for (T key : keys) {
			if (key.time > targetTime)
				break;
			current = key;
		}

		return current;
	}

	private static <T extends SpriterKey> T getNextXLineKey(Array<T> keys, T firstKey, boolean looping) {
		if (keys.size < 2)
			return null;

		int keyBId = firstKey.id + 1;
		if (keyBId >= keys.size) {
			if (!looping)
				return null;
			keyBId = 0;
		}

		return keys.get(keyBId);
	}

	static void applyParentTransform(SpriterSpatial child, SpriterSpatial parent) {
		float px = parent.scaleX * child.x;
		float py = parent.scaleY * child.y;

		float s = MathUtils.sinDeg(parent.angle);
		float c = MathUtils.cosDeg(parent.angle);

		child.x = px * c - py * s + parent.x;
		child.y = px * s + py * c + parent.y;
		child.scaleX *= parent.scaleX;
		child.scaleY *= parent.scaleY;
		child.angle = (parent.angle + Math.signum(parent.scaleX * parent.scaleY) * child.angle) % 360.0f;
		child.alpha *= parent.alpha;
	}

	@Override
	public String toString() {
		return "FrameData [spriteData=" + this.spriteData + ", pointData=" + this.pointData + ", boxData=" + this.boxData + ", animationVars=" + this.animationVars + ", objectVars=" + this.objectVars + ", animationTags=" + this.animationTags + ", objectTags=" + this.objectTags + ", events=" + this.events + ", sounds=" + this.sounds + "]";
	}

}
