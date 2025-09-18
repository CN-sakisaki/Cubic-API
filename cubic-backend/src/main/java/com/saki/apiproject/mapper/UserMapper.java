package com.saki.apiproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saki.common.model.entity.User;
import org.springframework.data.repository.query.Param;


/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户 Mapper
 * @date 2024-09-13 12:05:27
 */
public interface UserMapper extends BaseMapper<User> {

    int updateUserBalance(@Param("userId") Long userId, @Param("amount") int amount);
}




