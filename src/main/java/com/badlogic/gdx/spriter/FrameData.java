// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterFileInfo;
import com.badlogic.gdx.spriter.data.SpriterKey;
import com.badlogic.gdx.spriter.data.SpriterMainlineKey;
import com.badlogic.gdx.spriter.data.SpriterObject;
import com.badlogic.gdx.spriter.data.SpriterObjectRef;
import com.badlogic.gdx.spriter.data.SpriterRef;
import com.badlogic.gdx.spriter.data.SpriterSpatial;
import com.badlogic.gdx.spriter.data.SpriterTimeline;
import com.badlogic.gdx.spriter.data.SpriterTimelineKey;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class FrameData {

	public static FrameData create(SpriterAnimation first, SpriterAnimation second, float targetTime, float factor) {

		if (first == second)
			return create(first, targetTime);

		float targetTimeSecond = targetTime / first.length * second.length;

		SpriterMainlineKey[] keys = getMainlineKeys(first.mainline.keys, targetTime);
		SpriterMainlineKey firstKeyA = keys[0];
		SpriterMainlineKey firstKeyB = keys[1];

		keys = getMainlineKeys(second.mainline.keys, targetTimeSecond);
		SpriterMainlineKey secondKeyA = keys[0];
		SpriterMainlineKey secondKeyB = keys[1];

		if (firstKeyA.boneRefs.size != secondKeyA.boneRefs.size || firstKeyB.boneRefs.size != secondKeyB.boneRefs.size || firstKeyA.objectRefs.size != secondKeyA.objectRefs.size || firstKeyB.objectRefs.size != secondKeyB.objectRefs.size)
			return create(first, targetTime);

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
		SpriterAnimation currentAnimation = factor < 0.5f ? first : second;

		FrameData frameData = new FrameData();

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

			frameData.addSpatialData(info, currentAnimation.timelines.get(objectRefFirst.timelineId), currentAnimation.entity.data, targetTime);
		}

		return frameData;
	}

	public static FrameData create(SpriterAnimation animation, float targetTime) {
		return create(animation, targetTime, null);
	}

	public static FrameData create(SpriterAnimation animation, float targetTime, SpriterSpatial parentInfo) {
		Array<SpriterMainlineKey> keys = animation.mainline.keys;
		SpriterMainlineKey[] someKeys = getMainlineKeys(keys, targetTime);
		SpriterMainlineKey keyA = someKeys[0];
		SpriterMainlineKey keyB = someKeys[1];

		float adjustedTime = adjustTime(keyA, keyB, animation.length, targetTime);

		SpriterSpatial[] boneInfos = getBoneInfos(keyA, animation, targetTime, parentInfo);

		FrameData frameData = new FrameData();

		for (SpriterObjectRef objectRef : keyA.objectRefs) {
			SpriterObject interpolated = getObjectInfo(objectRef, animation, adjustedTime);

			if (boneInfos != null && objectRef.parentId >= 0)
				applyParentTransform(interpolated, boneInfos[objectRef.parentId]);

			frameData.addSpatialData(interpolated, animation.timelines.get(objectRef.timelineId), animation.entity.data, targetTime);
		}

		return frameData;
	}

	public final Array<SpriterObject> spriteData = new Array<SpriterObject>();
	public final Array<SpriterObject> pointData = new Array<SpriterObject>();
	public final IntMap<SpriterObject> boxData = new IntMap<SpriterObject>();

	private void addSpatialData(SpriterObject info, SpriterTimeline timeline, SpriterData spriter, float targetTime) {
		switch (timeline.objectType) {
		case Sprite:
			this.spriteData.add(info);
			break;
		case Entity:
			SpriterAnimation newAnim = spriter.entities.get(info.entityId).animations.get(info.animationId);
			float newTargetTime = info.t * newAnim.length;
			this.spriteData.addAll(FrameData.create(newAnim, newTargetTime, info).spriteData);
			break;
		case Point:
			this.pointData.add(info);
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

	static void applyParentTransform(SpriterSpatial child, SpriterSpatial parent) {
		float px = parent.scaleX * child.x;
		float py = parent.scaleY * child.y;

		float s = MathUtils.sinDeg(parent.angle);
		float c = MathUtils.cosDeg(parent.angle);

		child.x = px * c - py * s + parent.x;
		child.y = px * s + py * c + parent.y;
		child.scaleX *= parent.scaleX;
		child.scaleY *= parent.scaleY;
		child.angle = parent.angle + Math.signum(parent.scaleX * parent.scaleY) * child.angle;
		child.angle %= 360.0f;
		child.alpha *= parent.alpha;
	}

	public static float adjustTime(SpriterKey keyA, SpriterKey keyB, float animationLength, float targetTime) {
		float nextTime = keyB.time > keyA.time ? keyB.time : animationLength;
		float factor = getFactor(keyA, keyB, animationLength, targetTime);
		return MathHelper.linear(keyA.time, nextTime, factor);
	}

	public static float getFactor(SpriterKey keyA, SpriterKey keyB, float animationLength, float targetTime) {
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

	public static <T extends SpriterKey> T lastKeyForTime(Array<T> keys, float targetTime) {
		T current = keys.peek();
		for (T key : keys) {
			if (key.time > targetTime)
				break;
			current = key;
		}

		return current;
	}

	public static <T extends SpriterKey> T getNextXLineKey(Array<T> keys, T firstKey, boolean looping) {
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

	@Override
	public String toString() {
		return "FrameData [spriteData=" + spriteData + ", pointData=" + pointData + ", boxData=" + boxData + "]";
	}

}
