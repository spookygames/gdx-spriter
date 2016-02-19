// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.badlogic.gdx.Files.FileType;
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
import com.badlogic.gdx.spriter.data.SpriterVarline;
import com.badlogic.gdx.spriter.data.SpriterVarlineKey;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Abstract class to write Spriter data to output of type {@link OutputStream},
 * {@link Writer} or {@link FileHandle}.
 * 
 * Encoding is set to UTF-8.
 * 
 * @see ScmlWriter
 * 
 * @author thorthur
 * 
 */
public abstract class SpriterWriter {

	static interface WriterBean {

		public void attribute(String name, Object value) throws IOException;

		public void element(String name) throws IOException;

		public void array(String name) throws IOException;

		public void subElement() throws IOException;

		public void pop() throws IOException;

		public void close() throws IOException;
	}

	/**
	 * Write Spriter data into given file handle with charset UTF-8. Any
	 * existing file will be erased.
	 * 
	 * @param data
	 *            Spriter data to write
	 * @param file
	 *            Handle on the file to write into
	 * @throws IOException
	 *             If an I/O error occurs
	 * @throws GdxRuntimeException
	 *             if this file handle represents a directory, if it is a
	 *             {@link FileType#Classpath} or {@link FileType#Internal} file,
	 *             or if it could not be written.
	 */
	public void write(SpriterData data, FileHandle file) throws IOException {
		write(data, file.writer(false, "UTF-8"));
	}

	/**
	 * Write Spriter data into given output stream with charset UTF-8.
	 * 
	 * @param data
	 *            Spriter data to write
	 * @param output
	 *            Outputstream to write into
	 * @throws IOException
	 *             If an I/O error occurs
	 * @throws GdxRuntimeException
	 *             if this file handle represents a directory, if it is a
	 *             {@link FileType#Classpath} or {@link FileType#Internal} file,
	 *             or if it could not be written.
	 */
	public void write(SpriterData data, OutputStream output) throws IOException {
		write(data, new OutputStreamWriter(output, "UTF-8"));
	}

	/**
	 * Write Spriter data with given Writer.
	 * 
	 * @param data
	 *            Spriter data to write
	 * @param writer
	 *            Writer to write with
	 * @throws IOException
	 *             If an I/O error occurs
	 * @throws GdxRuntimeException
	 *             if this file handle represents a directory, if it is a
	 *             {@link FileType#Classpath} or {@link FileType#Internal} file,
	 *             or if it could not be written.
	 */
	public void write(SpriterData data, Writer writer) throws IOException {
		writeData(data, wrap(writer));
	}

	abstract WriterBean wrap(Writer writer) throws IOException;

	/**
	 * Get the file extension this Spriter writer would default to.
	 * 
	 * @return The file extension this Spriter writer would default to
	 */
	public abstract String getExtension();

	private void writeData(SpriterData data, WriterBean writer) throws IOException {
		writer.element("spriter_data");
		writer.attribute(getExtension().toLowerCase() + "_version", data.version);
		writer.attribute("generator", data.generator);
		writer.attribute("generator_version", data.generatorVersion);

		writeFolders(data.folders, writer);

		if (data.tags.size > 0) {
			writer.array("tag_list");
			writeElements(data.tags, writer);
			writer.pop();
		}

		writeEntities(data.entities, writer);

		writer.close();
	}

	private void writeElements(Array<SpriterElement> elements, WriterBean writer) throws IOException {
		for (SpriterElement element : elements) {
			writer.subElement();
			writeElement(element, writer);
			writer.pop();
		}
	}

	private void writeFolders(Array<SpriterFolder> folders, WriterBean writer) throws IOException {
		for (SpriterFolder folder : folders) {
			writer.element("folder");
			writeFolder(folder, writer);
			writer.pop();
		}
	}

	private void writeFolder(SpriterFolder folder, WriterBean writer) throws IOException {
		writeElement(folder, writer);
		writeFiles(folder.files, writer);
	}

	private void writeElement(SpriterElement element, WriterBean writer) throws IOException {
		writeElement(element, writer, true);
	}

