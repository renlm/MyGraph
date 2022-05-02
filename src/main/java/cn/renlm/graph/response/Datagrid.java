package cn.renlm.graph.response;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * EasyUI 数据表格
 *
 * @author Renlm
 */
@Data
@Accessors(chain = true)
public class Datagrid<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 总数
	 */
	protected long total = 0;

	/**
	 * 数据集
	 */
	protected List<T> rows;

	/**
	 * 转换列表数据
	 * 
	 * @param <R>
	 * @param datas
	 * @return
	 */
	public static final <R> Datagrid<R> of(List<R> datas) {
		Datagrid<R> datagrid = new Datagrid<R>();
		datagrid.setTotal(CollUtil.size(datas));
		datagrid.setRows(datas);
		return datagrid;
	}

	/**
	 * 转换分页数据
	 * 
	 * @param <R>
	 * @param page
	 * @return
	 */
	public static final <R> Datagrid<R> of(Page<R> page) {
		Datagrid<R> datagrid = new Datagrid<R>();
		datagrid.setTotal(page.getTotal());
		datagrid.setRows(page.getRecords());
		return datagrid;
	}
}