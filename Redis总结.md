****

Redis总结	





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

#### RDB持久化方式

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



#### AOF

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



### Redis事务

#### 事务的基本操作

- 开启事物

	**multi**

- 作用

	设定事务的开启位置，此命令执行后，后续的所有指令均加入到事务中

- 执行事务

	**exec**

- 作用

	设定事务的结束位置，同时执行事务。与multi成对出现，成对使用

**注意：**加入事务的命令暂时进入到任务队列中，并没有立即执行，只有执行exec命令才开始执行

- 取消事务

	**discard**

- 作用

	终止当前事务的定义，发生在multi之后，exec之前

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200617010038984.png" alt="image-20200617010038984" style="zoom:33%;" />

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200617010114646.png" alt="image-20200617010114646" style="zoom:33%;" />



#### 事务的注意事项

**定义事务的过程中，命令格式输入错误怎么办？**

- 语法错误

	指命令书写格式错误

- 处理结果

	如果定义的事务中所包含的命令存在语法错误，整体事务中所有命令均不会执行。包括那些语法正确的指令

**定义事务的过程中，命令执行出现错误怎么办？**

- 运行错误

	指命令格式正确，但是无法啊正确的执行命令。例如对list执行incr操作

- 处理结果

	能够正确运行的命令会执行，运行错误的命令不会被执行

**注意：**已经执行完毕的命令对应的数据不会自动回滚，需要程序员自己在代码中实现回滚



#### 手动进行事务回滚

- 记录操作过程中被影响的数据之前的状态
	- 单数据：string
	- 多数据：hash、list、set、zset
- 设置指令恢复所有的被修改的项
	- 单数据：直接set（注意周边属性，例如时效）
	- 多数据：修改对应值或整体克隆复制



#### 基于特定条件的事务执行——锁

解决方案

- 对key添加监视锁，在执行exec前如果key发生了变化，终止事务执行

	**watch** key1 [key2 ......]

- 取消对所有key的监视

	**unwatch**

在开启事务之前执行watch，指定要监控的key。（每次在开启事务之前都要执行watch）

当事务还没有执行时，其他的操作修改了key的值，则该事务将不能被执行



#### 分布式锁

使用setnx设置一个公共锁

**setnx** lock-key value

利用setnx命令的返回值特征，有值则返回设置失败，无值则返回成功

- 对于返回设置成功的，拥有控制权，进行下一步的具体业务操作；
- 对于返回设置失败的，不就有控制权，排队或等待

操作完毕通过del操作释放锁



### Redis删除策略

- 定时删除 （消耗CPU资源，当定时器计数完就将该数据删除）
- 惰性删除 （计时器到点后不立马删除，而是下一次再使用的时候删除，相当于以空间换时间）
- 定期删除 （相当于前两个方案的折衷，以一定的周期执行啥暗处操作，即每秒执行删除操作的CPU占用率有一个上限，在具体执行删除操作中又会采用一定的策略先选取M个key看过期率然后再执行下一个数据库的删除还是采取当前数据库的全库扫描）

![image-20200619101712550](/Users/didi/Library/Application Support/typora-user-images/image-20200619101712550.png)

### 逐出算法

新数据进入检测

**当新数据进入redis时，如果内存不足怎么办？**

Redis使用内存存储数据，在执行每一个命令前，会调用**freeMemoryIfNeeded()**检测内存是否充足。如果内存不满足新加入数据的最低内存需求，redis要临时删除一些数据为当前指令清理存储空间。清理数据策略称为逐出算法。

<u>删除策略</u>针对的是有**expire**的数据，<u>逐出算法</u>是加入新数据时内存不够了，从已有的这些数据中选择一些删除掉以腾出足够的空间容纳新数据。



- 检测易失数据（可能会过期的数据集server.db[i].expires）
	1. volatile-lru: 挑选最近最少使用的数据淘汰
	2. volatile-lfu: 挑选最近使用次数最少的数据淘汰
	3. volatile-ttl: 挑选将要过期的数据淘汰
	4. volatile-random: 任意选择数据淘汰

