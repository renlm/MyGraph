package cn.renlm.mygraph.modular.doc.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.doc.dto.DocProjectDto;
import cn.renlm.mygraph.modular.doc.entity.DocProject;

/**
 * <p>
 * 文档项目 Mapper 接口
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-06-13
 */
public interface DocProjectMapper extends BaseMapper<DocProject> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<DocProjectDto> findPage(Page<DocProjectDto> page, @Param("user") User user, @Param("form") DocProjectDto form);

}
