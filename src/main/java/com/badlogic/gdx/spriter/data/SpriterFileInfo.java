//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterFileInfo {

	public int fileId = -1;
	public int folderId = -1;

	public SpriterFileInfo() {
	}

	public SpriterFileInfo(SpriterFileInfo other) {
		this.fileId = other.fileId;
		this.folderId = other.folderId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fileId;
		result = prime * result + folderId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpriterFileInfo other = (SpriterFileInfo) obj;
		if (fileId != other.fileId)
			return false;
		if (folderId != other.folderId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SpriterFileInfo [folderId=" + folderId + ", fileId=" + fileId + "]";
	}

}