LRU: Least Recently Used

LFU: Least Frequently Used

- 检测全库数据（所有数据集server.db[i].dict）
	1. allkeys-lru: 挑选最近最少使用的数据淘汰
	2. allkeys-lfu: 挑选最近使用次数最少的数据淘汰
	3. allkeys-random: 任意选择数据淘汰
- 放弃数据驱逐
	1. no-enviction（驱逐）：禁止驱逐数据（redis默认策略），会引发OOM（Out Of Memory）

**配置**

配置文件中：

**maxmemory**-policy volatile-lru

使用INFO命令输出监控信息，查询缓存hit和miss的次数，根据业务需求调优Redis配置



### 服务器基础设置

#### 服务端设置

- 设置服务器以守护进程的方式运行

	**daemonize** yes|no

- 绑定主机地址

	**bind** 127.0.0.1    （绑定后就只能用该IP去访问）

- 设置服务器端口号

	**port** 6379

- 设置数据库数量

	**databases** 16

- 设置服务器以指定日志记录级别

	**loglevel** debug|verbose|notice|warning   （默认verbose）

- 日志记录文件名

	**logfile** 端口号.log   (推荐)

#### 客户端配置

- 设置同一时间最大客户端连接数，默认无限制。当客户端连接数到达上限，Redis会关闭新的连接

	**maxclients** 0

- 客户端设置等待最大时长，达到最大值后关闭连接。如需关闭该功能，设置为0

	**timeout** 300

#### 多服务器快捷配置

- 导入并加载指定配置文件信息，用于快速创建redis公共配置较多的redis实力配置文件，便于维护

**include** /path/server-端口号.conf



### 高级数据类型

#### Bitmaps

- 获取指定key对应偏移量上的bit值

	**getbit** key offset    (不存在时返回的是一个0)

- 设置指定key对应偏移量上的bit值，<u>**value只能是1或0**</u>

	**setbit** key offset value

扩展操作

- 对指定key按位进行交、并、非、异或操作，并将结果保存到destKey中

	**bitop** op destKey key1 [key2 ......]

	- and: 交
	- or: 或
	- not: 非
	- xor: 异或

- 统计指定key中1的数量

	**bitcount** key [start end]    (不给范围就是全统计)

Redis用于信息的状态的统计



#### HyperLogLog

统计不重复的数据

统计独立UV

- 原始方案：set
	- 存储每个用户的id(字符串)
- 改进方案：Bitmaps
	- 存储每个用户状态（bit）
- 全新的方案：HyperLogLog



**基数**

- 基数是数据集去重后元素个数
- HyperLogLog是用来做基数统计的，运用了LogLog算法

{1, 3, 5, 7, 5, 7, 8}.   基数集：{1, 3, 5, 7, 8}.    基数：5

{1, 1, 1, 1, 1, 7, 1}.   基数集：{1, 7}    基数：2

LogLog算法



- 添加数据

	**pfadd** key element [element ...]

- 统计数据

	**pfcount** key [key ......]

- 合并数据

	**pfmerge** destKey sourceKey [sourceKey]

**相关说明**

- 用于进行基数统计，不是集合，不保存数据，只记录数量而不是具体数据
- 核心是基数估算算法，最终数值存在一定误差
- 误差范围：基数估计的结果是一个带有0.81%标准错误的近似值
- 耗空间极小，每个hyperloglog key占用了12K的内存用于标记基数
- pfadd命令不是一次性分配12K内存使用，会随着基数的增加内存逐渐增大
- pfmerge命令合并后占用的存储空间为12K，无论合并之前数据量是多少



#### GEO

针对于地球经纬度的操作

- 添加坐标点

	**geoadd** key longitude latitude member [longitude latitude member ...]

	(将若干个坐标放到一个容器中，然后在这个容器中做相关操作)

- 获取坐标点

	**geopos** key member [member ...]

- 计算坐标点距离

	**geodist** key member1 member2 [unit]

	

