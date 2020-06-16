

## Redis总结	

redis获取/设置密码

config get requirepass | config get repass

config set requirepass "new password"

### string

**set** key value

**get** key

**del** key

**mset** key1 value1 key2 value2 ...

**mget** key1 key2 ...

获取数据字符个数（字符串长度） 

**strlen** key

追加信息到原始信息后部（如果原始信息存在就加，否则新建）

**append** key value



#### 设置数值数据增加指定范围的值

**incr** key

**incrby** key increment

**incrbyfloat** key increment



#### 设置数值数据减少制定范围的值

**decr** key

**decrby** key increment

**按数值进行操作的数据，如果原始数据不能转换为数值，或则超过了Redis数值范围上限，将报错。**



#### 设置数据具有指定的生命周期

**setex** key seconds value

**psetex** key milliseconds value

Redis控制数据的生命周期，通过数据是否失效控制业务行为，适用于所有具有时效性限定控制的操作



常用于存储的格式：

**表名:主键名:主键值 value**





### hash

**hset** key field value

**hget** key field

**hgetall** key

**hdel** key field

hlen key  查看hash有几个field



**hmset** key field1 value1, field2 value2, ......

**hmget** key field1, field2, .......



**hkeys** key

**hvals** key



**hincrby** key field increment

**hincrbyfloat** key field increment

hash类型下的value只能存储字符串，不能再嵌套存储其他类型的数据

每个hash可以存储2^32 - 1个键值对

hash的设计初衷不是为了存储对象的，因此不可滥用

hgetall操作可以获取全部属性，如果内部field过多，效率很低



string存储对象（json）与hash存储对象，string在于存储，hash在于修改



**购物车实现**

以用户ID作为key，商品编号作为field，商品数量作为value

当前仅仅是将数据存储到了Redis中，宾没有起到加速的作用，商品信息还需要二次查询数据库

- 每条购物车中的商品记录保存成两条field

- field1专用于保存购买数量

  命名格式：商品ID：nums

  保存数据：数值

- field2专用于保存购物车中显示的信息，包含文字描述，图片地址，所属商家信息等

  命名格式：商品ID：info

  保存数据：json 

  hmset 001 g01:nums 2 g01:info {......}

商品信息可以独立hash，这样可以大大减少重复的信息

**hsetnx** key field value



商家应用

- 以商家ID作为key
- 将参与抢购的商品ID作为field
- 将参与抢购的商品数量作为对应的value
- 抢购时使用降值的方式控制产品的数量

<u>Redis通常只做数据的提供和保存，尽量不要把业务压到Redis上</u>

Tips5：

Redis应用于抢购，限购类、限量发放优惠卷、激活码等业务的数据存储设计



### list

添加/修改

**lpush** key value1 [value2]....

**rpush** key value1 [value2]....

获取数据

**lrange** key start stop    (0, -1显示所有数据)

**lindex** key index

**llen** key

获取并移除数据

**lpop** key

**rpop** key



规定时间内获取并移除数据

**blpop** key1 [key2] timeout

**brpop** key1 [key2] timeout



朋友圈点赞，按照点赞顺序现实好友的点赞信息

如果取消点赞，移除对应好友信息

- 移除指定数据

> **lrem** key count value （value为从左边开始删除指定个数的value）



Tips6:

<u>redis应用于具有操作先后顺序的数据控制</u>



list中保存的数据都是string类型的，数据总量是有限的

list中最多保存2^32  - 个元素

list具有索引的概念，但是操作数据时通常以队列的形式进行入队操作，或以栈的形式进行入栈出栈操作

获取全部数据操作结束索引设置为-1

list可以对数据进行分页操作，通常第1页的信息来自于list，第2页及更多的信息通过数据库的形式加载



应用场景

微博/twitter关注列表/粉丝列表，先关注的在前面；

新闻、咨询类网站如何将新闻或咨询按照发生的时间顺序展示；

企业运营过程中，系统将产生大量的运营数据，如何保障多台服务器操作日志的统一顺序输出？（每个服务器都往Redis中写）

**解决方案**

- 依赖list的数据具有顺序的特征对信息进行管理
- 使用队列模型解决多路信息汇总合并的问题
- 使用栈模型解决最新消息的问题

 



### set

添加数据

**sadd** key member1 [member2]

获取全部数据

**smembers** key

删除数据

**srem** key member1 [member2]

获取集合数据总量

