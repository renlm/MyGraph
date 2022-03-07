package cn.renlm.graph.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.ErDto;
import cn.renlm.graph.entity.Er;

/**
 * <p>
 * ER模型 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-03-04
 */
public interface IErService extends IService<Er> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	Page<ErDto> findPage(Page<Er> page, ErDto form);

	/**
	 * 获取包含字段的表信息
	 * 
	 * @param uuids
	 * @return
	 */
	List<ErDto> findListWithFields(List<String> uuids);

}
