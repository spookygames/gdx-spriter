/**
 * Copyright (c) 2015-2016 Spooky Games
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgement in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package net.spookygames.gdx.spriter.data;

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
