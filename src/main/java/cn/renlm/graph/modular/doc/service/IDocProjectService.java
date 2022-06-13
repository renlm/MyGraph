package cn.renlm.graph.modular.doc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectDto;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 文档项目 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
public interface IDocProjectService extends IService<DocProject> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<DocProject> findPage(Page<DocProject> page, User user, DocProjectDto form);

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param user
	 * @param form
	 * @return
	 */
	Result<DocProjectDto> ajaxSave(User user, DocProjectDto form);

	/**
	 * 根据Uuid批量删除
	 * 
	 * @param user
	 * @param uuids
	 */
	void delByUuids(User user, String... uuids);

}
