create table if not exists alarm_Info
(
    id          bigint auto_increment comment 'id' primary key,
    video_id     bigint                             null comment '报警信息相关的视频id',
    device_id    varchar(128)                       null comment '报警信息相关的摄像头id',
    channel_id   varchar(128)                       null comment '报警信息相关的摄像头通道',
    alarm_time   varchar(128)                       null comment '报警的时间',
    location    varchar(256)                       null comment '报警地点',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint  default 0                 not null comment '是否删除'
    ) comment '报警信息' collate = utf8mb4_unicode_ci;

create table if not exists alarm_Video
(
    id          bigint auto_increment comment 'id' primary key,
    stream      varchar(256)                       null comment '报警视频流',
    start_time   varchar(256)                       null comment '报警视频开始时间',
    end_time     varchar(256)                       null comment '报警视频结束时间',
    file_name    varchar(256)                       null comment '报警视频的文件名',
    file_path    varchar(256)                       null comment '报警视频的全路径名',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint  default 0                 not null comment '是否删除'
    ) comment '报警视频' collate = utf8mb4_unicode_ci;