package cn.renlm.graph.modular.doc.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectMemberDto;
import cn.renlm.graph.modular.doc.entity.DocProjectMember;

/**
 * <p>
 * 文档项目-成员 Mapper 接口
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
public interface DocProjectMemberMapper extends BaseMapper<DocProjectMember> {

	/**
	 * 分页列表（项目授权人员）
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<DocProjectMemberDto> findAuthAccessPage(Page<DocProjectMemberDto> page, @Param("user") User user,
			@Param("form") DocProjectMemberDto form);

}
