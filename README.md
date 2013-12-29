##JobPool基础设计文档及demo

====

+ ####背景

	之前做过的更改将Job的逻辑迁移到了web项目中，并且统一做了运行管理。
	
	这种做法有如下的缺陷：
	- 业务中存在多个jar包可能会依赖同一个service不同版本的情况。将逻辑都放在一起无法隔离依赖。
	- 由于Job数量可能会增多，Job的依赖关系和配置都加在web项目中，导致web和job耦合过重。
	- 将jar包代码都放入web项目中容易造成代码管理的混乱。
	- 由于对job的控制全部交给quartz，不能灵活控制。
	
	
	基于以上的原因（主要是1，2），需要建立新的Job管理系统。
	
+ ####基本设计

	jobPool将每个Job作为一个独立的app，动态建立每个app对应的classloader，完全分离job调度系统和Job本身的依赖关系。Job可以独立开发，最后打成Jar包，通过配置存入jobPool进行调度.
	
	ClassLoader结构图：
	![JobPool ClassLoader](https://github.com/fuluchii/JobPool/blob/master/image/classloader.jpg?raw=true "JobPool ClassLoader")
	
	* ####Bootstrap
	
		目前仅用于初始化commonClassLoader.(继承自applicationClassLoader)
	
	* ####JobClassLoader & JobClassLoaderFactory
	
		在初始化时，每个Job都会通过`JobClassLoaderFactory`生成对应的classLoader，其parent为jobPool的commonClassLoader.
		
		接口：JobClassLoader
		
		简单实现（见demo）:`SimpleJarJobClassLoader`,基于jar包路径的classLoader。
		
		*拓展*:可以自定义其他的classLoader，实现JobClassLoder接口即可
		+ 1.基于package路径的jobClassLoader
		+ 2.基于非本地文件的jobClassLoader
		
	* ####JobClassLoaderRule
		
		由于业务上的需求，一些提供基础数据的service或者配置应该是全部Job通用的，也方便统一升级和管理。而另一些job特定的service则应该由Job内部管理。
						
		
		通过重写jobClassLoader的findClass方法，增加JobClassLoderRule条件过滤，可以打破双亲委托模式，强迫一些class必须使用commonClassLoader，而另一些则必须从jobClassLoader读取。
		
		这种实现方式主要是为了将基础数据service和Job特定的业务逻辑service做隔离，减少管理成本。
		
		=====
	Job调度示意图：
	![JobPool Repository](https://github.com/fuluchii/JobPool/blob/master/image/repository.jpg?raw=true "JobPool ClassLoader")

	
		
	* ####JobRepository
		JobRepository负责具体的job执行。
		
		每个JobRepository维护一个singleThreadPool,负责执行对应Job。该线程池管理范围内的所有线程都使用对应的jobClassLoader作为classLoader.	
		
			Thread.currentThread().setContextClassLoader(classLoader);

			
		配置：
		
		- 对应Job及lib文件的jar包存放路径，用于初始化classloader。（目前Job可以通过assembly打包或者mvn package将所有依赖lib都一起导出。）
		
		- Job入口类和函数（规定为Main），用于在运行线程中直接反射运行Job。
		
		- jobname，用于标识唯一的Job
		
		- maxjobruntime：限制Job运行的时间。到时间会关闭Job运行的线程池。
		
	* ####RepositoryCenter
	
		负责job的调度过程，使用quartz做调度系统。（由于quartz api有成熟的实现，没有在demo实现）
		
		center维护一个Job列表和job运行线程池。统一管理Job的运行。通过completionService获取Job的运行结果。提供register,remove,await等方法。
		
----
##DEMO

testjob1,testjob2分别依赖了testlib的1.0版本和2.0版本。
两个版本的testlib的sayhello方法分别打印test1,test2.

执行：

	Bootstrap.initializePool();
            JobRepository jobRepository1 = new JobRepository("test1",5000,"/Users/fuluchii/Documents/java/job-scheduler/JobPool/src/main/resources/testjob1","me.fuluchii.testjob.Main.Main");
            JobRepository jobRepository2 = new JobRepository("test2",5000,"/Users/fuluchii/Documents/java/job-scheduler/JobPool/src/main/resources/testjob2","me.fuluchii.testjob.Main.Main");

            RepositoryCenter.registerRepository(jobRepository1.getJobName(),jobRepository1);
            RepositoryCenter.registerRepository(jobRepository2.getJobName(),jobRepository2);

            RepositoryCenter.run("test1");
            RepositoryCenter.run("test2");

            RepositoryCenter.await();
            	
	
结果为：
	
	I am testjob1.
	I am testjob2.
	I am test 1.
	quit
	I am test 2.
	quit
		
	
		
		
		
		
		
		
	
	
	
	
	
