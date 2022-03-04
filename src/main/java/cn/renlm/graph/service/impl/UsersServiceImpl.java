package cn.renlm.graph.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.entity.Users;
import cn.renlm.graph.mapper.UsersMapper;
import cn.renlm.graph.service.IUsersService;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-03-04
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

}
