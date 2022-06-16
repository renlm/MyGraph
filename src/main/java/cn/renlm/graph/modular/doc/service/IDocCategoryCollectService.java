package cn.renlm.graph.modular.doc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocCategoryCollectDto;
import cn.renlm.graph.modular.doc.entity.DocCategoryCollect;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 文档分类-收藏 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-15
 */
public interface IDocCategoryCollectService extends IService<DocCategoryCollect> {

	/**
	 * 判断用户是否收藏指定分类
	 * 
	 * @param user
	 * @param docCategoryId
	 * @return
	 */
	boolean isCollected(User user, Long docCategoryId);

	/**
	 * 添加收藏/取消收藏
	 * 
	 * @param type            操作类型，0：取消收藏，1：添加收藏
	 * @param user            登录用户
	 * @param docProjectUuid  项目Uuid
	 * @param docCategoryUuid 分类Uuid
	 * @return
	 */
	Result<?> optCollect(int type, User user, String docProjectUuid, String docCategoryUuid);

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<DocCategoryCollectDto> findPage(Page<DocCategoryCollectDto> page, User user, DocCategoryCollectDto form);

}
