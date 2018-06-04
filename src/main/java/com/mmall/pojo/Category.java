package com.mmall.pojo;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class Category {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;


    //要用set集合时，最好equals和hsahCode都重写，保证判断因子一样
//两个对象equals为true,hashCode相同，并不一定相同
    //hashCode只取了id,equals还比较了其他

}