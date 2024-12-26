package cn.renlm.mygraph.common;

import java.util.List;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import lombok.SneakyThrows;

/**
 * SQLite
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class SQLite {

	public final String dbFile;

	private final DSFactory dsf;

	public final Db db;

	private SQLite(String dbFile, DSFactory dsf, Db db) {
		this.dbFile = dbFile;
		this.dsf = dsf;
		this.db = db;
	}

	/**
	 * 加载数据库文件
	 * 
	 * @param dbFile
	 * @param maximumPoolSize
	 * @return
	 */
	public static final SQLite load(String dbFile, int maximumPoolSize) {
		String jdbcUrlKey = ArrayUtil.firstNonNull(DSFactory.KEY_ALIAS_URL);
		String jdbcUrlVal = StrUtil.format("jdbc:sqlite:{}", dbFile);
		Setting setting = new Setting().set(jdbcUrlKey, jdbcUrlVal);
		setting.set("maximumPoolSize", String.valueOf(maximumPoolSize));
		DSFactory dsf = DSFactory.create(setting);
		Db db = DbUtil.use(dsf.getDataSource());
		SQLite myDb = new SQLite(dbFile, dsf, db);
		return myDb;
	}

	/**
	 * 执行脚本
	 * 
	 * @param scripts
	 */
	@SneakyThrows
	public void execute(String scripts) {
		List<String> list = StrUtil.splitTrim(scripts, ";");
		db.executeBatch(list);
	}

	/**
	 * 压缩数据库
	 */
	@SneakyThrows
	public void VACUUM() {
		db.execute("VACUUM");
	}

	/**
	 * 关闭连接
	 */
	@SneakyThrows
	public void close() {
		dsf.close();
	}

}
