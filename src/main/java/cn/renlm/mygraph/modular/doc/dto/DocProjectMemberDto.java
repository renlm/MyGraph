package cn.renlm.mygraph.modular.doc.dto;

import cn.renlm.mygraph.modular.doc.entity.DocProjectMember;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文档项目-成员
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DocProjectMemberDto extends DocProjectMember {

	private static final long serialVersionUID = 1L;

	/**
	 * 搜索关键词
	 */
	private String keywords;

	/**
	 * 是否授权
	 */
	private Boolean accessAuth;

	/**
	 * 组织机构ID集
	 */
	private String orgIds;

	/**
	 * 文档项目Uuid
	 */
	private String docProjectUuid;

	/**
	 * 账号
	 */
	private String username;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 手机号码
	 */
	private String mobile;

	/**
	 * 邮箱地址
	 */
	private String email;

}