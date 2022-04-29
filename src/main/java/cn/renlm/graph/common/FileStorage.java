package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 文件存储位置
 * 
 * @author Renlm
 *
 */
public enum FileStorage {

	dbPri(1, false, null, null), 
	dbPub(1, true, null, null), 
	filePri(2, false, "/mnt/file", null),
	filePub(2, true, "/mnt/file", "/public");

	@Getter
	private final int type;

	@Getter
	private final boolean isPublic;

	@Getter
	private final String filePath;

	@Getter
	private final String publicPath;

	private FileStorage(int type, boolean isPublic, String filePath, String publicPath) {
		this.type = type;
		this.isPublic = isPublic;
		this.filePath = filePath;
		this.publicPath = publicPath;
	}

	public boolean isDb() {
		return this == dbPri || this == dbPub;
	}
}