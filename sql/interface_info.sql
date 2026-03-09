INSERT INTO jsapi.interface_info (id, name, description, url, requestParams, responseParams, returnFormat,
                                  requestExample, requestHeader, responseHeader, status, method, userId, avatarUrl,
                                  totalInvokes, reduceScore, createTime, updateTime, isDeleted)
VALUES (1, '获取名字', '获取名字', 'http://localhost:8090/api/name', '[
  {
    "fieldName": "name",
    "required": "是",
    "desc": "名字",
    "type": "string"
  }
]', '[
  {
    "fieldName": "code",
    "desc": "返回码",
    "type": "int"
  },
  {
    "fieldName": "data",
    "desc": "返回的数据",
    "type": "Object"
  },
  {
    "fieldName": "message",
    "desc": "返回码的信息",
    "type": "String"
  }
]', 'JSON', 'http://localhost:8090/api/name?name=张三', '{"Content-Type":"application/json"}',
        '{"Content-Type":"application/json"}', 1, 'GET', 2, null, 4, 2, '2025-01-12 10:34:02', '2025-02-20 01:02:56',
        0);
INSERT INTO jsapi.interface_info (id, name, description, url, requestParams, responseParams, returnFormat,
                                  requestExample, requestHeader, responseHeader, status, method, userId, avatarUrl,
                                  totalInvokes, reduceScore, createTime, updateTime, isDeleted)
VALUES (2, '获取毒鸡汤', '获取毒鸡汤', 'http://localhost:8090/api/poisonousChickenSoup', '[
  {
    "fieldName": "无",
    "required": "否",
    "desc": "无",
    "type": "无"
  }
]', null, 'JSON', 'http://localhost:8090/api/poisonousChickenSoup', '{"Content-Type":"application/json"}',
        '{"Content-Type":"application/json"}', 1, 'GET', 2, null, 83, 2, '2025-01-12 23:26:50', '2025-02-20 01:03:59',
        0);
INSERT INTO jsapi.interface_info (id, name, description, url, requestParams, responseParams, returnFormat,
                                  requestExample, requestHeader, responseHeader, status, method, userId, avatarUrl,
                                  totalInvokes, reduceScore, createTime, updateTime, isDeleted)
VALUES (3, '星座运势', '星座运势', 'http://localhost:8090/api/horoscope', '[
  {
    "fieldName": "type",
    "required": "是",
    "desc": "星座名称（aries | taurus | gemini | cancer | leo | virgo | libra | scorpio | sagittarius | capricorn | aquarius | pisces）",
    "type": "String"
  },
  {
    "fieldName": "time",
    "required": "是",
    "desc": "日期（today｜nextday｜week｜month）",
    "type": "String"
  }
]', '[
  {
    "fieldName": "code",
    "desc": "响应状态码",
    "type": "Integer"
  },
  {
    "fieldName": "data.fortune.all",
    "desc": "整体运势评分",
    "type": "Integer"
  },
  {
    "fieldName": "data.fortune.health",
    "desc": "健康运势评分",
    "type": "Integer"
  },
  {
    "fieldName": "data.fortune.love",
    "desc": "爱情运势评分",
    "type": "Integer"
  },
  {
    "fieldName": "data.fortune.money",
    "desc": "财运评分",
    "type": "Integer"
  },
  {
    "fieldName": "data.fortune.work",
    "desc": "工作运势评分",
    "type": "Integer"
  },
  {
    "fieldName": "data.fortunetext.all",
    "desc": "整体运势描述",
    "type": "String"
  },
  {
    "fieldName": "data.fortunetext.health",
    "desc": "健康运势描述",
    "type": "String"
  },
  {
    "fieldName": "data.fortunetext.love",
    "desc": "爱情运势描述",
    "type": "String"
  },
  {
    "fieldName": "data.fortunetext.money",
    "desc": "财运描述",
    "type": "String"
  },
  {
    "fieldName": "data.fortunetext.work",
    "desc": "工作运势描述",
    "type": "String"
  },
  {
    "fieldName": "data.index.all",
    "desc": "整体运势百分比",
    "type": "String"
  },
  {
    "fieldName": "data.index.health",
    "desc": "健康运势百分比",
    "type": "String"
  },
  {
    "fieldName": "data.index.love",
    "desc": "爱情运势百分比",
    "type": "String"
  },
  {
    "fieldName": "data.index.money",
    "desc": "财运百分比",
    "type": "String"
  },
  {
    "fieldName": "data.index.work",
    "desc": "工作运势百分比",
    "type": "String"
  },
  {
    "fieldName": "data.luckycolor",
    "desc": "幸运颜色",
    "type": "String"
  },
  {
    "fieldName": "data.luckyconstellation",
    "desc": "幸运星座",
    "type": "String"
  },
  {
    "fieldName": "data.luckynumber",
    "desc": "幸运数字",
    "type": "Integer"
  },
  {
    "fieldName": "data.name",
    "desc": "星座名称",
    "type": "String"
  },
  {
    "fieldName": "data.shortcomment",
    "desc": "简短评论",
    "type": "String"
  },
  {
    "fieldName": "data.time",
    "desc": "运势日期",
    "type": "String"
  },
  {
    "fieldName": "data.title",
    "desc": "星座名称",
    "type": "String"
  },
  {
    "fieldName": "data.todo.yi",
    "desc": "宜做的事情",
    "type": "String"
  },
  {
    "fieldName": "data.todo.ji",
    "desc": "忌做的事情",
    "type": "String"
  },
  {
    "fieldName": "data.type",
    "desc": "运势类型",
    "type": "String"
  }
]', 'JSON', null, '{"Content-Type":"application/json"}', '{"Content-Type":"application/json"}', 1, 'GET', 2, null, 3, 2,
        '2025-01-13 16:39:54', '2025-02-20 01:02:56', 0);
