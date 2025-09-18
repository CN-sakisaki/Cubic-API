package com.saki.apiproject.cache;

import com.saki.common.model.entity.InterfaceInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class InterfaceCacheServiceTest {
    @Autowired
    private InterfaceCacheService interfaceCacheService;

    @Test
    public void testTwoLevelCache() {
        Long id = 1L; // 假设数据库里有这个接口

        // 第一次：走DB -> Redis -> Caffeine
        InterfaceInfo info1 = interfaceCacheService.getInterfaceInfoById(id);
        System.out.println("第一次查询：" + info1);

        // 第二次：应直接命中Caffeine
        InterfaceInfo info2 = interfaceCacheService.getInterfaceInfoById(id);
        System.out.println("第二次查询：" + info2);
    }
}