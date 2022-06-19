package cn.renlm.graph.dto;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import cn.renlm.graph.response.Result;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 认证用户（文档分享）
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
public class DocShareUser implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 分享Uuid
	 */
	private String shareUuid;

	/**
	 * 验证密码
	 * 
	 * @param request
	 * @param shareUuid
	 * @param password
	 * @return
	 */
	public static final Result<?> verifyPassword(HttpServletRequest request, String shareUuid, String password) {
		return Result.success();
	}

	/**
	 * 获取认证用户信息
	 * 
	 * @param request
	 * @return
	 */
	public static final DocShareUser getInfo(HttpServletRequest request) {
		return null;
	}
}