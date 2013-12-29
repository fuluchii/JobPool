package me.fuluchii.jobpool.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: fuluchii
 * Date: 13-12-29
 * Time: 下午6:43
 * To change this template use File | Settings | File Templates.
 */
public class SimpleJarJobClassLoader extends JobClassLoader{
    public SimpleJarJobClassLoader(String loaderName, URL[] urls, ClassLoader parent){
        super(urls,parent,loaderName);
    }
    public SimpleJarJobClassLoader(String loaderName, URL[] urls){
        super(urls);
    }

    public SimpleJarJobClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public SimpleJarJobClassLoader(URL[] urls) {
        super(urls);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //TODO check classloader rules,break parent-delegate pattern.
        return super.findClass(name);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SimpleJarJobClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }
}