- 根据坐标求范围内的数据

	**georadius** key longitude latitude radius m|km|ft|mi [withcoord] [withdist] [withhash] [count count]

- 根据点求范围内数据

	**georadiusbymember** key member radius m|km|ft|mi [withcoord] [withdist] [withhash] [count count]

- 获取指定点对应的坐标hash值

	**geohash** key member [member .....]

	

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200619113336711.png" alt="image-20200619113336711" style="zoom:33%;" />



<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200619113537299.png" alt="image-20200619113537299" style="zoom:33%;" />





### 主从复制

- 高并发
- 高性能
- 高可用

可用性 = (全年时间 - 服务器不可用的时间) / 全年时间 * 100%

业界可用性目标5个9，即99.999%，即服务器年宕机时长低于315秒，约5.25分



主从复制即将master中的数据即时/有效的复制到slave中

职责：

- master:
	- 写数据
	- 执行写操作时，将出现变化的数据自动同步到slave
	- 读数据（可忽略）
- slave:
	- 读数据
	- 写数据（禁止)



#### 高可用集群

主从复制的作用

- 读写分离：master写、slave读，提高服务器的读写负载能力
- 负载均衡：基于主从结构，配合读写分离，由slave分担master负载，并根据需求的变化，改变slave的数量，通过多个从节点分担数据读取负载，大大提高Redis服务器并发量与数据吞吐量
- 故障恢复：当master出现问题时，由slave提供服务，实现快速的故障恢复
- 数据冗余：实现数据热备份，是持久化之外的一种数据冗余方式
- 高可用基石：基于主从复制，构建哨兵模式与集群，实现Redis的高可用方案



#### 主从复制工作流程

主从复制大体可以分成3个阶段

- 建立连接阶段（即准备阶段）
- 数据同步阶段
- 命令椽笔阶段

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200619115650244.png" alt="image-20200619115650244" style="zoom: 50%;" />

阶段一：建立连接阶段

- 建立slave到master的连接，使master能够识别slave，并保存slave端口号

![image-20200619120145673](/Users/didi/Library/Application Support/typora-user-images/image-20200619120145673.png)



#### 主从连接（slave到master）

- 方式一：客户端发送命令

	**slaveof** <masterip> <masterport>

- 方式二：启动服务器参数

	redis-server **--slaveof** <masterip> <masterport>

- 方式三：服务器配置

	**slaveof** <masterip> <masterport>

主从断开连接

- 客户端发送命令

	**slaveof** no one   (从服务器发送断开命令)



#### 授权访问

- master配置文件设置密码

	**requirepass** <password>

- master客户端发送命令设置密码

	**config** **set** requirepass <password>

	**config** **get** requirepass



- slave客户端发送命令设置密码

	**auth** <password>

- slave配置文件设置密码

	**masterauth** <password>

- 启动客户端设置密码

	**redis-cli** -a <password>



#### 数据同步阶段

![image-20200621162006047](/Users/didi/Library/Application Support/typora-user-images/image-20200621162006047.png)

第7步发送的是缓冲区里面的指令



#### 数据同步阶段master说明

1. 如果master数据量巨大，数据同步阶段应避开流量高峰期，避免造成master阻塞，影响业务正常执行；

2. 复制缓冲区大小设定不合理，会导致数据溢出。如进行全量复制周期太长，进行部分复制时发现数据已经存在丢失的情况，必须进行第二次全量复制，致使slave陷入死循环状态。

	<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200621162543723.png" alt="image-20200621162543723" style="zoom: 50%;" />

**repl-backlog-siz**e 1mb

3. master单机内存占用主机内存的比例不应该过大，建议使用50% — 70%的内存，留下的30% — 50%的内存用于执行bgsave命令和创建复制缓冲区



#### 数据同步阶段slave说明

1. 为避免slave进行全量复制/部分复制时服务器响应阻塞或数据不同步，建议关闭此期间的对外服务

	**slave-serve-stale-data** yes|no

