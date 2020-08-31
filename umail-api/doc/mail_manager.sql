use mail_manager;
-- 用户表
create table user(
  id varchar(36)  primary key,
  name varchar(11) comment '用户名',
  email varchar(36) comment '邮箱',
  secret varchar(36)  comment '密码',
  verify varchar(6) comment '验证码',
  time varchar(14) comment '更新时间'
);
-- 账户表
create table account
(
  account      varchar(36) null
  comment '账户名',
  password     varchar(36) null
  comment '密码',
  recProtocol  varchar(8)  null
  comment '收件服务器协议',
  recHost      varchar(36) null
  comment '收件服务器主机',
  recPort      varchar(4)  null
  comment '收件服务器端口',
  recIsSSL     varchar(8)  null
  comment '是否使用ssl协议',
  sendProtocol varchar(8)  null
  comment '发件服务器协议',
  sendHost     varchar(36) null
  comment '发件服务器主机',
  sendPort     varchar(4)  null
  comment '发件服务器端口',
  sendIsSSL    varchar(8)  null
  comment '是否使用ssl协议',
  alias varchar(36) null
  comment '账户别名',
  isScheduled varchar(8) null
  comment '是否定时收取',
  scheduledPeriod varchar(8) null
  comment '定时周期',
  userId       varchar(36) null
);
-- foreign key (userId) references user (id)
-- 签名表
create table sign
(
  name varchar(36) comment '名字',
  content varchar(1024)  comment '内容',
  userId       varchar(36) null

);
-- foreign key (userId) references user (id)
-- 账户签名表
create table account_sign
(
  account varchar(36)  comment '账户名',
  sendsign varchar(36) comment '签名',
  replysign varchar(36) comment '签名'
);
-- 联系人表
create table contact
(
  name varchar(36) comment '名字',
  email varchar(36)  comment '电子邮箱',
  movePhone    varchar(16) comment '移动电话',
  company       varchar(512) comment '公司',
  workPhone varchar(16) comment '工作电话',
  remark varchar(512) comment '备注',
  userId       varchar(36) null
);
commit;