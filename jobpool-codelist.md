##Codelist-JobPool

[主要代码链接](https://github.com/fuluchii/JobPool/tree/master/src/main/java/me/fuluchii/jobpool)

----------------------

classLoaderfactory:

	public class ClassLoaderFactory{

    public static ClassLoader createClassLoader(String name,String[] locations,ClassLoader parent) throws MalformedURLException {
        //construct classpath for subLoader
        Set<URL> set = new LinkedHashSet<URL>();

        //read jar files from location
        for (String location : locations) {
            File locationPath = new File(location);
            if (!locationPath.isDirectory() || !locationPath.exists() || !locationPath.canRead()){
                continue;
            }
            String[] jars = locationPath.list();
            for (String jar : jars) {
                if(jar.endsWith(".jar")){
                    File jarfile = new File(locationPath, jar);
                    URL jarURL = jarfile.toURI().toURL();
                    set.add(jarURL);
                }
            }
        }

        //create classLoader
        SimpleJarJobClassLoader classLoader = null;
        if(null == parent){
           classLoader = new SimpleJarJobClassLoader(name,set.toArray(new URL[set.size()]));
        } else{
            classLoader = new SimpleJarJobClassLoader(name,set.toArray(new URL[set.size()]),parent);
        }
        return classLoader;
    }

	}

------------------
RepositoryCenter：

	public class RepositoryCenter {

    static ConcurrentHashMap<String,JobRepository> jobRepositories;

    static ExecutorService jobPool;

    static CompletionService jobCompletionService;

    private static AtomicInteger jobCount;

    static {
        jobRepositories = new ConcurrentHashMap<String, JobRepository>();
        jobPool = Executors.newCachedThreadPool();
        jobCompletionService = new ExecutorCompletionService(jobPool);
        jobCount = new AtomicInteger(0);
    }

    private final RepositoryCenter repositoryCenter = new RepositoryCenter();

    public static void registerRepository(String jobkey,JobRepository jobRepository){
        jobRepositories.put(jobkey, jobRepository);
    }

    public static void removeRepository(String jobkey){
        jobRepositories.remove(jobkey);
        jobCount.decrementAndGet();
    }

    public static void run(String jobkey){
        jobPool.submit(jobRepositories.get(jobkey));
        jobCount.incrementAndGet();
    }

    public static void await(){
        try {
            int jobCountNow = jobCount.intValue();
            for (int i = 0 ;i<jobCountNow;i++){
                Future future = jobCompletionService.take();
                System.out.println(future.get());
            }
            jobPool.shutdown();
        } catch (InterruptedException e) {
            jobPool.shutdown();
        } catch (ExecutionException ignore) {

        }
    }


	}