**scard** key

判断集合中是否包含指定数据

**sismember** key member

#### Set类型数据的扩展操作

随机获取集合中指定数量的数据

**srandmember** key [count]

随机获取集合中的某个数据并将该数据移除集合

**spop** key



求两个集合的交、并、差集

**sinter** key1 [key2]

**sunion** key1 [key2]

**sdiff** key1 [key2]



求两个集合的交、并、差集并存储到指定集合中

**sinterstore** destination key1 [key2]

**sunionstore** destination key1 [key2]

**sdiffstore** destination key1 [key2]



将指定数据从原始集合中移动到目标集合中

**smove** source destination member



### sorted_set

在set的存储结构基础上添加可排序字段

添加数据

**zadd** key score1 member1 [score2 member2]

获取全部数据

**zrange** key start stop [WITHSCORES]           (0  -1现实所有的数据)

**zrevrange** key start stop [WITHSCORES]

删除数据

**zrem** key member [member ... ]

按条件获取数据

**zrangebyscore** key min max [WITHSCORES] [LIMIT]

**zrevrangebyscore** key max min [WITHSCORES]

条件删除数据

**zremrangebyrank** key start stop

**zremrangebyscore** key min max



获取集合数据总量

**zcard** key

**zcount** key min max

集合交、并操作

**zinterstore** destination numkeys key [key ... ]  [AGGREGATE SUM|MIN|MAX]

**zunionstore** destination numkeys key [key ... ]



获取珊瑚橘对应的索引（排名）

**zrank** key member

**zrevrank** key member



socre值获取与修改

**zscore** key member

**zincrby** key increment member



score保存的数据存储空间是64位

score保存的数据也可以是一个双精度的double值，基于双精度浮点数的特征，可能会丢失精度，使用时要慎重；

sorted_set底层存储还是基于set结构的，因此数据不能重复，如果重复添加相同的数据，score值将被反复修改，保留最后一次修改的结果



获取当前系统时间

**time**



### 通用命令

key是一个字符串



针对key的操作

del key

exists key

type key      获取key的类型



为指定key设置有效期

**expire** key seconds

**pexpire** key milliseconds

**expireat** key timestamp

**pexpireat** key milliseconds-timestamp

获取key的有效时间

**ttl** key

**pttl** key

切换key从时效性转换为永久性

**persist** key



查询key

**keys** pattern

查询模式规则

```
* 匹配任意数量的任意符号      ？匹配一个人一符号
keys *        所有
keys it*      查询所有以it开头
keys *heima   查询所有以heima结尾
keys ??heima  查询所有前面两个字符人一，后面以heima结尾
keys user:?   查询所有以user:开头，最后一个字符任意
keys u[st]er:1 查询所有以u开头，以er:1结尾，中间包含一个字母，s或t
```

为key改名

**rename** key newkey

**renamenx** key newkey



对所有key排序

**sort**



其他key通用操作

**help @generic**



### 数据库通用操作

切换数据库

**select** index

其他操作

**quit**   退出

**ping**   检查服务器是否能联通，不写key的话能联通则返回PONG      

**echo** message



数据移动

**move** key db

数据清除操作

**dbsize**

**flushdb**

**flushall**



### Jedis

连接吃



### 持久化

RDB持久乎方式

**save**  指令

每执行一次就生成一个.rdb文件

配置文件

dbfilename dump.rdb

​		说明：设置本地数据库文件名，默认值为dump.rdb

​		经验：通常设置为dump-端口号.rdb

dir

​		说明：设置存储.rdb文件的路径

​		经验：通常设置成存储空间较大的目录中，名称为data

rdbcompression yes

​		说明：设置存储至本地数据库时是否压缩数据，默认为yes，采用LZF压缩

​		经验：通常默认为开启，如果设置为no, 可以节省CPU运行时间，但是会使得存储的文件变大（巨大）

rdbchecksum yes

​		说明：设置是否进行RDB文件格式校验，该校验过程在写文件和读文件过程均进行

​		经验：通常默认为开启状态，如果设置为no，可以节约读写过程约10%的时间消耗，但是存储有一定的数据损坏风险



**bgsave**   后台执行保存		

![image-20200615172424557](/Users/didi/Library/Application Support/typora-user-images/image-20200615172424557.png)

返回的消息在日志文件中



**配置**

**save** second changes (second秒内有changes个被修改过则执行一次bbgsave指令)

