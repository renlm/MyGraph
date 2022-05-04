package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
public interface ISysDictService extends IService<SysDict> {

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param pid
	 * @return
	 */
	List<SysDict> findListByPid(Long pid);

	/**
	 * 获取树形字典
	 * 
	 * @param codePaths
	 * @return
	 */
	List<Tree<Long>> getTree(String... codePaths);

	/**
	 * 导出字典（重新编排id及pid）
	 * 
	 * @param file
	 */
	void exportDataToFile(SysFile file);

	/**
	 * 导入字典
	 * 
	 * @param user
	 * @param fileId
	 * @return
	 */
	Result<List<String>> importDataFromFile(User user, String fileId);

}