2. 数据同步阶段，master发送给slave信息可以理解为master是slave的一个客户端，主动向slave发送命令
3. 多个slave同时对master请求数据同步，master发送的RDB文件增多，会对贷款造成巨大冲击，如果master贷款不足，因此数据同步需要根据业务需求，适量错峰
4. slave过多时，建议调整拓扑结构，由一主多从结构变为树状结构，中间的节点既是master，也是slave。注意使用树状结构时，由于层级深度，导致深度越高的slave与最顶层master间数据同步延迟较大，数据一致性变差，应谨慎选择。



#### 阶段三：命令传播阶段

- 当master数据库状态被修改后，导致主从服务器数据库状态不一致，此时需要让主从数据同步到一致的状态，同步的动作称为命令传播阶段。
- master将接收到的数据变更命令发送给slave，slave接受命令后执行命令



#### 命令传播阶段的部分复制

- 命令传播阶段出现了断网现象
	- 网络闪断闪连				忽略
	- 短时间网络中断            部分复制
	- 长时间网络中断            全量复制



- 部分复制的三个核心要素
	- 服务器的运行id（run id）
	- 主服务器的复制积压缓冲区
	- 主从服务器的复制偏移量



#### 服务器运行ID（run_id）

- 概念：服务器运行ID是每一台服务器每次运行的身份识别码，一台服务器多次运行可以生成多个运行ID

- 组成：运行ID由40位字符组成，是一个随机的十六进制字符

	例如：fdc9ff13b...........

- 作用：运行ID被用于在服务器间进行传输，识别身份

	如果想两次操作均对同一台服务器进行，必须每次操作携带对应的运行ID，用于对方识别

- 实现方式：运行ID在每台服务器启动时自动生成的，master在首次连接slave时，会将自己的运行ID发送给slave，slave保存此ID，通过info server命令，可以查看节点的run_id



#### 复制缓冲区

![image-20200621164907488](/Users/didi/Library/Application Support/typora-user-images/image-20200621164907488.png)

- 概念：复制缓冲区，又名复制积压缓冲区，是一个先进先出（FIFO）的队列，用于存储服务器执行过的命令，每次传播命令，master都会将传播的命令记录下来，并存储在复制缓冲区

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200621165335641.png" alt="image-20200621165335641" style="zoom:33%;" />

复制缓冲区默认数据存储空间大小是1M，由于存储空间大小是固定的，当入队元素的数量大于队列长度时，最先入队的元素会被弹出，而新元素会被放入队列



- 由来：每台服务器启动时，如果开启有AOF或被连接称为master节点，即创建复制缓冲区

- 作用：用于保存master接收到的所有指令（仅影响数据变更的指令，例如set、select）
- 数据来源：当master接收到客户端的指令时，除了将指令执行，还会将该指令存储到缓冲区中



#### 主从服务器复制偏移量（offset）

- 概念：一个数字，描述复制缓冲区中的指令字节位置

- 分类：

	- master复制偏移量：记录发送给所有slave的指令字节对应的位置（多个）
	- slave复制偏移量：记录slave接受master发送过来的指令字节对应的位置（一个）

- 数据来源：

	master端：发送一次记录一次

	slave端：接受一次记录一次

- 作用：同步信息，比对master与slave的差异，当slave断线后，恢复数据使用



#### 数据同步+命令传播阶段工作流程

![image-20200621170826915](/Users/didi/Library/Application Support/typora-user-images/image-20200621170826915.png)



#### 心跳机制

- 进入命令传播阶段后，master和slave间进行信息交换，使用心跳机制进行维护，实现双方连接保持在线



- master心跳
	- 指令：PING
	- 周期：由repl-ping-slave-period决定，默认10秒
	- 作用：判断slave是否在线
	- 查询：INFO replication      获取slave最后一次连接时间间隔，lag项维持在0或1视为正常
- slave心跳任务
	- 指令：REPLCONF ACK {offset}
	- 周期：1秒
	- 作用1：汇报slave自己的复制偏移量，获取最新的数据变更指令
	- 作用2：判断master是否在线



