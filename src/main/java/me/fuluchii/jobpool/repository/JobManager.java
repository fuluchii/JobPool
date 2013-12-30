package me.fuluchii.jobpool.repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: fuluchii
 * Date: 13-12-29
 * Time: 下午7:59
 * To change this template use File | Settings | File Templates.
 */
public class JobManager implements Runnable {
    private String mainClassName;
    private ClassLoader classLoader;

    public JobManager(String mainClassName,ClassLoader classLoader) {
        this.mainClassName = mainClassName;
        this.classLoader = classLoader;
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            //load main class of job jar.
            Class mainClazz = Thread.currentThread().getContextClassLoader().loadClass(mainClassName);
            Object mainjob = mainClazz.newInstance();

            //invoke main method
            Class[] parameterTypes = new Class[]{String[].class};
            Method main = mainClazz.getDeclaredMethod("main",parameterTypes);
            main.invoke(mainjob, new Object[]{new String[]{}});

        } catch (ClassNotFoundException e) {
            System.out.println("no such class:"+mainClassName);
        } catch (InstantiationException e) {
            //TODO
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            //TODO
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            //TODO
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            //TODO
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("quit");
        return;
    }

}
