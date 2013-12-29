package me.fuluchii.jobpool.bootstrap;

import me.fuluchii.jobpool.classloader.ClassLoaderFactory;
import me.fuluchii.jobpool.repository.JobRepository;
import me.fuluchii.jobpool.repository.RepositoryCenter;

import java.net.MalformedURLException;

/**
 * Created with IntelliJ IDEA.
 * User: fuluchii
 * Date: 13-12-29
 * Time: 下午6:07
 * To change this template use File | Settings | File Templates.
 */
public class Bootstrap {
    public static ClassLoader parentCL;

    public static void initializePool()  throws MalformedURLException{
        parentCL = ClassLoaderFactory.createClassLoader("common",null,Thread.currentThread().getContextClassLoader());
    }

    public static void main(String[] args){
        try {
            Bootstrap.initializePool();
            JobRepository jobRepository1 = new JobRepository("test1",5000,"/Users/fuluchii/Documents/java/job-scheduler/JobPool/src/main/resources/testjob1","me.fuluchii.testjob.Main.Main");
            JobRepository jobRepository2 = new JobRepository("test2",5000,"/Users/fuluchii/Documents/java/job-scheduler/JobPool/src/main/resources/testjob2","me.fuluchii.testjob.Main.Main");

            RepositoryCenter.registerRepository(jobRepository1.getJobName(),jobRepository1);
            RepositoryCenter.registerRepository(jobRepository2.getJobName(),jobRepository2);

            RepositoryCenter.run("test1");
            RepositoryCenter.run("test2");

            RepositoryCenter.await();
        } catch (MalformedURLException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