#### 心跳阶段注意事项

- 当slave多数掉线，或延迟过高时，master为保障数据稳定性，将拒绝所有信息同步操作

	**min-slaves-to-write** 2

	**min-slaves-max-lag** 8

	slave数量少于2个，或者所有slave的延迟都大于等于8秒时，强制关闭master的写功能，停止数据同步

- salve数量由slave发送**REPLCONF ACK**命令做确认
- slave延迟由slave发送**REPLCONF ACK**命令做确认

![image-20200621172344317](/Users/didi/Library/Application Support/typora-user-images/image-20200621172344317.png)



#### 频繁的网络问题（1）

- 问题现象

	- master的CPU占用过高 或 slave频繁断开连接

- 问题原因

	- slave每1秒发送REPLCONF ACK命令到master
	- 当slave接收到慢查询时（keys *, hgetall等），会大量占用CPU性能
	- master每1秒调用复制定时函数replicationCron()，对比slave发现长时间没有进行响应

- 最终结果

	- master各种资源（输出缓冲区、带宽、连接等）被严重占用

- 解决方案

	- 通过设置合理的超时时间，确认是否释放slave

		**repl-timeout** 

		该参数定义了超时时间的阈值（默认60秒），超过该值，释放slave

#### 频繁的网络中断（2）

- 问题现象
	- slave与master连接断开
- 问题原因
	- master发送ping指令频度较低
	- master设定超市时间较短
	- ping指令在网络中存在丢包
- 解决方案
	- 提高ping命令发送的频度

超时时间repl-time的时间至少是ping指令频度的5到10背，否则slave很容易判定超时



### 哨兵模式

- 将宕机的master下线
- 找一个slave作为master
- 通知所有的slave连接新的master
- 启动新的master与slave
- 全量复制 * N+部分复制 * N

#### 哨兵

哨兵（sentinel）是一个分布式系统，用于对主从结构中的每台服务器进行**<u>监控</u>**，当出现故障时通过投票机制**<u>选择</u>**新的master并将所有slave连接到新的master

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200621174415219.png" alt="image-20200621174415219" style="zoom:33%;" />

**<u>监控</u>**

不断的检查master和slave是否正常运行

master存活检测、master与slave运行情况检测



**<u>通知（提醒）</u>**

当被监控的服务器出现问题时，向其他（哨兵间，客户端）发送通知



<u>**自动故障转移**</u>

断开master与slave连接，选取一个slave作为master，将其他slave连接到新的master，并告知客户端新的服务器地址



**注意**

哨兵也是一台Redis服务器，只是不提供数据服务

通常哨兵配置数量为单数



哨兵配置文件

```s
port 26379
dir ../data
sentinel monitor mymaster 127.0.0.1 6379 2   (如果有2个哨兵判断它挂了，那么就认为它挂了，通常为半数+1)
sentinel down-after-milliseconds mymaster 30000  （主连接多长时间没响应就认为它挂了）
sentinel parallel-syncs mymaster 1  （一次有多少个开始同步，越小服务器的压力越小，但是压力越小速度越慢，压力越大，速度越快）
sentinel failover-timeout mymaster 180000  （进行同步的时候，多长时间同步完成算有效，不可能给很长的时间让它们来同步，30秒认定同步超时）
```

```shell
sed 's/26379/26380/g' sentinel-26379.conf > sentinel-26380.conf
```



#### 哨兵工作原理

#### 阶段一：监控阶段

- 用于同步各个节点的状态信息
	- 获取各个sentinel的状态（是否在线）
	- 获取master的状态
		- master的属性
			- run_id
			- role: master
		- 各个slave的详细信息
	- 获取所有slave的状态（根据master中的slave信息）
		- slave属性
			- run_id
			- role: slave
			- master_host、master_port
			- offset
			- ......

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200621185215232.png" alt="image-20200621185215232" style="zoom:33%;" />

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200621185527101.png" alt="image-20200621185527101" style="zoom: 33%;" />

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200621185645046.png" alt="image-20200621185645046" style="zoom: 33%;" />



