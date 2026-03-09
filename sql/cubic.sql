create table interface_info
(
    id             bigint auto_increment comment '主键'
        primary key,
    name           varchar(256)                           not null comment '名称',
    description    varchar(256)                           null comment '描述',
    url            varchar(512)                           not null comment '接口地址',
    requestParams  text                                   null comment '请求参数',
    responseParams text                                   null comment '响应参数',
    returnFormat   varchar(512) default 'JSON'            null comment '返回格式(JSON等等)',
    requestExample text                                   null comment '请求示例',
    requestHeader  text                                   null comment '请求头',
    responseHeader text                                   null comment '响应头',
    status         int          default 1                 not null comment '接口状态 （0-关闭， 1-开启）',
    method         varchar(256)                           not null comment '请求类型',
    userId         bigint                                 not null comment '创建人',
    avatarUrl      varchar(1024)                          null comment '接口头像',
    totalInvokes   bigint       default 0                 not null comment '接口总调用次数',
    reduceScore    bigint       default 0                 not null comment '扣除积分数',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDeleted      tinyint      default 0                 not null comment '是否删除(0-未删, 1-已删)'
)
    comment '接口信息';

create table monthly_sign_records
(
    id         bigint auto_increment comment '主键Id'
        primary key,
    userId     bigint                             not null comment '用户Id',
    signMonth  varchar(10)                        not null comment '签到年月（yyyy-MM）',
    signStatus bit(31)                            not null comment '该月每天的签到情况（位图）',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint userId
        unique (userId, signMonth)
)
    comment '签到表';

create table user
(
    id             bigint auto_increment comment 'id'
        primary key,
    userName       varchar(256)                           null comment '用户昵称',
    userAccount    varchar(256)                           not null comment '账号',
    userAvatar     varchar(1024)                          null comment '用户头像',
    email          varchar(256)                           null comment '邮箱',
    gender         varchar(10)                            null comment '性别(0-男 1-女)',
    userRole       varchar(256) default 'user'            not null comment '用户角色：user / admin',
    userPassword   varchar(512)                           not null comment '密码',
    accessKey      varchar(512)                           not null comment 'accessKey',
    secretKey      varchar(512)                           not null comment 'secretKey',
    status         tinyint      default 0                 not null comment '帐号状态（0-正常 1-封号）',
    balance        bigint       default 30                not null comment '钱包余额,注册送30币',
    invitationCode varchar(256)                           null comment '邀请码',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDeleted      tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount
        unique (userAccount)
)
    comment '用户';

create table user_interface_info
(
    id              bigint auto_increment comment '主键'
        primary key,
    userId          bigint                             not null comment '调用用户 Id',
    interfaceInfoId bigint                             not null comment '接口 Id',
    totalNum        int      default 0                 not null comment '总调用次数',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDeleted       tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
)
    comment '用户调用接口关系';


