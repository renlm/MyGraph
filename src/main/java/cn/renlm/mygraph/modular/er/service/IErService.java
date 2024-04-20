package cn.renlm.mygraph.modular.er.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.er.dto.ErDto;
import cn.renlm.mygraph.modular.er.entity.Er;

/**
 * <p>
 * ER模型 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
public interface IErService extends IService<Er> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<ErDto> findPage(Page<Er> page, User user, ErDto form);

	/**
	 * 获取包含字段的表信息
	 * 
	 * @param uuids
	 * @return
	 */
	List<ErDto> findListWithFields(List<String> uuids);

}
