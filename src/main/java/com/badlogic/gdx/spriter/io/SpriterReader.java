// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterCharacterMap;
import com.badlogic.gdx.spriter.data.SpriterCurveType;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterElement;
import com.badlogic.gdx.spriter.data.SpriterEntity;
import com.badlogic.gdx.spriter.data.SpriterEventline;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFileInfo;
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
import com.badlogic.gdx.utils.Array;

public abstract class SpriterReader {

	static interface ReaderBean {
		String get(String name);

		String get(String name, String defaultValue);

		Array<ReaderBean> getChildren();

		Array<ReaderBean> getChildrenByName(String name);

		ReaderBean getChildByName(String name);

		int getInt(String name, int defaultValue);

		float getFloat(String name, float defaultValue);

		boolean getBoolean(String name, boolean defaultValue);
	}

	public SpriterData load(String content) throws IOException {
		return load(new StringReader(content));
	}

	public SpriterData load(FileHandle file) throws IOException {
		return load(file.reader());
	}

	public SpriterData load(InputStream input) throws IOException {
		return load(new InputStreamReader(input));
	}

	public SpriterData load(Reader reader) throws IOException {
		SpriterData data = new SpriterData();

		// First read
		loadData(data, parse(reader));

		// Then clean up...
		initializeData(data);

		return data;
	}

	public abstract ReaderBean parse(Reader reader) throws IOException;

	public abstract String getExtension();

	private void loadData(SpriterData data, ReaderBean root) {
		data.version = root.get(getExtension().toLowerCase() + "_version", data.version);
		data.generator = root.get("generator", data.generator);
		data.generatorVersion = root.get("generator_version", data.generatorVersion);
		loadFolders(data, root.getChildrenByName("folder"));
		loadEntities(data, root.getChildrenByName("entity"));

		ReaderBean tagList = root.getChildByName("tag_list");
		if (tagList != null)
			loadElements(data, tagList.getChildren());
	}

	private void loadElements(SpriterData data, Array<ReaderBean> elements) {
		for (ReaderBean e : elements) {
			SpriterElement element = new SpriterElement();

			loadElement(element, e);

			data.tags.add(element);
		}
	}

	private void loadFolders(SpriterData data, Array<ReaderBean> folders) {
		for (ReaderBean repo : folders) {
			SpriterFolder folder = new SpriterFolder();

			loadFolder(folder, repo);

			data.folders.add(folder);
		}
	}

	private void loadFolder(SpriterFolder folder, ReaderBean repo) {
		loadElement(folder, repo);
		loadFiles(folder, repo.getChildrenByName("file"));
	}

	private void loadElement(SpriterElement element, ReaderBean xmlElement) {
		element.id = xmlElement.getInt("id", element.id);
		element.name = xmlElement.get("name", element.name);
	}

	private void loadFiles(SpriterFolder folder, Array<ReaderBean> files) {
		for (ReaderBean f : files) {
			SpriterFile file = new SpriterFile();

			loadFile(file, f);

			folder.files.add(file);
		}
	}

	private void loadFile(SpriterFile file, ReaderBean f) {
		loadElement(file, f);
		file.type = SpriterFileType.parse(f.get("type", file.type.toString().toLowerCase()));
		file.width = f.getInt("width", file.width);
		file.height = f.getInt("height", file.height);
		file.pivotX = f.getFloat("pivot_x", file.pivotX);
		file.pivotY = f.getFloat("pivot_y", file.pivotY);
	}

	private void loadEntities(SpriterData data, Array<ReaderBean> entities) {
		for (ReaderBean e : entities) {
			SpriterEntity entity = new SpriterEntity();

			loadEntity(entity, e);

			data.entities.add(entity);
		}
	}

	private void loadEntity(SpriterEntity entity, ReaderBean e) {
		loadElement(entity, e);
		loadObjectInfos(entity, e.getChildrenByName("obj_info"));
		loadCharacterMaps(entity, e.getChildrenByName("character_map"));
		loadAnimations(entity, e.getChildrenByName("animation"));

		ReaderBean varDefs = e.getChildByName("var_defs");
		if (varDefs != null)
			loadVariables(entity, varDefs.getChildren());
	}

