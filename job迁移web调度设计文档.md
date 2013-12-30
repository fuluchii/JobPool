###Job迁移web调度设计文档

-------

+ ####背景
	
	酒店业务中有很多Job，都需要从OP进行部署运行。但由于和第三方交互很多，所以job更改频繁，数量也很多，管理起来不够灵活，上线流程较为复杂。基于这个原因，需要将原先的job迁移到web项目中，以便上线管理和更新。
	
+ ####基本设计

	job迁移web主要分为2部分：
	
	- Job逻辑迁移及定时调度
	
	- 手动调度Job方法
	
	大体结构如下：
	![JobMigration](https://github.com/fuluchii/JobPool/blob/master/image/jobsystem.jpg?raw=true "")
	
	=============
+ ####具体设计
	
	* #####jobManageFactory & manageTask
		JobManageTask是Job运行的通用主线程，对于新增加的job，可以通过实现其abstract方法自定义运行的内容。
		
		ManageTask维护一个线程池，其中每个子线程都调用service提供的方法，这是酒店业务中比较通用的一种Job模式，即批量调用第三方api获取数据。
	
		根据job的配置信息。可以通过ManageFactory生成对应的JobManageTask.
		
	* #####基于Quartz-spring的Job调度管理
	
		目前Job的基本信息配置在xml文件中，多数为crontab形式，通过quartz-spring进行调度管理，
		
	----------
	* #####Job手动调度
		在业务需求中经常需要手动fire Job来及时获取数据。考虑到迁移成本，暂时没有做可视化的后台，而是通过socket端口监听命令fire job。
		
		- #####SocketServer && SocketHandler
		
			在web项目中监听某个socket端口（1989），每一个连接都新建一个socketHandler线程处理。
			socketHandler线程会对接收的命令进行parse处理,标准命令格式如下：
			
			[jobname],[otaname],[startdate],[enddate]
			
			符合该标准的命令，会生成一个对应的JobCommand对象。
			
		- #####JobCommandObserver
		
			JobCommandObserver是一个负责fire job的单例对象。实现observer接口，监听是否有fire job活动。
			
			SocketHandler继承observable，初始化时注册JobCommmandObserver为监听者。当Parse出正确的jobCommand对象时，调用Notify方法，并将jobCommand对象传递给jobCommandObserver.
			
			JobCommandObserver接收信息后根据对应的command内容调度quartz job。
			
		- #####手动调度操作：
		telnet到给定的端口，执行相应命令。
		
	--------
+ ####TODO

	目前订单Job已经迁移至web项目中并成功运行。但是这种调度方式存在一定的问题，需要做修改。
	
	修改方案见：
	
	[JobPool基本设计及demo](https://github.com/fuluchii/JobPool)
			
	
	
	