	private void writeElement(SpriterElement element, WriterBean writer, boolean withId) throws IOException {
		if (withId)
			writer.attribute("id", element.id);
		if (element.name != null)
			writer.attribute("name", element.name);
	}

	private void writeFiles(Array<SpriterFile> files, WriterBean writer) throws IOException {
		for (SpriterFile file : files) {
			writer.element("file");
			writeFile(file, writer);
			writer.pop();
		}
	}

	private void writeFile(SpriterFile file, WriterBean writer) throws IOException {
		writeElement(file, writer);
		if (file.type != SpriterFileType.Image)
			writer.attribute("type", file.type.toString().toLowerCase());
		writer.attribute("width", file.width);
		writer.attribute("height", file.height);
		writer.attribute("pivot_x", doubleToString(file.pivotX));
		writer.attribute("pivot_y", doubleToString(file.pivotY));
	}

	private void writeEntities(Array<SpriterEntity> entities, WriterBean writer) throws IOException {
		for (SpriterEntity entity : entities) {
			writer.element("entity");
			writeEntity(entity, writer);
			writer.pop();
		}
	}

	private void writeEntity(SpriterEntity entity, WriterBean writer) throws IOException {
		writeElement(entity, writer);

		writeObjectInfos(entity.objectInfos, writer);

		if (entity.variables.size > 0) {
			writer.array("var_defs");
			writeVariables(entity.variables, writer);
			writer.pop();
		}

		writeCharacterMaps(entity.characterMaps, writer);
		writeAnimations(entity.animations, writer);
	}

	private void writeVariables(Array<SpriterVarDef> variables, WriterBean writer) throws IOException {
		for (SpriterVarDef var : variables) {
			writer.subElement();
			writeVarDef(var, writer);
			writer.pop();
		}
	}

	private void writeVarDef(SpriterVarDef var, WriterBean writer) throws IOException {
		writeElement(var, writer);
		writer.attribute("type", var.type.toString().toLowerCase());
		writer.attribute("default", var.defaultValue);
	}

	private void writeObjectInfos(Array<SpriterObjectInfo> objectInfos, WriterBean writer) throws IOException {
		for (SpriterObjectInfo objInfo : objectInfos) {
			writer.element("obj_info");
			writeObjectInfo(objInfo, writer);
			writer.pop();
		}
	}

	private void writeObjectInfo(SpriterObjectInfo objInfo, WriterBean writer) throws IOException {
		writeElement(objInfo, writer, false);

		if (objInfo.realName != null)
			writer.attribute("realname", objInfo.realName);
		writer.attribute("type", objInfo.objectType.toString().toLowerCase());
		if (objInfo.width != 0f)
			writer.attribute("w", doubleToString(objInfo.width));
		if (objInfo.height != 0f)
			writer.attribute("h", doubleToString(objInfo.height));
		if (objInfo.pivotX != 0f)
			writer.attribute("pivot_x", doubleToString(objInfo.pivotX));
		if (objInfo.pivotY != 0f)
			writer.attribute("pivot_y", doubleToString(objInfo.pivotY));

		if (objInfo.frames.size > 0) {
			writer.array("frames");
			writeFrames(objInfo.frames, writer);
			writer.pop();
		}

		if (objInfo.variables.size > 0) {
			writer.array("var_defs");
			writeVariables(objInfo.variables, writer);
			writer.pop();
		}
	}

	private void writeFrames(Array<SpriterFileInfo> frames, WriterBean writer) throws IOException {
		for (SpriterFileInfo frame : frames) {
			writer.subElement();
			writeFileInfo(frame, writer);
			writer.pop();
		}
	}

	private void writeCharacterMaps(Array<SpriterCharacterMap> characterMaps, WriterBean writer) throws IOException {
		for (SpriterCharacterMap charMap : characterMaps) {
			writer.element("character_map");
			writeCharacterMap(charMap, writer);
			writer.pop();
		}
	}

