package cn.renlm.graph.modular.doc.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.plugins.MyResponse.Result;

/**
 * <p>
 * 文档分类 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-06-13
 */
public interface IDocCategoryService extends IService<DocCategory> {

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param docProjectUuid
	 * @param pid
	 * @return
	 */
	List<DocCategory> findListByPid(String docProjectUuid, Long pid);

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param docProjectUuid
	 * @param id
	 * @return
	 */
	List<DocCategory> findFathers(String docProjectUuid, Long id);

	/**
	 * 获取树形结构
	 * 
	 * @param docProjectUuid
	 * @param root
	 * @param pid
	 * @return
	 */
	List<Tree<Long>> getTree(String docProjectUuid, boolean root, Long pid);

	/**
	 * 保存|更新
	 * 
	 * @param user
	 * @param docProjectUuid
	 * @param docCategory
	 * @return
	 */
	Result<DocCategory> ajaxSave(User user, String docProjectUuid, DocCategory docCategory);

	/**
	 * 根据Uuid删除
	 * 
	 * @param user
	 * @param uuid
	 */
	Result<?> delByUuid(User user, String uuid);

}
