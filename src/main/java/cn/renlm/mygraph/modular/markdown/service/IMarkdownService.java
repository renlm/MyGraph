package cn.renlm.mygraph.modular.markdown.service;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.markdown.entity.Markdown;
import cn.renlm.plugins.MyResponse.Result;

/**
 * <p>
 * Markdown文档 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
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
