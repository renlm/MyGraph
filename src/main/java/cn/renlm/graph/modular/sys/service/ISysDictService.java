package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.common.Result;
import cn.renlm.graph.modular.sys.dto.DictDto;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.security.User;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author Renlm
 * @since 2020-09-17
 */
public interface ISysDictService extends IService<SysDict> {

	/**
	 * 导出文件（重新编排id及pid）
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
	Result<List<String>> importConfigFromFile(User user, String fileId);

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param paths 由上至下编码串
	 * @return
	 */
	List<SysDict> findPaths(String... paths);

	/**
	 * 获取字典树
	 * 
	 * @param paths 由上至下编码串
	 * @return
	 */
	List<SysDict> findTreeList(String... paths);

	/**
	 * 获取指定层级字典列表
	 * 
	 * @param level
	 * @return
	 */
	List<DictDto> findListByLevel(Integer level);

	/**
	 * 获取指定父节点下级字典列表
	 * 
	 * @param pid
	 * @return
	 */
	List<DictDto> findListByPid(Long pid);

	/**
	 * 获取由下而上的子父集
	 * 
	 * @param id
	 * @return
	 */
	List<SysDict> findFathers(Long id);

	/**
	 * 获取指定父节点及其下所有子节点
	 * 
	 * @param pid
	 * @return
	 */
	List<DictDto> findChilds(Long pid);

	/**
	 * 根据Uuid获取详细信息
	 * 
	 * @param uuid
	 * @return
	 */
	DictDto getDetailByUuid(String uuid);

}