	private void loadVariables(SpriterVariableContainer container, Array<ReaderBean> elements) {
		for (ReaderBean i : elements) {
			SpriterVarDef var = new SpriterVarDef();

			loadVarDef(var, i);

			container.variables.add(var);
		}
	}

	private void loadVarDef(SpriterVarDef var, ReaderBean i) {
		loadElement(var, i);
		var.type = SpriterVarType.parse(i.get("type", var.type.toString().toLowerCase()));
		var.defaultValue = i.get("default", var.defaultValue);
	}

	private void loadObjectInfos(SpriterEntity entity, Array<ReaderBean> infos) {
		for (ReaderBean info : infos) {
			SpriterObjectInfo objInfo = new SpriterObjectInfo();

			loadObjectInfo(objInfo, info);

			entity.objectInfos.add(objInfo);
		}
	}

	private void loadObjectInfo(SpriterObjectInfo objInfo, ReaderBean info) {
		loadElement(objInfo, info);

		objInfo.realName = info.get("realname", objInfo.realName);
		objInfo.objectType = SpriterObjectType.parse(info.get("type", objInfo.objectType.toString().toLowerCase()));
		objInfo.width = info.getFloat("w", objInfo.width);
		objInfo.height = info.getFloat("h", objInfo.height);
		objInfo.pivotX = info.getFloat("pivot_x", objInfo.pivotX);
		objInfo.pivotY = info.getFloat("pivot_y", objInfo.pivotY);

		ReaderBean frames = info.getChildByName("frames");
		if (frames != null)
			loadFrames(objInfo, frames.getChildren());

		ReaderBean varDefs = info.getChildByName("var_defs");
		if (varDefs != null)
			loadVariables(objInfo, varDefs.getChildren());
	}

	private void loadFrames(SpriterObjectInfo objInfo, Array<ReaderBean> charMaps) {
		for (ReaderBean f : charMaps) {
			SpriterFileInfo frame = new SpriterFileInfo();

			loadFileInfo(frame, f);

			objInfo.frames.add(frame);
		}
	}

	private void loadCharacterMaps(SpriterEntity entity, Array<ReaderBean> charMaps) {
		for (ReaderBean map : charMaps) {
			SpriterCharacterMap charMap = new SpriterCharacterMap();

			loadCharacterMap(charMap, map);

			entity.characterMaps.add(charMap);
		}
	}

	private void loadCharacterMap(SpriterCharacterMap charMap, ReaderBean map) {
		loadElement(charMap, map);
		loadCharacterMapInstructions(charMap, map.getChildrenByName("map"));
	}

	private void loadCharacterMapInstructions(SpriterCharacterMap charMap, Array<ReaderBean> maps) {
		for (ReaderBean mapping : maps) {
			SpriterMapInstruction instruction = new SpriterMapInstruction();

			loadMapInstruction(instruction, mapping);

			charMap.maps.add(instruction);
		}
	}

	private void loadMapInstruction(SpriterMapInstruction instruction, ReaderBean mapping) {
		SpriterFileInfo source = new SpriterFileInfo();
		loadFileInfo(source, mapping);
		instruction.file = source;

		SpriterFileInfo target = new SpriterFileInfo();
		target.folderId = mapping.getInt("target_folder", -1);
		target.fileId = mapping.getInt("target_file", -1);
		instruction.target = target;
	}

	private void loadFileInfo(SpriterFileInfo file, ReaderBean element) {
		file.folderId = element.getInt("folder", file.folderId);
		file.fileId = element.getInt("file", file.fileId);
	}

	private void loadAnimations(SpriterEntity entity, Array<ReaderBean> animations) {
		for (ReaderBean a : animations) {
			SpriterAnimation animation = new SpriterAnimation();

			loadAnimation(animation, a);

			entity.animations.add(animation);
		}
	}

