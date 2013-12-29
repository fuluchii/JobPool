package me.fuluchii.jobpool.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: fuluchii
 * Date: 13-12-30
 * Time: 上午1:06
 * To change this template use File | Settings | File Templates.
 */
public class JobClassLoader  extends URLClassLoader {
    private String loaderName = "default";

    public JobClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public JobClassLoader(URL[] urls) {
        super(urls);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public JobClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public JobClassLoader(URL[] urls, String loaderName) {
        super(urls);
        this.loaderName = loaderName;
    }

    public JobClassLoader(URL[] urls, ClassLoader parent, String loaderName) {
        super(urls, parent);
        this.loaderName = loaderName;
    }

    public String getLoaderName() {
        return loaderName;
    }

    public void setLoaderName(String loaderName) {
        this.loaderName = loaderName;
    }
}