INSERT INTO jsapi.interface_info (id, name, description, url, requestParams, responseParams, returnFormat,
                                  requestExample, requestHeader, responseHeader, status, method, userId, avatarUrl,
                                  totalInvokes, reduceScore, createTime, updateTime, isDeleted)
VALUES (4, '获取壁纸', '获取壁纸', 'http://localhost:8090/api/randomWallpaper', '[
  {
    "fieldName": "method",
    "required": "否",
    "desc": "输出壁纸端( mobile | pc | zsy )",
    "type": "string"
  },
  {
    "fieldName": "lx",
    "required": "否",
    "desc": "选择输出分类( meizi | dongman | fengjing | suiji)",
    "type": "string"
  }
]', '[
  {
    "fieldName": "code",
    "desc": "响应码",
    "type": "int"
  },
  {
    "fieldName": "message",
    "desc": "响应描述",
    "type": "String"
  },
  {
    "fieldName": "data.imgurl",
    "desc": "图片URL",
    "type": "String"
  },
  {
    "fieldName": "data.with",
    "desc": "图片宽度",
    "type": "String"
  },
  {
    "fieldName": "data.height",
    "desc": "图片高度",
    "type": "String"
  },
  {
    "fieldName": "message",
    "desc": "返回码的信息",
    "type": "String"
  }
]', 'JSON', null, '{"Content-Type":"application/json"}', '{"Content-Type":"application/json"}', 1, 'GET', 2, null, 1, 2,
        '2025-01-13 17:08:28', '2025-02-20 01:03:59', 0);
INSERT INTO jsapi.interface_info (id, name, description, url, requestParams, responseParams, returnFormat,
                                  requestExample, requestHeader, responseHeader, status, method, userId, avatarUrl,
                                  totalInvokes, reduceScore, createTime, updateTime, isDeleted)
VALUES (5, '获取天气', '获取天气', 'http://localhost:8090/api/weather', '[
  {
    "fieldName": "city",
    "desc": "输入城市或县区",
    "type": "string",
    "required": "是"
  },
  {
    "fieldName": "ip",
    "desc": "输入IP",
    "type": "string",
    "required": "是"
  },
  {
    "fieldName": "type",
    "desc": "默认一天，可配置 week 获取周",
    "type": "string",
    "required": "是"
  }
]', '[
  {
    "fieldName": "code",
    "desc": "响应码",
    "type": "int"
  },
  {
    "fieldName": "message",
    "desc": "响应描述",
    "type": "String"
  },
  {
    "fieldName": "data.city",
    "desc": "城市",
    "type": "String"
  },
  {
    "fieldName": "data.info.date",
    "desc": "日期",
    "type": "String"
  },
  {
    "fieldName": "data.info.week",
    "desc": "星期几",
    "type": "String"
  },
  {
    "fieldName": "data.info.type",
    "desc": "天气类型",
    "type": "String"
  },
  {
    "fieldName": "data.info.low",
    "desc": "最低温度",
    "type": "String"
  },
  {
    "fieldName": "data.info.high",
    "desc": "最高温度",
    "type": "String"
  },
  {
    "fieldName": "data.info.fengxiang",
    "desc": "风向",
    "type": "String"
  },
  {
    "fieldName": "data.info.fengli",
    "desc": "风力",
    "type": "String"
  },
  {
    "fieldName": "data.info.night.type",
    "desc": "夜间天气类型",
    "type": "String"
  },
  {
    "fieldName": "data.info.night.fengxiang",
    "desc": "夜间风向",
    "type": "String"
  },
  {
    "fieldName": "data.info.night.fengli",
    "desc": "夜间风力",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.aqi",
    "desc": "空气质量指数",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.aqi_level",
    "desc": "空气质量指数级别",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.aqi_name",
    "desc": "空气质量指数名称",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.co",
    "desc": "一氧化碳浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.no2",
    "desc": "二氧化氮浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.o3",
    "desc": "臭氧浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.pm10",
    "desc": "PM10浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.pm25",
    "desc": "PM2.5浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.so2",
    "desc": "二氧化硫浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.tip",
    "desc": "提示信息",
    "type": "String"
  }
]', 'JSON', null, '{"Content-Type":"application/json"}', '{"Content-Type":"application/json"}', 1, 'GET', 2, null, 1, 0,
        '2025-02-08 13:56:25', '2025-02-20 01:03:59', 0);
