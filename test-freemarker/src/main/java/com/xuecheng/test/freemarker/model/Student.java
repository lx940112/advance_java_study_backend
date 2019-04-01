package com.xuecheng.test.freemarker.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class Student {
    /**
     * 姓名
     */
    private String name;
    /**
     * 年龄
     */
    private int age;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 钱包
     */
    private Float money;
    /**
     * 朋友列表
     */
    private List<Student> friends;
    /**
     * 最好的朋友
     */
    private Student bestFriend;
}