<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200621185946494.png" alt="image-20200621185946494" style="zoom:33%;" />



<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200621190118300.png" alt="image-20200621190118300" style="zoom:33%;" />



<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200621190400772.png" alt="image-20200621190400772" style="zoom:33%;" />

**总结**

- 监控
	- 同步信息
- 通知
	- 保持联通
- 故障转移
	- 发现问题
	- 精选负责人
	- 优选新master
	- 新master上任，其他slave切换master，原master作为slave故障回复后连接





### 集群

配置集群

```
cluster-enabled yes
cluster-config-file nodes-6379.conf
cluster-node-timeout 10000    (超时时间)
```



```
redis-cli --cluster create 127.0.0.1:6379 127.0.0.1:6380 127.0.0.1:6381 127.0.0.1:6382 127.0.0.1:6383 127.0.0.1:6384 --cluster-replicas 1
```

--replicas参数指定集群中每个主节点配备几个从节点，这里设置为1

![image-20200622152940715](/Users/didi/Library/Application Support/typora-user-images/image-20200622152940715.png)

可以看到127.0.0.1:6379、127.0.0.1:6380、127.0.0.1:6381作为master，剩下的分别作为了对应master的备份节点，在3个master节点中一共分配了16384个槽



连接集群客户端

```
redis-cli -c    要加-c参数
```

执行get\set操作会重定向到对应的槽中

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200622154134457.png" alt="image-20200622154134457" style="zoom:33%;" />

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200622154239053.png" alt="image-20200622154239053" style="zoom:33%;" />

在从节点6382上

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200622154342038.png" alt="image-20200622154342038" style="zoom:33%;" />

在从节点6383上

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200622154602635.png" alt="image-20200622154602635" style="zoom:33%;" />

在从节点6384上

<img src="/Users/didi/Library/Application Support/typora-user-images/image-20200622154645391.png" alt="image-20200622154645391" style="zoom:33%;" />



**cluster nodes** 命令查看集群中的节点情况



#### 主从下线与主从切换

当从节点下线后，只是标记了一下；从新连接后又会自动连接上并做同步操作

当主节点下线后，从节点作为了主节点；主节点重新连接上后将作为现在的主节点（原来的从节点）的从节点，并进行同步操作



#### Cluster配置

- 设置加入cluster，成为其中的节点

	**cluster-enabled**  yes|no

- cluster配置文件名，该文件属于自动生成，仅用于快速查找文件并查询文件内容

	**cluster-config-file** <filename>

- 节点服务响应超时时间，用于判定该节点是否下线或切换从节点

	**cluster-node-timeout** <milliseconds>

- master连接的slave最小数量

	**cluster-migration-barrier** <count>



- 查看集群节点信息

	**cluster** **nodes**

- 进入一个从节点redis，切换其主节点

	**cluster replicate** <master-id>

- 发现一个新节点，新增主节点

	**cluster meet** ip:port

- 忽略一个没有solt的节点

	**cluster forget** <id>

- 手动故障转移

	**cluster** **failover**



redis-6379.conf配置哦文件

```
port 6379
dbfilename dump-6379.rdb
dir ../data/
daemonize no
#logfile ../data/log/6379.log
rdbcompression yes
rdbchecksum yes
save 10 2
appendonly yes
appendfsync always
appendfilename appendonly-6379.aof
bind 127.0.0.1
databases 16

cluster-enabled yes
cluster-config-file nodes-6379.conf
cluster-node-timeout 10000
```



### 缓存预热

前期准备工作：

1. 日常例行统计数据访问记录，统计访问频度较高的热点数据

2. 利用LRU数据删除策略，构建数据留存队列

	例如：storm和Kafka配合

准备工作：

3. 将统计结果中的数据分类，根据级别，redis优先加载级别较高的热点数据
4. 利用分布式多服务器同时进行数据读取，提速数据加载过程

实施：

