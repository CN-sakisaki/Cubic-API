package com.saki.apiproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saki.apiproject.model.vo.InterfaceInvokeCountVO;
import com.saki.common.model.entity.InterfaceInfo;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * @author JianShang
 * @version 1.0.0
 * @description 接口信息 Mapper
 * @date 2024-09-13 12:04:56
 */
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {
    List<InterfaceInvokeCountVO> listTopInvokeInterfaceInfo(@Param("limit") int limit);
}