	private void loadAnimation(SpriterAnimation animation, ReaderBean a) {
		loadElement(animation, a);
		animation.length = a.getFloat("length", animation.length);
		animation.looping = a.getBoolean("looping", animation.looping);
		animation.interval = a.getFloat("interval", animation.interval);

		SpriterMainline main = new SpriterMainline();
		loadMainline(main, a.getChildByName("mainline"));
		animation.mainline = main;

		loadTimelines(animation, a.getChildrenByName("timeline"));
		loadEventlines(animation, a.getChildrenByName("eventline"));
		loadSoundlines(animation, a.getChildrenByName("soundline"));

		ReaderBean xmlMeta = a.getChildByName("meta");
		if (xmlMeta != null) {
			SpriterMeta meta = new SpriterMeta();
			loadMeta(meta, xmlMeta);
			animation.meta = meta;
		}
	}

	private void loadMainline(SpriterMainline main, ReaderBean mainline) {
		loadMainlineKeys(main, mainline.getChildrenByName("key"));
	}

	private void loadMainlineKeys(SpriterMainline main, Array<ReaderBean> keys) {
		for (ReaderBean k : keys) {
			SpriterMainlineKey key = new SpriterMainlineKey();

			loadMainlineKey(key, k);

			main.keys.add(key);
		}
	}

	private void loadMainlineKey(SpriterMainlineKey key, ReaderBean k) {
		loadKey(key, k);
		loadBoneRefs(key, k.getChildrenByName("bone_ref"));
		loadObjectRefs(key, k.getChildrenByName("object_ref"));
	}

	private void loadKey(SpriterKey key, ReaderBean k) {
		loadElement(key, k);
		key.time = k.getFloat("time", key.time);
		key.curveType = SpriterCurveType.parse(k.get("curve_type", key.curveType.toString().toLowerCase()));
		key.c1 = k.getFloat("c1", key.c1);
		key.c2 = k.getFloat("c2", key.c2);
		key.c3 = k.getFloat("c3", key.c3);
		key.c4 = k.getFloat("c4", key.c4);
	}

	private void loadBoneRefs(SpriterMainlineKey key, Array<ReaderBean> boneRefs) {
		for (ReaderBean e : boneRefs) {
			SpriterRef boneRef = new SpriterRef();

			loadRef(boneRef, e);

			key.boneRefs.add(boneRef);
		}
	}

	private void loadRef(SpriterRef boneRef, ReaderBean e) {
		loadElement(boneRef, e);
		boneRef.timelineId = e.getInt("timeline", boneRef.timelineId);
		boneRef.keyId = e.getInt("key", boneRef.keyId);
		boneRef.parentId = e.getInt("parent", boneRef.parentId);
	}

	private void loadObjectRefs(SpriterMainlineKey key, Array<ReaderBean> objectRefs) {
		for (ReaderBean o : objectRefs) {
			SpriterObjectRef objectRef = new SpriterObjectRef();

			loadObjectRef(objectRef, o);

			key.objectRefs.add(objectRef);
		}
		key.objectRefs.sort();
	}

	private void loadObjectRef(SpriterObjectRef objectRef, ReaderBean o) {
		loadRef(objectRef, o);
		objectRef.zIndex = o.getInt("z_index", objectRef.zIndex);
	}

	private void loadTimelines(SpriterAnimation animation, Array<ReaderBean> timelines) {
		for (ReaderBean t : timelines) {
			SpriterTimeline timeline = new SpriterTimeline();

			loadTimeline(timeline, t);

			animation.timelines.add(timeline);
		}
	}

	private void loadTimeline(SpriterTimeline timeline, ReaderBean t) {
		loadElement(timeline, t);
		timeline.objectType = SpriterObjectType.parse(t.get("object_type", timeline.objectType.toString().toLowerCase()));
		timeline.objectId = t.getInt("obj", timeline.objectId);
		loadTimelineKeys(timeline, t.getChildrenByName("key"));

		ReaderBean xmlMeta = t.getChildByName("meta");
		if (xmlMeta != null) {
			SpriterMeta meta = new SpriterMeta();
			loadMeta(meta, xmlMeta);
			timeline.meta = meta;
		}
	}

