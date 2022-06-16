package cn.renlm.graph.modular.doc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.entity.DocCategoryShare;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 文档分类-分享 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-16
 */
public interface IDocCategoryShareService extends IService<DocCategoryShare> {

	/**
	 * 关闭分享
	 * 
	 * @param user
	 * @param docProjectUuid
	 * @param docCategoryUuid
	 * @return
	 */
	Result<?> closeShare(User user, String docProjectUuid, String docCategoryUuid);

	/**
	 * 保存（新建）
	 * 
	 * @param user
	 * @param form
	 * @return
	 */
	Result<?> ajaxSave(User user, DocCategoryShareDto form);

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<DocCategoryShareDto> findPage(Page<DocCategoryShareDto> page, User user, DocCategoryShareDto form);

}
