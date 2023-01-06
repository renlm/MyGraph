package cn.renlm.graph.modular.sys.service;

import static cn.renlm.graph.common.CacheKey.DICT_CACHE_NAME;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.plugins.MyResponse.Result;

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
	 * 获取由上而下的父子集
	 * 
	 * @param id
	 * @return
	 */
	List<SysDict> findFathers(Long id);

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param codePaths
	 * @return
	 */
	List<SysDict> findListByPath(String... codePaths);

	/**
	 * 获取树形字典
	 * 
	 * @param root
	 * @param codePaths
	 * @return
	 */
	List<Tree<Long>> getTree(boolean root, String... codePaths);

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param request
	 * @param sysDict
	 * @return
	 */
	@CacheEvict(cacheNames = DICT_CACHE_NAME, allEntries = true)
	Result<SysDict> ajaxSave(SysDict sysDict);

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
	 * @param file
	 * @return
	 */
	@CacheEvict(cacheNames = DICT_CACHE_NAME, allEntries = true)
	Result<List<String>> importDataFromFile(User user, MultipartFile file);

}