	private void loadTimelineKeys(SpriterTimeline timeline, Array<ReaderBean> keys) {
		for (ReaderBean k : keys) {
			SpriterTimelineKey key = new SpriterTimelineKey();

			loadTimelineKey(key, k);

			timeline.keys.add(key);
		}
	}

	private void loadTimelineKey(SpriterTimelineKey key, ReaderBean k) {
		loadKey(key, k);

		key.spin = k.getInt("spin", key.spin);

		ReaderBean obj = k.getChildByName("bone");
		if (obj != null) {
			SpriterSpatial boneInfo = new SpriterSpatial();

			loadSpatial(boneInfo, obj);

			key.boneInfo = boneInfo;
		}

		obj = k.getChildByName("object");
		if (obj != null) {
			SpriterObject objectInfo = new SpriterObject();

			loadObject(objectInfo, obj);

			key.objectInfo = objectInfo;
		}
	}

	private void loadSpatial(SpriterSpatial spatial, ReaderBean obj) {
		spatial.x = obj.getFloat("x", spatial.x);
		spatial.y = obj.getFloat("y", spatial.y);
		spatial.scaleX = obj.getFloat("scale_x", spatial.scaleX);
		spatial.scaleY = obj.getFloat("scale_y", spatial.scaleY);
		spatial.angle = obj.getFloat("angle", spatial.angle);
		spatial.alpha = obj.getFloat("a", spatial.alpha);
	}

	private void loadObject(SpriterObject objectInfo, ReaderBean element) {
		loadSpatial(objectInfo, element);
		objectInfo.animationId = element.getInt("animation", objectInfo.animationId);
		objectInfo.pivotX = element.getFloat("pivot_x", objectInfo.pivotX);
		objectInfo.pivotY = element.getFloat("pivot_y", objectInfo.pivotY);
		objectInfo.entityId = element.getInt("entity", objectInfo.entityId);
		objectInfo.t = element.getFloat("t", objectInfo.t);

		SpriterFileInfo file = new SpriterFileInfo();
		loadFileInfo(file, element);
		objectInfo.file = file;
	}

	private void loadEventlines(SpriterAnimation animation, Array<ReaderBean> elements) {
		for (ReaderBean e : elements) {
			SpriterEventline eventline = new SpriterEventline();

			loadEventline(eventline, e);

			animation.eventlines.add(eventline);
		}
	}

	private void loadEventline(SpriterEventline eventline, ReaderBean e) {
		loadElement(eventline, e);
		loadKeys(eventline, e.getChildrenByName("key"));
	}

	private void loadKeys(SpriterEventline eventline, Array<ReaderBean> elements) {
		for (ReaderBean e : elements) {
			SpriterKey key = new SpriterKey();

			loadKey(key, e);

			eventline.keys.add(key);
		}
	}

	private void loadSoundlines(SpriterAnimation animation, Array<ReaderBean> elements) {
		for (ReaderBean e : elements) {
			SpriterSoundline soundline = new SpriterSoundline();

			loadSoundline(soundline, e);

			animation.soundlines.add(soundline);
		}
	}

	private void loadSoundline(SpriterSoundline soundline, ReaderBean e) {
		loadElement(soundline, e);
		loadSoundlineKeys(soundline, e.getChildrenByName("key"));
	}

	private void loadSoundlineKeys(SpriterSoundline soundline, Array<ReaderBean> elements) {
		for (ReaderBean e : elements) {
			SpriterSoundlineKey key = new SpriterSoundlineKey();

			loadSoundlineKey(key, e);

			soundline.keys.add(key);
		}
	}

