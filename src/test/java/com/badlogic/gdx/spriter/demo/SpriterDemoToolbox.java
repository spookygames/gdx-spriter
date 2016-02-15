// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.demo;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterCharacterMap;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterEntity;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFileInfo;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.spriter.data.SpriterMapInstruction;
import com.badlogic.gdx.spriter.data.SpriterObjectInfo;
import com.badlogic.gdx.spriter.data.SpriterSoundline;
import com.badlogic.gdx.spriter.data.SpriterSoundlineKey;
import com.badlogic.gdx.spriter.data.SpriterTimeline;
import com.badlogic.gdx.spriter.data.SpriterTimelineKey;
import com.badlogic.gdx.spriter.io.ScmlWriter;
import com.badlogic.gdx.spriter.io.SpriterWriter;
import com.badlogic.gdx.spriter.loader.SpriterDataFormatUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SpriterDemoToolbox {

	/**
	 * Remove files and folders that are not referenced anywhere in the rest of
	 * the file.
	 * 
	 * @param spriterFile
	 */
	static void removeUnusedFiles(FileHandle spriterFile) {
		try {
			backup(spriterFile);
			SpriterData data = SpriterDataFormatUtils.getProperReader(spriterFile).load(spriterFile);
			removeUnusedFiles(data);
			save(data, spriterFile);
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	/**
	 * Remove files and folders that are duplicate (same underlying file) of
	 * others.
	 * 
	 * @param spriterFile
	 */
	static void removeDuplicateFiles(FileHandle spriterFile) {
		try {
			backup(spriterFile);
			SpriterData data = SpriterDataFormatUtils.getProperReader(spriterFile).load(spriterFile);
			removeDuplicateFiles(data);
			save(data, spriterFile);
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	private static void removeUnusedFiles(SpriterData data) {
		removeUnusedFiles(data.folders, getAllFileInfos(data));
	}

	private static void removeUnusedFiles(Array<SpriterFolder> folders, Array<SpriterFileInfo> usage) {
		Array<SpriterFolder> toRemoveFrom = new Array<SpriterFolder>();
		Array<SpriterFile> toRemove = new Array<SpriterFile>();

		// Mark
		for (SpriterFolder folder : folders) {
			for (SpriterFile file : folder.files) {
				if (!fileIsUsed(usage, folder, file)) {
					toRemoveFrom.add(folder);
					toRemove.add(file);
				}
			}
		}

		// Perform
		removeFiles(folders, usage, toRemoveFrom, toRemove);
	}

	private static void removeDuplicateFiles(SpriterData data) {
		Array<SpriterFolder> toRemoveFrom = new Array<SpriterFolder>();
		Array<SpriterFile> toRemove = new Array<SpriterFile>();

		Array<SpriterFolder> folders = data.folders;
		Array<SpriterFileInfo> usage = getAllFileInfos(data);

		// Change references
		for (int i = 0; i < folders.size; i++) {
			SpriterFolder folder = folders.get(i);
			for (int j = 0; j < folder.files.size; j++) {
				SpriterFile file = folder.files.get(j);
				SpriterFileInfo duplicate = findDuplicateFile(folders, folder, file);
				if (duplicate != null) {
					SpriterFileInfo original = new SpriterFileInfo();
					original.folderId = folder.id;
					original.fileId = file.id;
					replaceReferences(usage, original, duplicate);
					toRemoveFrom.add(folder);
					toRemove.add(file);
				}
			}
		}

		// Remove dups
		removeFiles(folders, usage, toRemoveFrom, toRemove);
	}

	private static Array<SpriterFileInfo> getAllFileInfos(SpriterData data) {
		Array<SpriterFileInfo> fileInfos = new Array<SpriterFileInfo>();
		for (SpriterEntity entity : data.entities) {
			for (SpriterObjectInfo objectInfo : entity.objectInfos) {
				for (SpriterFileInfo frame : objectInfo.frames) {
					fileInfos.add(frame);
				}
			}
			for (SpriterCharacterMap characterMap : entity.characterMaps) {
				for (SpriterMapInstruction instruction : characterMap.maps) {
					fileInfos.add(instruction.file);
					fileInfos.add(instruction.target);
				}
			}
			for (SpriterAnimation animation : entity.animations) {
				for (SpriterTimeline timeline : animation.timelines) {
					for (SpriterTimelineKey key : timeline.keys) {
						if (key.objectInfo != null) {
							fileInfos.add(key.objectInfo.file);
						}
					}
				}
				for (SpriterSoundline soundline : animation.soundlines) {
					for (SpriterSoundlineKey key : soundline.keys) {
						fileInfos.add(key.soundObject.file);
					}
				}
			}
		}
		return fileInfos;
	}

	private static SpriterFileInfo findDuplicateFile(Array<SpriterFolder> folders, SpriterFolder targetFolder, SpriterFile targetFile) {
		String name = targetFile.name;
		int targetFolderId = targetFolder.id;
		int targetFileId = targetFile.id;

		for (SpriterFolder folder : folders) {
			int folderId = folder.id;

			if (folderId < targetFolderId) {
				// Only go forward
				continue;
			}

			for (SpriterFile file : folder.files) {
				int fileId = file.id;

				if (folderId == targetFolderId && fileId <= targetFileId) {
					// Only go forward
					// Skip itself
					continue;
				}

				if (name.equals(file.name)) {
					SpriterFileInfo duplicate = new SpriterFileInfo();
					duplicate.folderId = folderId;
					duplicate.fileId = fileId;
					return duplicate;
				}
			}
		}
		return null;
	}

	private static boolean fileIsUsed(Array<SpriterFileInfo> usage, SpriterFolder folder, SpriterFile file) {
		SpriterFileInfo f = new SpriterFileInfo();
		f.folderId = folder.id;
		f.fileId = file.id;

		return usage.contains(f, false);
	}

	private static void removeFiles(Array<SpriterFolder> folders, Array<SpriterFileInfo> usage, Array<SpriterFolder> toRemoveFrom, Array<SpriterFile> toRemove) {
		for (int i = 0; i < toRemoveFrom.size; i++) {
			SpriterFolder folder = toRemoveFrom.get(i);
			removeFile(usage, folder, toRemove.get(i));
			if (folderIsEmpty(folder)) {
				removeFolder(usage, folders, folder);
			}
		}
	}

	private static void removeFile(Array<SpriterFileInfo> usage, SpriterFolder folder, SpriterFile file) {
		Array<SpriterFile> files = folder.files;
		int index = -1;
		for (int i = 0; i < files.size; i++) {
			SpriterFile f = files.get(i);
			if (f.id == file.id)
				index = i;
			if (f.id > file.id)
				changeFileId(usage, folder, f, f.id - 1);
		}
		files.removeIndex(index);
	}

	private static void changeFileId(Array<SpriterFileInfo> usage, SpriterFolder folder, SpriterFile file, int newId) {
		int actualFileId = file.id;
		int actualFolderId = folder.id;
		for (SpriterFileInfo info : usage) {
			if (info.folderId == actualFolderId && info.fileId == actualFileId) {
				info.fileId = newId;
			}
		}
		file.id = newId;
	}

	private static boolean folderIsEmpty(SpriterFolder folder) {
		return folder.files.size == 0;
	}

	private static void removeFolder(Array<SpriterFileInfo> usage, Array<SpriterFolder> folders, SpriterFolder folder) {
		int index = -1;
		for (int i = 0; i < folders.size; i++) {
			SpriterFolder f = folders.get(i);
			if (f.id == folder.id)
				index = i;
			if (f.id > folder.id)
				changeFolderId(usage, f, f.id - 1);
		}
		folders.removeIndex(index);
	}

	private static void changeFolderId(Array<SpriterFileInfo> usage, SpriterFolder folder, int newId) {
		int actualId = folder.id;
		for (SpriterFileInfo info : usage) {
			if (info.folderId == actualId) {
				info.folderId = newId;
			}
		}
		folder.id = newId;
	}

	private static void replaceReferences(Array<SpriterFileInfo> usage, SpriterFileInfo source, SpriterFileInfo destination) {
		for (SpriterFileInfo info : usage) {
			if (info.equals(source)) {
				info.folderId = destination.folderId;
				info.fileId = destination.fileId;
			}
		}
	}

	private static final DateFormat hourFormat = new SimpleDateFormat("HHmmss");

	private static void backup(FileHandle file) {
		file.copyTo(file.sibling(file.nameWithoutExtension() + "." + hourFormat.format(new Date()) + "." + file.extension()));
	}

	private static void save(SpriterData data, FileHandle file) {
		SpriterWriter writer = new ScmlWriter();
		try {
			writer.write(data, file);
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

}
