package cn.renlm.graph.modular.markdown.service;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * Markdown 文档 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-13
 */
public interface IMarkdownService extends IService<Markdown> {

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param user
	 * @param form
	 * @return
	 */
	Result<Markdown> ajaxSave(User user, Markdown form);

}
