//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterMapInstruction {

	public int folderId;
	public int fileId;
	public int targetFolderId = -1;
	public int targetFileId = -1;

	@Override
	public String toString() {
		return "SpriterMapInstruction [folderId=" + folderId + ", fileId=" + fileId + ", targetFolderId=" + targetFolderId + ", targetFileId=" + targetFileId + "]";
	}

}