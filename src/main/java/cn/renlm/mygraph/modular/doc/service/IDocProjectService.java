package cn.renlm.mygraph.modular.doc.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.doc.dto.DocProjectDto;
import cn.renlm.mygraph.modular.doc.entity.DocProject;
import cn.renlm.plugins.MyResponse.Result;

/**
 * <p>
 * 文档项目 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-06-13
 */
public interface IDocProjectService extends IService<DocProject> {

	/**
	 * 获取用户授权角色
	 * 
	 * @param user
	 * @param docProjectId
	 * @return 1：浏览者，2：编辑者，3：管理员
	 */
	Integer findRole(User user, Long docProjectId);

	/**
	 * 全部列表
	 * 
	 * @param user
	 * @return
	 */
	List<DocProjectDto> findAll(User user);

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<DocProjectDto> findPage(Page<DocProjectDto> page, User user, DocProjectDto form);

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param user
	 * @param form
	 * @return
	 */
	Result<DocProjectDto> ajaxSave(User user, DocProjectDto form);

	/**
	 * 根据Uuid删除
	 * 
	 * @param user
	 * @param uuid
	 */
	Result<?> delByUuid(User user, String uuid);

}
