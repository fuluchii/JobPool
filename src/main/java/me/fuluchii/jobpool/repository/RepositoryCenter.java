package me.fuluchii.jobpool.repository;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: fuluchii
 * Date: 13-12-29
 * Time: 下午7:42
 * To change this template use File | Settings | File Templates.
 */
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
