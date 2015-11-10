// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterElement;
import com.badlogic.gdx.spriter.data.SpriterEntity;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.spriter.data.SpriterMainline;
import com.badlogic.gdx.spriter.data.SpriterMainlineKey;
import com.badlogic.gdx.spriter.data.SpriterMeta;
import com.badlogic.gdx.spriter.data.SpriterObject;
import com.badlogic.gdx.spriter.data.SpriterObjectRef;
import com.badlogic.gdx.spriter.data.SpriterTimeline;
import com.badlogic.gdx.spriter.data.SpriterTimelineKey;
import com.badlogic.gdx.spriter.data.SpriterVarDef;
import com.badlogic.gdx.spriter.data.SpriterVarType;
import com.badlogic.gdx.spriter.data.SpriterVarline;
import com.badlogic.gdx.spriter.data.SpriterVarlineKey;

public class SpriterTestData {

	static final SpriterData data1 = BuildData1();

	// From
	// http://brashmonkey.com/forum/index.php?/topic/4087-small-example-spriter-scml-files-for-the-metadata-features/
	static final String letterbotSCML = "/Variable/LetterBot.scml";
	static final String letterbotSCON = "/Variable/LetterBot.scon";
	static final SpriterData letterbotSCMLData = BuildLetterBotSCML();
	static final SpriterData letterbotSCONData = BuildLetterBotSCON();

	static final String boxtagSCML = "/BoxTagVariable/player.scml";
	static final String boxtagSCON = "/BoxTagVariable/player.scon";

	static final String pointstriggersSCML = "/PointsTriggers/gunner_player_smaller_head.scml";
	static final String pointstriggersSCON = "/PointsTriggers/gunner_player_smaller_head.scon";

	// Default Spriter example
	static final String greyguySCML = "/GreyGuy/player.scml";
	static final String greyguySCON = "/GreyGuy/player.scon";

	static final String[] scml = new String[] { letterbotSCML, boxtagSCML,
			pointstriggersSCML, greyguySCML };

	static final String[] scon = new String[] { letterbotSCON, boxtagSCON,
			pointstriggersSCON, greyguySCON };

	private static SpriterData BuildData1() {
		SpriterData data = new SpriterData();
		SpriterFile file = new SpriterFile();
		file.id = 0;
		file.pivotX = 0.25f;
		file.pivotY = 0.75f;
		SpriterFolder folder = new SpriterFolder();
		folder.files.add(file);
		folder.id = 0;
		data.folders.add(folder);
		SpriterEntity entity = new SpriterEntity();
		SpriterAnimation animation = new SpriterAnimation();
		SpriterTimeline timeline = new SpriterTimeline();
		SpriterTimelineKey timelineKey = new SpriterTimelineKey();
		SpriterObject info = new SpriterObject();
		info.folderId = 0;
		info.fileId = 0;
		info.pivotX = Float.NaN;
		info.pivotY = Float.NaN;
		timelineKey.objectInfo = info;
		timeline.keys.add(timelineKey);
		animation.timelines.add(timeline);
		entity.animations.add(animation);
		data.entities.add(entity);
		return data;
	}

