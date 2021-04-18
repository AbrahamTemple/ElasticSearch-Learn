# ES增删改查

## 创建索引

> 创建一个指定类型并且带数据的索引

![Screenshot](ES-CRUD/res1.png)

> 创建一个空索引，并指定各个字段的类型

![Screenshot](ES-CRUD/res2.png)

> 得到索引库信息

``` Bash
GET test2
```

![Screenshot](ES-CRUD/res35.png)

> 创建一个默认类型并且带数据的索引

![Screenshot](ES-CRUD/res3.png)

> 查看该索引库信息

![Screenshot](ES-CRUD/res4.png)

> 查看所有库信息

``` Bash
GET _cat/indices?v
```

![Screenshot](ES-CRUD/res5.png)

## 修改索引

### 覆盖型修改

![Screenshot](ES-CRUD/res6.png)

![Screenshot](ES-CRUD/res7.png)

### 指定型修改

![Screenshot](ES-CRUD/res8.png)

![Screenshot](ES-CRUD/res9.png)

## 删除索引

``` Bash
DELETE test1
```

![Screenshot](ES-CRUD/res10.png)

## 花式查询

### 以文档的名字简单查询

``` Bash
GET test3/_doc/4
```

![Screenshot](ES-CRUD/res11.png)

### 绑定权重与关键词有关的索引全查询

``` Bash
GET test3/_doc/search?q=address:路
```

![Screenshot](ES-CRUD/res12.png)

``` Bash
GET test3/_doc/search?q=address:三环路
```

![Screenshot](ES-CRUD/res13.png)

### 条件的匹配

> match是使用了分词器

> 分词在数组里面都有效

![Screenshot](ES-CRUD/res14.png)

![Screenshot](ES-CRUD/res15.png)

> 结果的过滤

![Screenshot](ES-CRUD/res16.png)

![Screenshot](ES-CRUD/res17.png)

> 降序排序

![Screenshot](ES-CRUD/res18.png)

![Screenshot](ES-CRUD/res19.png)

> 分页查询

![Screenshot](ES-CRUD/res20.png)

> 必须包含查询

![Screenshot](ES-CRUD/res21.png)

![Screenshot](ES-CRUD/res22.png)

> 只要满足指定匹配的全查询

![Screenshot](ES-CRUD/res23.png)

![Screenshot](ES-CRUD/res24.png)

> 必须不包含查询

![Screenshot](ES-CRUD/res25.png)

![Screenshot](ES-CRUD/res26.png)

> 过滤器

![Screenshot](ES-CRUD/res27.png)

![Screenshot](ES-CRUD/res28.png)

> 不分词查询

![Screenshot](ES-CRUD/res29.png)

![Screenshot](ES-CRUD/res30.png)

> 高亮查询

![Screenshot](ES-CRUD/res31.png)

![Screenshot](ES-CRUD/res32.png)

> 自定义高亮查询

![Screenshot](ES-CRUD/res33.png)

![Screenshot](ES-CRUD/res34.png)