	private void loadSoundlineKey(SpriterSoundlineKey key, ReaderBean e) {
		loadKey(key, e);
		SpriterSound sound = new SpriterSound();
		loadSound(sound, e.getChildByName("object"));
		key.soundObject = sound;
	}

	private void loadSound(SpriterSound sound, ReaderBean element) {
		loadElement(sound, element);
		sound.trigger = element.getBoolean("trigger", sound.trigger);
		sound.panning = element.getFloat("panning", sound.panning);
		sound.volume = element.getFloat("volume", sound.volume);

		SpriterFileInfo file = new SpriterFileInfo();
		loadFileInfo(file, element);
		sound.file = file;
	}

	private void loadMeta(SpriterMeta meta, ReaderBean element) {
		loadVarlines(meta, element.getChildrenByName("varline"));

		ReaderBean xmlTagline = element.getChildByName("tagline");
		if (xmlTagline != null) {
			SpriterTagline tagline = new SpriterTagline();
			loadTagline(tagline, xmlTagline);
			meta.tagline = tagline;
		}
	}

	private void loadVarlines(SpriterMeta meta, Array<ReaderBean> elements) {
		for (ReaderBean e : elements) {
			SpriterVarline varline = new SpriterVarline();

			loadVarline(varline, e);

			meta.varlines.add(varline);
		}
	}

	private void loadVarline(SpriterVarline varline, ReaderBean e) {
		loadElement(varline, e);
		varline.def = e.getInt("def", varline.def);
		loadVarlineKeys(varline, e.getChildrenByName("key"));
	}

	private void loadVarlineKeys(SpriterVarline varline, Array<ReaderBean> elements) {
		for (ReaderBean e : elements) {
			SpriterVarlineKey key = new SpriterVarlineKey();

			loadVarlineKey(key, e);

			varline.keys.add(key);
		}
	}

	private void loadVarlineKey(SpriterVarlineKey key, ReaderBean e) {
		loadKey(key, e);
		key.value = e.get("val", key.value);
	}

	private void loadTagline(SpriterTagline tagline, ReaderBean e) {
		loadTaglineKeys(tagline, e.getChildrenByName("key"));
	}

	private void loadTaglineKeys(SpriterTagline tagline, Array<ReaderBean> elements) {
		for (ReaderBean e : elements) {
			SpriterTaglineKey key = new SpriterTaglineKey();

			loadTaglineKey(key, e);

			tagline.keys.add(key);
		}
	}

	private void loadTaglineKey(SpriterTaglineKey key, ReaderBean e) {
		loadKey(key, e);
		loadTags(key, e.getChildrenByName("tag"));
	}

	private void loadTags(SpriterTaglineKey key, Array<ReaderBean> elements) {
		for (ReaderBean e : elements) {
			SpriterTag tag = new SpriterTag();

			loadTag(tag, e);

			key.tags.add(tag);
		}
	}

	private void loadTag(SpriterTag tag, ReaderBean i) {
		loadElement(tag, i);
		tag.tagId = i.getInt("t", tag.tagId);
	}

	public void initializeData(SpriterData data) {
		for (SpriterEntity entity : data.entities) {
			entity.data = data;
			for (SpriterAnimation a : entity.animations) {
				a.entity = entity;

				// Initialize vardefs
				if (a.meta != null) {

					for (SpriterVarline v : a.meta.varlines)
						initializeVarline(v, entity.variables.get(v.def));

					for (SpriterTimeline timeline : a.timelines)
						if (timeline.meta != null)
							for (SpriterVarline v : timeline.meta.varlines)
								for (SpriterObjectInfo o : entity.objectInfos)
									if (timeline.name.equals(o.name))
										initializeVarline(v, o.variables.get(v.def));

				}
			}
		}
	}

	private void initializeVarline(SpriterVarline varline, SpriterVarDef varDef) {
		varDef.variableValue = varDef.type.buildVarValue(varDef.defaultValue);
		for (SpriterVarlineKey key : varline.keys)
			key.variableValue = varDef.type.buildVarValue(key.value);
	}

}