1. 使用脚本程序固定触发数据预热过程
2. 如果条件允许，使用了CDN（内容分发网络），效果会更好

总结：

缓存预热就是系统启动前，提前将相关的缓存数据直接加载到缓存系统。避免在用户请求的时候，先查询数据库，然后再将数据缓存的问题！用户直接查询实现被预热的缓存数据！直接查的话会让刚开始的请求都打到数据库上，导致数据库前期出现瓶颈问题。



### 缓存雪崩

1. 在一个**<u>较短</u>**的时间内，缓存中**<u>较多</u>**的key**<u>集中过期</u>**
2. 此周期内请求访问过气的数据，redis未命中，redis向数据库获取数据
3. 数据库同时接收到大量的请求无法及时处理
4. Redis大量请求被积压，开始出现超时现象
5. 数据库流量激增没数据库崩溃
6. 重启后仍然面对缓存中无数据可用
7. Redis服务器资源被严重占用，Redis服务器崩溃
8. Redis集群呈现崩溃，集群瓦解
9. 应用服务器无法及时得到数据响应请求，来自客户端的请求数量越来越多，应用服务器崩溃
10. 应用服务器，Redis，数据库全部重启，效果不理想

**解决方案**

1. 更多的页面静态化处理

2. 构建多级缓存架构

	Nginx缓存 + Redis缓存 + ehcache缓存

3. 检测Mysql严重耗时业务进行优化

	对数据库的瓶颈排查：例如超时查询、耗时较高事务等

4. 灾难预警机制

	监控Redis服务器性能指标

	- CPU占用、CPU使用率
	- 内存容量
	- 查询平均响应时间
	- 线程数

5. 限流、降级

	短时间内牺牲一些客户体验，限制一部分请求访问，降低应用服务器压力，待业务低速运转后再逐步放开访问

**解决方案**

1. LRU与LFU切换

2. 数据有效期策略调整

	- 根据业务数据有效期进行分类错峰，A类90分钟、B类80分钟、C类70分钟
	- 过期时间使用固定时间+随机值的形式，稀释集中到期的key的数量

3. 超热数据使用永久key

4. 定期维护（自动+人工）

	对即将过期数据做访问量分析，确认是否延时，配合访问量统计，做热点数据的延时

5. 加锁

	慎用



### 缓存击穿

缓存击穿就是单个高热数据过期的瞬间，数据访问量较大，未命中redis后，发起了大量对同一数据的数据库访问，导致对数据库服务器造成压力。

**解决方案**

1. 预先设定

2. 现场调整

	监控访问量，对自然流量激增的数据演唱过期时间或设置为永久性key

3. 后台刷新数据

	启动定时任务，高峰期来临之前，刷新数据有效期，确保不丢失

4. 二级缓存

	设置不同的实效时间，保障不会被同时淘汰就行

5. 加锁

	分布式锁，防止被击穿，但是要注意也是性能瓶颈，慎重



### 缓存穿透

1. Redis中大面积出现未命中
2. 出现非正常的URL访问

问题分析

- 获取的数据在数据库中不存在，数据库查询未得到对应数据
- Redis获取到null数据未进行持久化，直接返回
- 下次此类数据到达重复上述过程
- 出现黑客攻击服务器

**解决方案**

1. 缓存null

	对查询结果为null的数据进行缓存（长期使用，定期清理），设定段时间，例如30-60秒，最高50分钟

2. 白名单策略

	- 提前预热各种分类数据id对应的bitmaps，id作为bitmaps的offset，相当于设置了数据白名单，当加载正常数据时，放行，加载异常数据时直接拦截（效率较低）
	- 使用布隆过滤器（油罐布隆过滤器的命中问题对当前状况可以忽略）

3. 实施监控

	实时监控redis命中率（业务正常范围时，通常会有一个波动值）与null数据的占比

	- 非活动时段波动：通常检测3-5倍，超过5倍纳入重点排查对象
	- 活动时段波动：通常检测10-50倍，超过50倍纳入重点排查对象

	根据倍数不同，启动不同的排查流程。然后使用黑名单进行防空（运营）