	private void writeCharacterMap(SpriterCharacterMap charMap, WriterBean writer) throws IOException {
		writeElement(charMap, writer);
		writeCharacterMapInstructions(charMap.maps, writer);
	}

	private void writeCharacterMapInstructions(Array<SpriterMapInstruction> maps, WriterBean writer) throws IOException {
		for (SpriterMapInstruction instruction : maps) {
			writer.element("map");
			writeMapInstruction(instruction, writer);
			writer.pop();
		}
	}

	private void writeMapInstruction(SpriterMapInstruction instruction, WriterBean writer) throws IOException {
		writeFileInfo(instruction.file, writer);

		if (instruction.target.folderId != -1)
			writer.attribute("target_folder", instruction.target.folderId);
		if (instruction.target.fileId != -1)
			writer.attribute("target_file", instruction.target.fileId);
	}

	private void writeFileInfo(SpriterFileInfo file, WriterBean writer) throws IOException {
		writeFileInfo(file, writer, true);
	}

	private void writeFileInfo(SpriterFileInfo file, WriterBean writer, boolean forceWrite) throws IOException {
		if (forceWrite || file.folderId != 0)
			writer.attribute("folder", file.folderId);
		if (forceWrite || file.fileId != 0)
			writer.attribute("file", file.fileId);
	}

	private void writeAnimations(Array<SpriterAnimation> animations, WriterBean writer) throws IOException {
		for (SpriterAnimation animation : animations) {
			writer.element("animation");
			writeAnimation(animation, writer);
			writer.pop();
		}
	}

	private void writeAnimation(SpriterAnimation animation, WriterBean writer) throws IOException {
		writeElement(animation, writer);
		writer.attribute("length", doubleToString(animation.length));
		writer.attribute("interval", doubleToString(animation.interval));
		if (animation.looping != true)
			writer.attribute("looping", animation.looping);

		writeMainline(animation.mainline, writer);

		writeTimelines(animation.timelines, writer);
		writeEventlines(animation.eventlines, writer);
		writeSoundlines(animation.soundlines, writer);

		if (animation.meta != null) {
			writer.element("meta");
			writeMeta(animation.meta, writer);
			writer.pop();
		}
	}

	private void writeMainline(SpriterMainline main, WriterBean writer) throws IOException {
		writer.element("mainline");
		writeMainlineKeys(main.keys, writer);
		writer.pop();
	}

	private void writeMainlineKeys(Array<SpriterMainlineKey> keys, WriterBean writer) throws IOException {
		for (SpriterMainlineKey key : keys) {
			writer.element("key");
			writeMainlineKey(key, writer);
			writer.pop();
		}
	}

	private void writeMainlineKey(SpriterMainlineKey key, WriterBean writer) throws IOException {
		writeKey(key, writer);
		writeBoneRefs(key.boneRefs, writer);
		writeObjectRefs(key.objectRefs, writer);
	}

	private void writeKey(SpriterKey key, WriterBean writer) throws IOException {
		writeKey(key, writer, false);
	}

	private void writeKey(SpriterKey key, WriterBean writer, boolean alwaysWriteTime) throws IOException {
		writeElement(key, writer);
		if (alwaysWriteTime || key.time != 0f)
			writer.attribute("time", doubleToString(key.time));
		if (key.curveType != SpriterCurveType.Linear)
			writer.attribute("curve_type", key.curveType.toString().toLowerCase());
		if (key.c1 != 0f)
			writer.attribute("c1", key.c1);
		if (key.c2 != 0f)
			writer.attribute("c2", key.c2);
		if (key.c3 != 0f)
			writer.attribute("c3", key.c3);
		if (key.c4 != 0f)
			writer.attribute("c4", key.c4);
	}

	private void writeBoneRefs(Array<SpriterRef> boneRefs, WriterBean writer) throws IOException {
		for (SpriterRef boneRef : boneRefs) {
			writer.element("bone_ref");
			writeRef(boneRef, writer);
			writer.pop();
		}
	}

