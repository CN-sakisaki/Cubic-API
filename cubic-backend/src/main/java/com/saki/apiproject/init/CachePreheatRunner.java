package com.saki.apiproject.init;

import com.saki.apiproject.cache.InterfaceCacheService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 项目启动时预热缓存
 * @author sakisaki
 * @date 2025/9/17 23:20
 */
@Component
public class CachePreheatRunner implements CommandLineRunner {

    @Resource
    private InterfaceCacheService interfaceCacheService;

    @Override
    public void run(String... args) {
        interfaceCacheService.preheatHotInterfaces();
    }
}
