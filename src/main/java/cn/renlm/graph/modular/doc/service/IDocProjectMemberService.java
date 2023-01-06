package cn.renlm.graph.modular.doc.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectMemberDto;
import cn.renlm.graph.modular.doc.entity.DocProjectMember;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.plugins.MyResponse.Result;

/**
 * <p>
 * 文档项目-成员 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
public interface IDocProjectMemberService extends IService<DocProjectMember> {

	/**
	 * 分页列表（项目授权人员）
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<DocProjectMemberDto> findAuthAccessPage(Page<SysUser> page, User user, DocProjectMemberDto form);

	/**
	 * 添加项目授权人员
	 * 
	 * @param verify
	 * @param user
	 * @param role
	 * @param docProjectUuid
	 * @param userIds
	 * @return
	 */
	Result<?> addRoleMember(boolean verify, User user, Integer role, String docProjectUuid, List<String> userIds);

	/**
	 * 修改授权角色
	 * 
	 * @param user
	 * @param role
	 * @param docProjectUuid
	 * @param userIds
	 * @return
	 */
	Result<?> editRole(User user, Integer role, String docProjectUuid, List<String> userIds);

	/**
	 * 移除项目授权人员
	 * 
	 * @param user
	 * @param docProjectUuid
	 * @param userIds
	 * @return
	 */
	Result<?> removeRoleMember(User user, String docProjectUuid, List<String> userIds);

}