在conf文件中进行配置

范例：

save 900 1

save 300 10

save 60 1000

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200616103559473.png" alt="image-20200616103559473" style="zoom: 33%;" />

这3种情况都视为改变了key

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200616103800860.png" alt="image-20200616103800860" style="zoom:33%;" />



RDB特俗启动形式

全量复制

​		主从复制种用到

服务器运行过程中启动

​		**debug** reload

关闭服务器时指定保存数据

​		**shutdown** save



**RDB优点**

- RDB是一个紧凑的二进制文件，存储效率高
- RDB内部存储的是Redis在某个**<u>时间点</u>**的数据快照，非常适合数据备份，全量复制等场景
- RDB恢复数据的速度要比AOF快很多
- 应用：服务器中每X小时执行bgsave备份，并将RDB文件拷贝到远程及其中，用于灾难恢复

**RDB缺点**

- RDB方式无论是执行指令还是利用配置，无法做到实时持久化，具有较大的可能性丢失数据
- bgsave指令每次执行fork操作创建子进程，要牺牲一些性能
- Redis的众多版本中未进行RDB文件格式的版本统一，有可能出夏安各版本服务之间数据格式无法兼容的现象



### AOF

AOF(Append only file)持久化：以独立日志的方式记录每次写命令，重启时再重新执行AOF文件中命令以达到恢复数据的目的。与RDB相比可以简单描述为**<u>改记录数据为记录数据产生的过程</u>**。

AOF的主要作用是解决了数据持久化的实时性，目前已经是Redis持久话的主流方式



**优先用AOF**



<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200616105340292.png" alt="image-20200616105340292" style="zoom:33%;" />

**AOF写数据三种策略（appendfsync）**

- always(每次)

	 	每次写入操作均同步到AOF文件中，**数据零误差**，**性能较低**，不建议使用

- everysec(每秒)

	 	每秒将缓存区中的指令同步到AOF文件中，数据准确性较高，性能较高，建议使用，也是默认配置

	​	 在系统突然宕机的情况下丢失1秒数据

- no(系统性能)

	​	 由操作系统控制每次同步到AOF文件的周期，整体过程不可控

**AOF功能开启**

配置

**appendonly** yes|no

作用

​		是否开启AOF持久化功能，默认为不开启状态

配置

**appendfsync** always|everysec|no

作用

​		AOF写数据策略

**AOF相关配置**

- 配置

	**appendfilename** filename

- 作用

	AOF持久化文件名，默认文件名为appendonly.aof，建议配置为appendonly-端口号.aof

- 配置

	**dir**

- 作用

	AOF持久化文件保存路径，与RDB持久化文件保持一致即可



#### AOF重写

AOF重写机制压缩。就是将对同一个数据的若干条命令执行结果转化成最终结果数据对应的指令进行记录

- 降低磁盘占用量，提高磁盘利用率；
- 提高持久化效率，降低持久化写时间，提高IO性能；
- 降低数据恢复用时，提高数据恢复效率



#### AOF重写规则

- 进程内已超时的数据不再写入文件

- 忽略无效指令，重写时使用进程内数据直接生成，这样新的AOF文件只保留最终数据的写入命令

	如del key1、hdel key2、srem key3、set key4 111、set key4 222等

- 对同一数据的多条写命令合并为一条命令

	如lpush list1 a、lpush list1 b、lpush list1 c可以转化为：lpush list1 a, b, c

	为了防止数据量过大造成客户端缓冲区溢出，对list、set、hash、zset等类型，每条指令最多写入64个元素

配置文件

**手动重写**

**bgrewriteaof**

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200616183442853.png" alt="image-20200616183442853" style="zoom:33%;" />

**自动重写触发条件设置**

**auto-aof-rewrite-min-size** size

**auto-aof-rewrite-percentage** percentage

**自动重写触发比对参数（运行指令info Persistence获取具体信息）**

**aof_current_size**

**aof_base_size**

**自动重写触发条件**

**aof_current_size>aauto-aof-rewrite-min-size**

**aof_current_size - aof_base_size / aof_baase_size >= auto-aof-rewrite-percentage**

![image-20200616184333973](/Users/didi/Library/Application Support/typora-user-images/image-20200616184333973.png)

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200616184457268.png" alt="image-20200616184457268" style="zoom:33%;" />

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200616184615523.png" alt="image-20200616184615523" style="zoom:33%;" />



























