	private void writeRef(SpriterRef boneRef, WriterBean writer) throws IOException {
		writeElement(boneRef, writer);
		if (boneRef.parentId != -1f)
			writer.attribute("parent", boneRef.parentId);
		writer.attribute("timeline", boneRef.timelineId);
		writer.attribute("key", boneRef.keyId);
	}

	private void writeObjectRefs(Array<SpriterObjectRef> objectRefs, WriterBean writer) throws IOException {
		for (SpriterObjectRef objectRef : objectRefs) {
			writer.element("object_ref");
			writeObjectRef(objectRef, writer);
			writer.pop();
		}
	}

	private void writeObjectRef(SpriterObjectRef objectRef, WriterBean writer) throws IOException {
		writeRef(objectRef, writer);
		writer.attribute("z_index", objectRef.zIndex);
	}

	private void writeTimelines(Array<SpriterTimeline> timelines, WriterBean writer) throws IOException {
		for (SpriterTimeline timeline : timelines) {
			writer.element("timeline");
			writeTimeline(timeline, writer);
			writer.pop();
		}
	}

	private void writeTimeline(SpriterTimeline timeline, WriterBean writer) throws IOException {
		writeElement(timeline, writer);
		if (timeline.objectType != SpriterObjectType.Sprite)
			writer.attribute("object_type", timeline.objectType.toString().toLowerCase());
		if (timeline.objectId != 0)
			writer.attribute("obj", timeline.objectId);
		writeTimelineKeys(timeline.keys, writer);

		if (timeline.meta != null) {
			writer.element("meta");
			writeMeta(timeline.meta, writer);
			writer.pop();
		}
	}

	private void writeTimelineKeys(Array<SpriterTimelineKey> keys, WriterBean writer) throws IOException {
		for (SpriterTimelineKey key : keys) {
			writer.element("key");
			writeTimelineKey(key, writer);
			writer.pop();
		}
	}

	private void writeTimelineKey(SpriterTimelineKey key, WriterBean writer) throws IOException {
		writeKey(key, writer);

		if (key.spin != 1)
			writer.attribute("spin", key.spin);

		if (key.boneInfo != null) {
			writer.element("bone");
			writeSpatial(key.boneInfo, writer);
			writer.pop();
		}

		if (key.objectInfo != null) {
			writer.element("object");
			writeObject(key.objectInfo, writer);
			writer.pop();
		}
	}

	private void writeSpatial(SpriterSpatial spatial, WriterBean writer) throws IOException {
		if (spatial.x != 0f)
			writer.attribute("x", doubleToString(spatial.x));
		if (spatial.y != 0f)
			writer.attribute("y", doubleToString(spatial.y));
		writer.attribute("angle", doubleToString(spatial.angle));
		if (spatial.scaleX != 1f)
			writer.attribute("scale_x", spatial.scaleX);
		if (spatial.scaleY != 1f)
			writer.attribute("scale_y", spatial.scaleY);
		if (spatial.alpha != 1f)
			writer.attribute("a", doubleToString(spatial.alpha));
	}

	private void writeObject(SpriterObject objectInfo, WriterBean writer) throws IOException {

		writeFileInfo(objectInfo.file, writer);

		writeSpatial(objectInfo, writer);

		if (objectInfo.animationId != 0)
			writer.attribute("animation", objectInfo.animationId);
		if (!Float.isNaN(objectInfo.pivotX))
			writer.attribute("pivot_x", doubleToString(objectInfo.pivotX));
		if (!Float.isNaN(objectInfo.pivotY))
			writer.attribute("pivot_y", doubleToString(objectInfo.pivotY));
		if (objectInfo.entityId != 0)
			writer.attribute("entity", objectInfo.entityId);
		if (objectInfo.t != 0f)
			writer.attribute("t", objectInfo.t);
	}

	private void writeEventlines(Array<SpriterEventline> eventlines, WriterBean writer) throws IOException {
		for (SpriterEventline eventline : eventlines) {
			writer.element("eventline");
			writeEventline(eventline, writer);
			writer.pop();
		}
	}