4. key加密

	每天随机分配几个。在正常的参数里面按照设计好的机制加上某些个参数再作为参数。黑客不知道这种机制就可以将这种非正常key拦截掉

**总结**

缓存击穿访问了不存在的数据，跳过了合法数据的redis数据缓存阶段，每次访问数据库，导致对数据库服务器造成药理。通常此类数据的出现是一个较低的值，当出现此类情况以毒攻毒，并及时**<u>报警</u>**。应对策略应该在临时预备案防范方面多做文章。

无论是黑名单还是白名单，都是对整体系统的压力，报警解除后尽快移除。



### 性能指标监控

- 性能指标：Performance

| Name                      | Description             |
| ------------------------- | ----------------------- |
| latency                   | Redis响应一个请求的时间 |
| instantaneous_ops_per_sec | 平均每秒处理请求总数    |
| hit rate(calculated)      | 缓存命中率              |



- 内存指标：Memory

| Name                    | Description                                      |
| ----------------------- | ------------------------------------------------ |
| used_memory             | 已使用的内存                                     |
| mem_fragmentation_ratio | 内存碎片率                                       |
| evicted_keys            | 由于最大内存限制被移除的key的数量                |
| blocked_clients         | 由于BLPOP，BRPOP， or BRPOPLPUSH而备阻塞的客户端 |



- 基本活动指标：Basic activity

| Name                       | Description                |
| -------------------------- | -------------------------- |
| connected_clients          | 客户端连接数               |
| connected_slaves           | Salve数量                  |
| master_last_io_seconds_ago | 最近一次主从交互之后的秒数 |
| keyspace                   | 数据库中的key值总数        |



- 错误指标：Error

| Name                           | Description                           |
| ------------------------------ | ------------------------------------- |
| rejected_connections           | 由于达到maxclient限制而被拒绝的连接数 |
| keyspace_misses                | Key值查找失败（没有命中）次数         |
| master_link_down_since_seconds | 主从断开的持续时间（以秒为单位）      |



监控命令

**benchmark**

- 命令

	redis-benchmark [-h] [-p] [-c] [-n <requests>] [-k]

- 范例

	redis-**benchmark**

	说明：50个连接，10000次请求对应的性能

- 范例2

	redis-benchmark -c 100 -n 5000

	说明：100个连接，5000次请求对应的性能

| 序号 | 选项  | 描述                                    | 默认值    |
| ---- | ----- | --------------------------------------- | --------- |
| 1    | -h    | 指定服务器主机名                        | 127.0.0.1 |
| 2    | -p    | 指定服务器端口号                        | 6379      |
| 3    | -s    | 指定服务器socket                        |           |
| 4    | -c    | 指定并发连接数                          | 50        |
| 5    | -n    | 指定请求数                              | 10000     |
| 6    | -d    | 以字节的形式指定SET/GET值的数据大小     | 2         |
| 7    | -k    | 1=keep alive 0=reconnect                | 1         |
| 8    | -r    | SET/GET/INCR使用随机key，SADD使用随机值 |           |
| 9    | -P    | 通过管道传输<numreq>请求                | 1         |
| 10   | -q    | 强制退出redis，仅显示query/sec值        |           |
| 11   | --csv | 以csv格式输出                           |           |
| 12   | -l    | 生成循环，永久执行测试                  |           |
| 13   | -t    | 仅运行以都好分割的测试命令列表          |           |
| 14   | -I    | Idel模式，仅打开N个idle连接并等待       |           |



- moniter

	是redis命令，要在redis客户端执行



slowlog

- 命令

	slowlog [operator]

	- get: 获取慢查询日志
	- len: 获取慢查询日志条目数
	- reset: 重置慢查询日志

- 相关配置

	**slowlog-log-slower-than** 1000 #设置慢查询的时间下线，单位：微妙

	**slowlog-max-len** 100 #设置慢查询命令对应的日志显示长度，单位：命令数







