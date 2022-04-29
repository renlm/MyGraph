package cn.renlm.graph.modular.graph.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.graph.entity.Users;
import cn.renlm.graph.modular.graph.mapper.UsersMapper;
import cn.renlm.graph.modular.graph.service.IUsersService;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

}