INSERT INTO jsapi.interface_info (id, name, description, url, requestParams, responseParams, returnFormat,
                                  requestExample, requestHeader, responseHeader, status, method, userId, avatarUrl,
                                  totalInvokes, reduceScore, createTime, updateTime, isDeleted)
VALUES (6, '获取天气', '获取天气', 'http://localhost:8090/api/weatherSoJson', '[
  {
    "fieldName": "cityCode",
    "required": "是",
    "desc": "城市代码",
    "type": "String"
  }
]', '[
  {
    "fieldName": "code",
    "desc": "响应码",
    "type": "int"
  },
  {
    "fieldName": "message",
    "desc": "响应描述",
    "type": "String"
  },
  {
    "fieldName": "data.city",
    "desc": "城市",
    "type": "String"
  },
  {
    "fieldName": "data.info.date",
    "desc": "日期",
    "type": "String"
  },
  {
    "fieldName": "data.info.week",
    "desc": "星期几",
    "type": "String"
  },
  {
    "fieldName": "data.info.type",
    "desc": "天气类型",
    "type": "String"
  },
  {
    "fieldName": "data.info.low",
    "desc": "最低温度",
    "type": "String"
  },
  {
    "fieldName": "data.info.high",
    "desc": "最高温度",
    "type": "String"
  },
  {
    "fieldName": "data.info.fengxiang",
    "desc": "风向",
    "type": "String"
  },
  {
    "fieldName": "data.info.fengli",
    "desc": "风力",
    "type": "String"
  },
  {
    "fieldName": "data.info.night.type",
    "desc": "夜间天气类型",
    "type": "String"
  },
  {
    "fieldName": "data.info.night.fengxiang",
    "desc": "夜间风向",
    "type": "String"
  },
  {
    "fieldName": "data.info.night.fengli",
    "desc": "夜间风力",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.aqi",
    "desc": "空气质量指数",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.aqi_level",
    "desc": "空气质量指数级别",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.aqi_name",
    "desc": "空气质量指数名称",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.co",
    "desc": "一氧化碳浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.no2",
    "desc": "二氧化氮浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.o3",
    "desc": "臭氧浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.pm10",
    "desc": "PM10浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.pm25",
    "desc": "PM2.5浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.air.so2",
    "desc": "二氧化硫浓度",
    "type": "String"
  },
  {
    "fieldName": "data.info.tip",
    "desc": "提示信息",
    "type": "String"
  }
]', 'JSON', 'http://localhost:8090/api/weatherSoJson/101280101', '{"Content-Type":"application/json"}',
        '{"Content-Type":"application/json"}', 1, 'GET', 2, null, 1, 2, '2025-02-05 15:33:21', '2025-02-20 01:03:59',
        0);
INSERT INTO jsapi.interface_info (id, name, description, url, requestParams, responseParams, returnFormat,
                                  requestExample, requestHeader, responseHeader, status, method, userId, avatarUrl,
                                  totalInvokes, reduceScore, createTime, updateTime, isDeleted)
VALUES (7, '获取情话', '获取情话', 'https://localhost:8090/api/loversPrattle', '[
  {
    "fieldName": "无",
    "required": "否",
    "desc": "无",
    "type": "无"
  }
]', null, 'JSON', null, '{"Content-Type":"application/json"}', '{"Content-Type":"application/json"}', 1, 'GET', 2, null,
        1, 0, '2025-02-08 13:56:25', '2025-02-20 01:03:59', 0);