	private static SpriterData BuildLetterBotSCML() {
		SpriterData data = new SpriterData();
		data.scmlVersion = "1.0";
		data.generator = "BrashMonkey Spriter";
		data.generatorVersion = "r3";

		SpriterFolder folder = new SpriterFolder();
		folder.id = 0;

		SpriterFile file = new SpriterFile();
		file.id = 0;
		file.name = "Robot.png";
		file.width = 95;
		file.height = 106;
		file.pivotX = 0.463158f;
		file.pivotY = 0.132075f;
		folder.files.add(file);

		file = new SpriterFile();
		file.id = 1;
		file.name = "A.png";
		file.width = 37;
		file.height = 23;
		file.pivotX = 0.486486f;
		file.pivotY = 0.521739f;
		folder.files.add(file);

		file = new SpriterFile();
		file.id = 2;
		file.name = "B.png";
		file.width = 37;
		file.height = 23;
		file.pivotX = 0.486486f;
		file.pivotY = 0.521739f;
		folder.files.add(file);

		data.folders.add(folder);

		SpriterEntity entity = new SpriterEntity();
		entity.id = 0;
		entity.name = "Letter Bot";
		entity.data = data;

		SpriterVarDef varDef = new SpriterVarDef();
		varDef.id = 0;
		varDef.name = "current letter";
		varDef.type = SpriterVarType.String;
		varDef.defaultValue = "A";

		entity.variables.add(varDef);

		SpriterAnimation animation = new SpriterAnimation();
		animation.id = 0;
		animation.name = "Letter Cycle";
		animation.length = 2000f;
		animation.entity = entity;

		SpriterMainline mainline = new SpriterMainline();

		SpriterMainlineKey mainlineKey = new SpriterMainlineKey();
		mainlineKey.id = 0;

		SpriterObjectRef objectRef = new SpriterObjectRef();
		objectRef.id = 0;
		objectRef.timelineId = 0;
		objectRef.keyId = 0;
		objectRef.zIndex = 0;

		mainlineKey.objectRefs.add(objectRef);

		objectRef = new SpriterObjectRef();
		objectRef.id = 1;
		objectRef.timelineId = 1;
		objectRef.keyId = 0;
		objectRef.zIndex = 1;

		mainlineKey.objectRefs.add(objectRef);

		mainline.keys.add(mainlineKey);

		mainlineKey = new SpriterMainlineKey();
		mainlineKey.id = 1;
		mainlineKey.time = 773f;

		objectRef = new SpriterObjectRef();
		objectRef.id = 0;
		objectRef.timelineId = 0;
		objectRef.keyId = 0;
		objectRef.zIndex = 0;

		mainlineKey.objectRefs.add(objectRef);

		objectRef = new SpriterObjectRef();
		objectRef.id = 1;
		objectRef.timelineId = 1;
		objectRef.keyId = 1;
		objectRef.zIndex = 1;

		mainlineKey.objectRefs.add(objectRef);

		mainline.keys.add(mainlineKey);

		mainlineKey = new SpriterMainlineKey();
		mainlineKey.id = 2;
		mainlineKey.time = 871f;

		objectRef = new SpriterObjectRef();
		objectRef.id = 0;
		objectRef.timelineId = 0;
		objectRef.keyId = 0;
		objectRef.zIndex = 0;

		mainlineKey.objectRefs.add(objectRef);

		objectRef = new SpriterObjectRef();
		objectRef.id = 1;
		objectRef.timelineId = 1;
		objectRef.keyId = 2;
		objectRef.zIndex = 1;

		mainlineKey.objectRefs.add(objectRef);

		mainline.keys.add(mainlineKey);

		mainlineKey = new SpriterMainlineKey();
		mainlineKey.id = 3;
		mainlineKey.time = 975f;

		objectRef = new SpriterObjectRef();
		objectRef.id = 0;
		objectRef.timelineId = 0;
		objectRef.keyId = 0;
		objectRef.zIndex = 0;

		mainlineKey.objectRefs.add(objectRef);

		objectRef = new SpriterObjectRef();
		objectRef.id = 1;
		objectRef.timelineId = 1;
		objectRef.keyId = 3;
		objectRef.zIndex = 1;

		mainlineKey.objectRefs.add(objectRef);

		mainline.keys.add(mainlineKey);

		mainlineKey = new SpriterMainlineKey();
		mainlineKey.id = 4;
		mainlineKey.time = 1820f;

		objectRef = new SpriterObjectRef();
		objectRef.id = 0;
		objectRef.timelineId = 0;
		objectRef.keyId = 0;
		objectRef.zIndex = 0;

		mainlineKey.objectRefs.add(objectRef);

		objectRef = new SpriterObjectRef();
		objectRef.id = 1;
		objectRef.timelineId = 1;
		objectRef.keyId = 4;
		objectRef.zIndex = 1;

		mainlineKey.objectRefs.add(objectRef);

		mainline.keys.add(mainlineKey);

		mainlineKey = new SpriterMainlineKey();
		mainlineKey.id = 5;
		mainlineKey.time = 1900f;

		objectRef = new SpriterObjectRef();
		objectRef.id = 0;
		objectRef.timelineId = 0;
		objectRef.keyId = 0;
		objectRef.zIndex = 0;

		mainlineKey.objectRefs.add(objectRef);

		objectRef = new SpriterObjectRef();
		objectRef.id = 1;
		objectRef.timelineId = 1;
		objectRef.keyId = 5;
		objectRef.zIndex = 1;

		mainlineKey.objectRefs.add(objectRef);

		mainline.keys.add(mainlineKey);

		mainlineKey = new SpriterMainlineKey();
		mainlineKey.id = 6;
		mainlineKey.time = 1986f;

		objectRef = new SpriterObjectRef();
		objectRef.id = 0;
		objectRef.timelineId = 0;
		objectRef.keyId = 0;
		objectRef.zIndex = 0;

		mainlineKey.objectRefs.add(objectRef);

		objectRef = new SpriterObjectRef();
		objectRef.id = 1;
		objectRef.timelineId = 1;
		objectRef.keyId = 6;
		objectRef.zIndex = 1;

		mainlineKey.objectRefs.add(objectRef);

		mainline.keys.add(mainlineKey);

		animation.mainline = mainline;

		SpriterTimeline timeline = new SpriterTimeline();
		timeline.id = 0;
		timeline.name = "Robot";

		SpriterTimelineKey timelineKey = new SpriterTimelineKey();
		timelineKey.id = 0;
		timelineKey.spin = 0;

		SpriterObject object = new SpriterObject();
		object.folderId = 0;
		object.fileId = 0;
		object.x = -5f;
		object.y = 8f;
		object.angle = 0f;
		object.pivotX = 0.463158f;
		object.pivotY = 0.132075f;

		timelineKey.objectInfo = object;

		timeline.keys.add(timelineKey);

		animation.timelines.add(timeline);

		timeline = new SpriterTimeline();
		timeline.id = 1;
		timeline.name = "A";

		timelineKey = new SpriterTimelineKey();
		timelineKey.id = 0;
		timelineKey.spin = 0;

		object = new SpriterObject();
		object.folderId = 0;
		object.fileId = 1;
		object.x = -1f;
		object.y = 45f;
		object.angle = 0f;
		object.pivotX = 0.486486f;
		object.pivotY = 0.521739f;

		timelineKey.objectInfo = object;

		timeline.keys.add(timelineKey);

		timelineKey = new SpriterTimelineKey();
		timelineKey.id = 1;
		timelineKey.time = 773;
		timelineKey.spin = 0;

		object = new SpriterObject();
		object.folderId = 0;
		object.fileId = 1;
		object.x = -1f;
		object.y = 45f;
		object.angle = 0f;
		object.pivotX = 0.486486f;
		object.pivotY = 0.521739f;

		timelineKey.objectInfo = object;

		timeline.keys.add(timelineKey);

		timelineKey = new SpriterTimelineKey();
		timelineKey.id = 2;
		timelineKey.time = 871;
		timelineKey.spin = 0;

		object = new SpriterObject();
		object.folderId = 0;
		object.fileId = 2;
		object.x = -1f;
		object.y = 45f;
		object.angle = 0f;
		object.scaleX = 0.205128f;
		object.pivotX = 0.486486f;
		object.pivotY = 0.521739f;

		timelineKey.objectInfo = object;

		timeline.keys.add(timelineKey);

		timelineKey = new SpriterTimelineKey();
		timelineKey.id = 3;
		timelineKey.time = 975;
		timelineKey.spin = 0;

		object = new SpriterObject();
		object.folderId = 0;
		object.fileId = 2;
		object.x = -1f;
		object.y = 45f;
		object.angle = 0f;
		object.pivotX = 0.486486f;
		object.pivotY = 0.521739f;

		timelineKey.objectInfo = object;

		timeline.keys.add(timelineKey);

		timelineKey = new SpriterTimelineKey();
		timelineKey.id = 4;
		timelineKey.time = 1820;
		timelineKey.spin = 0;

		object = new SpriterObject();
		object.folderId = 0;
		object.fileId = 2;
		object.x = -1f;
		object.y = 45f;
		object.angle = 0f;
		object.pivotX = 0.486486f;
		object.pivotY = 0.521739f;

		timelineKey.objectInfo = object;

		timeline.keys.add(timelineKey);

		timelineKey = new SpriterTimelineKey();
		timelineKey.id = 5;
		timelineKey.time = 1900;
		timelineKey.spin = 0;

		object = new SpriterObject();
		object.folderId = 0;
		object.fileId = 1;
		object.x = -1f;
		object.y = 45f;
		object.angle = 0f;
		object.scaleX = 0.102564f;
		object.pivotX = 0.486486f;
		object.pivotY = 0.521739f;

		timelineKey.objectInfo = object;

		timeline.keys.add(timelineKey);

		timelineKey = new SpriterTimelineKey();
		timelineKey.id = 6;
		timelineKey.time = 1986;
		timelineKey.spin = 0;

		object = new SpriterObject();
		object.folderId = 0;
		object.fileId = 1;
		object.x = -1f;
		object.y = 45f;
		object.angle = 0f;
		object.pivotX = 0.486486f;
		object.pivotY = 0.521739f;

		timelineKey.objectInfo = object;

		timeline.keys.add(timelineKey);

		animation.timelines.add(timeline);

		SpriterMeta meta = new SpriterMeta();

		SpriterVarline varline = new SpriterVarline();
		varline.id = 0;
		varline.def = 0;

		SpriterVarlineKey varlineKey = new SpriterVarlineKey();
		varlineKey.id = 0;
		varlineKey.time = 2;
		varlineKey.value = "";

		varline.keys.add(varlineKey);

		varlineKey = new SpriterVarlineKey();
		varlineKey.id = 1;
		varlineKey.time = 871;
		varlineKey.value = "B";

		varline.keys.add(varlineKey);

		varlineKey = new SpriterVarlineKey();
		varlineKey.id = 2;
		varlineKey.time = 1901;
		varlineKey.value = "A";

		varline.keys.add(varlineKey);

		meta.varlines.add(varline);

		animation.meta = meta;

		entity.animations.add(animation);

		data.entities.add(entity);
		
		for (SpriterEntity e : data.entities)
			for (SpriterAnimation a : e.animations)
				for (SpriterTimeline t : a.timelines)
					for (SpriterTimelineKey k : t.keys)
						k.toString();	// Do nothing but "warm up" iterators

		return data;
	}

	private static SpriterData BuildLetterBotSCON() {
		SpriterData data = BuildLetterBotSCML();
		data.generatorVersion = "r4.1";
		
		data.entities.first().animations.first().meta.varlines.clear();
		
		SpriterElement element = new SpriterElement();
		element.id = 0;
		element.name = "StuckInMove";
		
		data.tags.add(element);
		
		return data;
	}

}