	private void writeEventline(SpriterEventline eventline, WriterBean writer) throws IOException {
		writeElement(eventline, writer);
		writeKeys(eventline.keys, writer);
	}

	private void writeKeys(Array<SpriterKey> keys, WriterBean writer) throws IOException {
		for (SpriterKey key : keys) {
			writer.element("key");
			writeKey(key, writer);
			writer.pop();
		}
	}

	private void writeSoundlines(Array<SpriterSoundline> soundlines, WriterBean writer) throws IOException {
		for (SpriterSoundline soundline : soundlines) {
			writer.element("soundline");
			writeSoundline(soundline, writer);
			writer.pop();
		}
	}

	private void writeSoundline(SpriterSoundline soundline, WriterBean writer) throws IOException {
		writeElement(soundline, writer);
		writeSoundlineKeys(soundline.keys, writer);
	}

	private void writeSoundlineKeys(Array<SpriterSoundlineKey> keys, WriterBean writer) throws IOException {
		for (SpriterSoundlineKey key : keys) {
			writer.element("key");
			writeSoundlineKey(key, writer);
			writer.pop();
		}
	}

	private void writeSoundlineKey(SpriterSoundlineKey key, WriterBean writer) throws IOException {
		writeKey(key, writer);
		writer.element("object");
		writeSound(key.soundObject, writer);
		writer.pop();
	}

	private void writeSound(SpriterSound sound, WriterBean writer) throws IOException {
		writeElement(sound, writer);
		writer.attribute("trigger", sound.trigger);
		writer.attribute("panning", sound.panning);
		writer.attribute("volume", sound.volume);

		writeFileInfo(sound.file, writer);
	}

	private void writeMeta(SpriterMeta meta, WriterBean writer) throws IOException {
		writeVarlines(meta.varlines, writer);

		if (meta.tagline != null) {
			writer.element("tagline");
			writeTagline(meta.tagline, writer);
			writer.pop();
		}
	}

	private void writeVarlines(Array<SpriterVarline> varlines, WriterBean writer) throws IOException {
		for (SpriterVarline varline : varlines) {
			writer.element("varline");
			writeVarline(varline, writer);
			writer.pop();
		}
	}

	private void writeVarline(SpriterVarline varline, WriterBean writer) throws IOException {
		writeElement(varline, writer);
		writer.attribute("def", varline.def);
		writeVarlineKeys(varline.keys, writer);
	}

	private void writeVarlineKeys(Array<SpriterVarlineKey> keys, WriterBean writer) throws IOException {
		for (SpriterVarlineKey key : keys) {
			writer.element("key");
			writeVarlineKey(key, writer);
			writer.pop();
		}
	}

	private void writeVarlineKey(SpriterVarlineKey key, WriterBean writer) throws IOException {
		writeKey(key, writer, true);
		writer.attribute("val", key.value);
	}

	private void writeTagline(SpriterTagline tagline, WriterBean writer) throws IOException {
		writeTaglineKeys(tagline.keys, writer);
	}

	private void writeTaglineKeys(Array<SpriterTaglineKey> keys, WriterBean writer) throws IOException {
		for (SpriterTaglineKey key : keys) {
			writer.element("key");
			writeTaglineKey(key, writer);
			writer.pop();
		}
	}

	private void writeTaglineKey(SpriterTaglineKey key, WriterBean writer) throws IOException {
		writeKey(key, writer, true);
		writeTags(key.tags, writer);
	}

	private void writeTags(Array<SpriterTag> tags, WriterBean writer) throws IOException {
		for (SpriterTag tag : tags) {
			writer.element("tag");
			writeTag(tag, writer);
			writer.pop();
		}
	}

	private void writeTag(SpriterTag tag, WriterBean writer) throws IOException {
		writeElement(tag, writer);
		writer.attribute("t", tag.tagId);
	}

	private static final DecimalFormat doubleFormat = new DecimalFormat("#.######", DecimalFormatSymbols.getInstance(Locale.US));

	private static String doubleToString(double d) {
		return doubleFormat.format(d);
	}

}
