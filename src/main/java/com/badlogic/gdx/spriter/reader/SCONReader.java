//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.reader;

import java.io.IOException;
import java.io.Reader;

import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterCharacterMap;
import com.badlogic.gdx.spriter.data.SpriterCurveType;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterElement;
import com.badlogic.gdx.spriter.data.SpriterEntity;
import com.badlogic.gdx.spriter.data.SpriterEventline;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFileType;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.spriter.data.SpriterKey;
import com.badlogic.gdx.spriter.data.SpriterMainline;
import com.badlogic.gdx.spriter.data.SpriterMainlineKey;
import com.badlogic.gdx.spriter.data.SpriterMapInstruction;
import com.badlogic.gdx.spriter.data.SpriterMeta;
import com.badlogic.gdx.spriter.data.SpriterObject;
import com.badlogic.gdx.spriter.data.SpriterObjectInfo;
import com.badlogic.gdx.spriter.data.SpriterObjectRef;
import com.badlogic.gdx.spriter.data.SpriterObjectType;
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
import com.badlogic.gdx.spriter.data.SpriterVarType;
import com.badlogic.gdx.spriter.data.SpriterVariableContainer;
import com.badlogic.gdx.spriter.data.SpriterVarline;
import com.badlogic.gdx.spriter.data.SpriterVarlineKey;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class SCONReader extends SpriterReader {

	public SCONReader() {
		super();
	}

	@Override
	public SpriterData load(Reader reader) throws IOException {
		SpriterData data = new SpriterData();
		JsonValue root = new JsonReader().parse(reader);

		// First read
		loadData(data, root);

		// Then clean up...
		cleanData(data);

		return data;
	}

	private void loadData(SpriterData data, JsonValue root) {
		data.scmlVersion = root.getString("scon_version", data.scmlVersion);
		data.generator = root.getString("generator", data.generator);
		data.generatorVersion = root.getString("generator_version", data.generatorVersion);
		loadFolders(data, root.get("folder"));
		loadEntities(data, root.get("entity"));

		JsonValue tagList = root.get("tag_list");
		if (tagList != null)
			loadElements(data, tagList);
	}

	private void loadElements(SpriterData data, JsonValue elements) {
		for (JsonValue e : elements) {
			SpriterElement element = new SpriterElement();

			loadElement(element, e);

			data.tags.add(element);
		}
	}

	private void loadFolders(SpriterData data, JsonValue folders) {
		for (JsonValue repo : folders) {
			SpriterFolder folder = new SpriterFolder();

			loadFolder(folder, repo);

			data.folders.add(folder);
		}
	}

	private void loadFolder(SpriterFolder folder, JsonValue repo) {
		loadElement(folder, repo);
		loadFiles(folder, repo.get("file"));
	}

	private void loadElement(SpriterElement element, JsonValue value) {
		element.id = value.getInt("id", element.id);
		element.name = value.getString("name", element.name);
	}

	private void loadFiles(SpriterFolder folder, JsonValue files) {
		for (JsonValue f : files) {
			SpriterFile file = new SpriterFile();

			loadFile(file, f);

			folder.files.add(file);
		}
	}

	private void loadFile(SpriterFile file, JsonValue f) {
		loadElement(file, f);
		file.type = SpriterFileType.parse(f.getString("type", file.type.toString().toLowerCase()));
		file.width = f.getInt("width", file.width);
		file.height = f.getInt("height", file.height);
		file.pivotX = f.getFloat("pivot_x", file.pivotX);
		file.pivotY = f.getFloat("pivot_y", file.pivotY);
	}

	private void loadEntities(SpriterData data, JsonValue entities) {
		for (JsonValue e : entities) {
			SpriterEntity entity = new SpriterEntity();

			loadEntity(entity, e);

			data.entities.add(entity);
		}
	}

	private void loadEntity(SpriterEntity entity, JsonValue e) {
		loadElement(entity, e);
		loadObjectInfos(entity, e.get("obj_info"));
		loadCharacterMaps(entity, e.get("character_map"));
		loadAnimations(entity, e.get("animation"));

		JsonValue varDefs = e.get("var_defs");
		if (varDefs != null)
			loadVariables(entity, varDefs);
	}

	private void loadVariables(SpriterVariableContainer container, JsonValue elements) {
		for (JsonValue i : elements) {
			SpriterVarDef var = new SpriterVarDef();

			loadVarDef(var, i);

			container.variables.add(var);
		}
	}

	private void loadVarDef(SpriterVarDef var, JsonValue i) {
		loadElement(var, i);
		var.type = SpriterVarType.parse(i.getString("type", var.type.toString().toLowerCase()));
		var.defaultValue = i.getString("default", var.defaultValue);
	}

	private void loadObjectInfos(SpriterEntity entity, JsonValue infos) {
		for (JsonValue info : infos) {
			SpriterObjectInfo objInfo = new SpriterObjectInfo();

			loadObjectInfo(objInfo, info);

			entity.objectInfos.add(objInfo);
		}
	}

	private void loadObjectInfo(SpriterObjectInfo objInfo, JsonValue info) {
		loadElement(objInfo, info);

		objInfo.objectType = SpriterObjectType.parse(info.getString("type", objInfo.objectType.toString().toLowerCase()));
		objInfo.width = info.getFloat("w", objInfo.width);
		objInfo.height = info.getFloat("h", objInfo.height);
		objInfo.pivotX = info.getFloat("pivot_x", objInfo.pivotX);
		objInfo.pivotY = info.getFloat("pivot_y", objInfo.pivotY);

		JsonValue varDefs = info.get("var_defs");
		if (varDefs != null)
			loadVariables(objInfo, varDefs);
	}

	private void loadCharacterMaps(SpriterEntity entity, JsonValue charMaps) {
		for (JsonValue map : charMaps) {
			SpriterCharacterMap charMap = new SpriterCharacterMap();

			loadCharacterMap(charMap, map);

			entity.characterMaps.add(charMap);
		}
	}

	private void loadCharacterMap(SpriterCharacterMap charMap, JsonValue map) {
		loadElement(charMap, map);
		loadCharacterMapInstructions(charMap, map.get("map"));
	}

	private void loadCharacterMapInstructions(SpriterCharacterMap charMap, JsonValue maps) {
		for (JsonValue mapping : maps) {
			SpriterMapInstruction instruction = new SpriterMapInstruction();

			loadMapInstruction(instruction, mapping);

			charMap.maps.add(instruction);
		}
	}

	private void loadMapInstruction(SpriterMapInstruction instruction, JsonValue mapping) {
		instruction.folderId = mapping.getInt("folder", instruction.folderId);
		instruction.fileId = mapping.getInt("file", instruction.fileId);
		instruction.targetFolderId = mapping.getInt("target_folder", instruction.targetFolderId);
		instruction.targetFileId = mapping.getInt("target_file", instruction.targetFileId);
	}

	private void loadAnimations(SpriterEntity entity, JsonValue animations) {
		for (JsonValue a : animations) {
			SpriterAnimation animation = new SpriterAnimation();

			loadAnimation(animation, a);

			entity.animations.add(animation);
		}
	}

	private void loadAnimation(SpriterAnimation animation, JsonValue a) {
		loadElement(animation, a);
		animation.length = a.getFloat("length", animation.length);
		animation.looping = a.getBoolean("looping", animation.looping);

		SpriterMainline main = new SpriterMainline();
		loadMainline(main, a.get("mainline"));
		animation.mainline = main;

		loadTimelines(animation, animation.entity, a.get("timeline"));
		
		JsonValue eventLines = a.get("eventline");
		if(eventLines != null)
			loadEventlines(animation, eventLines);
		
		JsonValue soundLines = a.get("soundline");
		if(soundLines != null)
			loadSoundlines(animation, soundLines);

		JsonValue xmlMeta = a.get("meta");
		if (xmlMeta != null) {
			SpriterMeta meta = new SpriterMeta();
			loadMeta(meta, xmlMeta);
			animation.meta = meta;
		}
	}

	private void loadMainline(SpriterMainline main, JsonValue mainline) {
		loadMainlineKeys(main, mainline.get("key"));
	}

	private void loadMainlineKeys(SpriterMainline main, JsonValue keys) {
		for (JsonValue k : keys) {
			SpriterMainlineKey key = new SpriterMainlineKey();

			loadMainlineKey(key, k);

			main.keys.add(key);
		}
	}

	private void loadMainlineKey(SpriterMainlineKey key, JsonValue k) {
		loadKey(key, k);
		loadBoneRefs(key, k.get("bone_ref"));
		loadObjectRefs(key, k.get("object_ref"));
	}

	private void loadKey(SpriterKey key, JsonValue k) {
		loadElement(key, k);
		key.time = k.getFloat("time", key.time);
		key.curveType = SpriterCurveType.parse(k.getString("curve_type", key.curveType.toString().toLowerCase()));
		key.c1 = k.getFloat("c1", key.c1);
		key.c2 = k.getFloat("c2", key.c2);
		key.c3 = k.getFloat("c3", key.c3);
		key.c4 = k.getFloat("c4", key.c4);
	}

	private void loadBoneRefs(SpriterMainlineKey key, JsonValue boneRefs) {
		for (JsonValue e : boneRefs) {
			SpriterRef boneRef = new SpriterRef();

			loadRef(boneRef, e);

			key.boneRefs.add(boneRef);
		}
	}

	private void loadRef(SpriterRef boneRef, JsonValue e) {
		loadElement(boneRef, e);
		boneRef.timelineId = e.getInt("timeline", boneRef.timelineId);
		boneRef.keyId = e.getInt("key", boneRef.keyId);
		boneRef.parentId = e.getInt("parent", boneRef.parentId);
	}

	private void loadObjectRefs(SpriterMainlineKey key, JsonValue objectRefs) {
		for (JsonValue o : objectRefs) {
			SpriterObjectRef objectRef = new SpriterObjectRef();

			loadObjectRef(objectRef, o);

			key.objectRefs.add(objectRef);
		}
		key.objectRefs.sort();
	}

	private void loadObjectRef(SpriterObjectRef objectRef, JsonValue o) {
		loadRef(objectRef, o);
		objectRef.zIndex = o.getInt("z_index", objectRef.zIndex);
	}

	private void loadTimelines(SpriterAnimation animation, SpriterEntity entity, JsonValue timelines) {
		for (JsonValue t : timelines) {
			SpriterTimeline timeline = new SpriterTimeline();

			loadTimeline(timeline, t);

			animation.timelines.add(timeline);
		}
	}

	private void loadTimeline(SpriterTimeline timeline, JsonValue t) {
		loadElement(timeline, t);
		timeline.objectType = SpriterObjectType.parse(t.getString("object_type", timeline.objectType.toString().toLowerCase()));
		timeline.objectId = t.getInt("obj", timeline.objectId);
		loadTimelineKeys(timeline, t.get("key"));

		JsonValue xmlMeta = t.get("meta");
		if (xmlMeta != null) {
			SpriterMeta meta = new SpriterMeta();
			loadMeta(meta, xmlMeta);
			timeline.meta = meta;
		}
	}

	private void loadTimelineKeys(SpriterTimeline timeline, JsonValue keys) {
		for (JsonValue k : keys) {
			SpriterTimelineKey key = new SpriterTimelineKey();

			loadTimelineKey(key, k);

			timeline.keys.add(key);
		}
	}

	private void loadTimelineKey(SpriterTimelineKey key, JsonValue k) {
		loadKey(key, k);

		key.spin = k.getInt("spin", key.spin);

		JsonValue obj = k.get("bone");
		if (obj != null) {
			SpriterSpatial boneInfo = new SpriterSpatial();

			loadSpatial(boneInfo, obj);

			key.boneInfo = boneInfo;
		}

		obj = k.get("object");
		if (obj != null) {
			SpriterObject objectInfo = new SpriterObject();

			loadObject(objectInfo, obj);

			key.objectInfo = objectInfo;
		}
	}

	private void loadSpatial(SpriterSpatial spatial, JsonValue obj) {
		spatial.x = obj.getFloat("x", spatial.x);
		spatial.y = obj.getFloat("y", spatial.y);
		spatial.scaleX = obj.getFloat("scale_x", spatial.scaleX);
		spatial.scaleY = obj.getFloat("scale_y", spatial.scaleY);
		spatial.angle = obj.getFloat("angle", spatial.angle);
		spatial.alpha = obj.getFloat("a", spatial.alpha);
	}

	private void loadObject(SpriterObject objectInfo, JsonValue element) {
		loadSpatial(objectInfo, element);
		objectInfo.animationId = element.getInt("animation", objectInfo.animationId);
		objectInfo.fileId = element.getInt("file", objectInfo.fileId);
		objectInfo.pivotX = element.getFloat("pivot_x", Float.NaN);
		objectInfo.pivotY = element.getFloat("pivot_y", Float.NaN);
		objectInfo.entityId = element.getInt("entity", objectInfo.entityId);
		objectInfo.folderId = element.getInt("folder", objectInfo.folderId);
		objectInfo.t = element.getFloat("t", objectInfo.t);
	}

	private void loadEventlines(SpriterAnimation animation, JsonValue elements) {
		for (JsonValue e : elements) {
			SpriterEventline eventline = new SpriterEventline();

			loadEventline(eventline, e);

			animation.eventlines.add(eventline);
		}
	}

	private void loadEventline(SpriterEventline eventline, JsonValue e) {
		loadElement(eventline, e);
		loadKeys(eventline, e.get("key"));
	}

	private void loadKeys(SpriterEventline eventline, JsonValue elements) {
		for (JsonValue e : elements) {
			SpriterKey key = new SpriterKey();

			loadKey(key, e);

			eventline.keys.add(key);
		}
	}

	private void loadSoundlines(SpriterAnimation animation, JsonValue elements) {
		for (JsonValue e : elements) {
			SpriterSoundline soundline = new SpriterSoundline();

			loadSoundline(soundline, e);

			animation.soundlines.add(soundline);
		}
	}

	private void loadSoundline(SpriterSoundline soundline, JsonValue e) {
		loadElement(soundline, e);
		loadSoundlineKeys(soundline, e.get("key"));
	}

	private void loadSoundlineKeys(SpriterSoundline soundline, JsonValue elements) {
		for (JsonValue e : elements) {
			SpriterSoundlineKey key = new SpriterSoundlineKey();

			loadSoundlineKey(key, e);

			soundline.keys.add(key);
		}
	}

	private void loadSoundlineKey(SpriterSoundlineKey key, JsonValue e) {
		loadKey(key, e);
		SpriterSound sound = new SpriterSound();
		loadSound(sound, e.get("object"));
		key.soundObject = sound;
	}

	private void loadSound(SpriterSound sound, JsonValue element) {
		loadElement(sound, element);
		sound.folderId = element.getInt("folder", sound.folderId);
		sound.fileId = element.getInt("file", sound.fileId);
		sound.trigger = element.getBoolean("trigger", sound.trigger);
		sound.panning = element.getFloat("panning", sound.panning);
		sound.volume = element.getFloat("volume", sound.volume);
	}

	private void loadMeta(SpriterMeta meta, JsonValue element) {
		JsonValue varlines = element.get("varline");
		if(varlines != null)
			loadVarlines(meta, varlines);

		JsonValue xmlTagline = element.get("tagline");
		if (xmlTagline != null) {
			SpriterTagline tagline = new SpriterTagline();
			loadTagline(tagline, xmlTagline);
		}
	}

	private void loadVarlines(SpriterMeta meta, JsonValue elements) {
		for (JsonValue e : elements) {
			SpriterVarline varline = new SpriterVarline();

			loadVarline(varline, e);

			meta.varlines.add(varline);
		}
	}

	private void loadVarline(SpriterVarline varline, JsonValue e) {
		loadElement(varline, e);
		varline.def = e.getInt("def", varline.def);
		loadVarlineKeys(varline, e.get("key"));
	}

	private void loadVarlineKeys(SpriterVarline varline, JsonValue elements) {
		for (JsonValue e : elements) {
			SpriterVarlineKey key = new SpriterVarlineKey();

			loadVarlineKey(key, e);

			varline.keys.add(key);
		}
	}

	private void loadVarlineKey(SpriterVarlineKey key, JsonValue e) {
		loadKey(key, e);
		key.value = e.getString("val", key.value);
	}

	private void loadTagline(SpriterTagline tagline, JsonValue e) {
		loadTaglineKeys(tagline, e.get("key"));
	}

	private void loadTaglineKeys(SpriterTagline tagline, JsonValue elements) {
		for (JsonValue e : elements) {
			SpriterTaglineKey key = new SpriterTaglineKey();

			loadTaglineKey(key, e);

			tagline.keys.add(key);
		}
	}

	private void loadTaglineKey(SpriterTaglineKey key, JsonValue e) {
		loadKey(key, e);
		loadTags(key, e.get("tag"));
	}

	private void loadTags(SpriterTaglineKey key, JsonValue elements) {
		for (JsonValue e : elements) {
			SpriterTag tag = new SpriterTag();

			loadTag(tag, e);

			key.tags.add(tag);
		}
	}

	private void loadTag(SpriterTag tag, JsonValue i) {
		loadElement(tag, i);
		tag.tagId = i.getInt("t", tag.tagId);
	}

}
