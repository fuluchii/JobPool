package me.fuluchii.jobpool.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: fuluchii
 * Date: 13-12-29
 * Time: 下午6:30
 * To change this template use File | Settings | File Templates.
 */
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
