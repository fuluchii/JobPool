package me.fuluchii.jobpool.repository;

import me.fuluchii.jobpool.bootstrap.Bootstrap;
import me.fuluchii.jobpool.classloader.ClassLoaderFactory;
import me.fuluchii.jobpool.classloader.SimpleJarJobClassLoader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: fuluchii
 * Date: 13-12-11
 * Time: 下午7:43
 * To change this template use File | Settings | File Templates.
 */
public class JobRepository implements Runnable {
    private String jobName;

    //Wait for maxjobruntime millseconds.Then send an interrupt flag to JobServcie to stop manager.
    private long maxJobRunTime;

    //create a subClassLoader from job path
    private String jobLoaderPath;

    private String mainClassName;

    private ExecutorService jobService;

    private JobManager manager;

    public JobManager getManager() {
        return manager;
    }

    public void setManager(JobManager manager) {
        this.manager = manager;
    }

    public JobRepository(String jobName, long maxJobRunTime, String jobLoaderPath,String mainClassName) {
        this.jobName = jobName;
        this.maxJobRunTime = maxJobRunTime;
        this.jobLoaderPath = jobLoaderPath;
        this.mainClassName = mainClassName;
    }

    private void initJobRepository(){
        try{
            SimpleJarJobClassLoader simpleJarJobClassLoader = (SimpleJarJobClassLoader)ClassLoaderFactory.createClassLoader(jobName,new String[]{jobLoaderPath}, Bootstrap.parentCL);
            jobService = Executors.newSingleThreadExecutor();
            manager = new JobManager(mainClassName, simpleJarJobClassLoader);
        }catch (Exception e){
            System.out.println("Error when initialize job.");
        }
    }

    public void run(){
        try {
            initJobRepository();
            jobService.submit(manager);
            jobService.awaitTermination(1000,TimeUnit.MILLISECONDS);
            jobService.shutdown();
        } catch (InterruptedException e) {
            //stop jobservice when receiving an interrupt flag
            jobService.shutdown();
            //throw the exception
            try {
                throw e;
            } catch (InterruptedException e1) {
                //if thread is interrupted when throw exception;
                e1.printStackTrace();
            }
        }
        return;

    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public long getMaxJobRunTime() {
        return maxJobRunTime;
    }

    public void setMaxJobRunTime(long maxJobRunTime) {
        this.maxJobRunTime = maxJobRunTime;
    }

    public String getJobLoaderPath() {
        return jobLoaderPath;
    }

    public void setJobLoaderPath(String jobLoaderPath) {
        this.jobLoaderPath = jobLoaderPath;
    }
}
