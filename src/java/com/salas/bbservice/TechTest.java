package com.salas.bbservice;

import com.salas.bbservice.service.meta.discovery.TechnoratiDiscoverer;
import com.salas.bbservice.service.meta.discovery.HtmlDiscoverer;
import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.utils.Configuration;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: alg
 * Date: Oct 27, 2008
 * Time: 8:47:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class TechTest {
    public static void main(String[] args) throws MalformedURLException {
        Configuration.getBaseForumURL();

        HashMap props = new HashMap();
        props.put("key", "f093ddd8ba391c2139625ccd9d544c06");

        HtmlDiscoverer t = new HtmlDiscoverer();
        t.setProperties(props);
        
        Blog blog = new Blog();
        URL url = new URL("http://www.garmin.com/garmin/cms/site/us");
//        URL url = new URL("http://blogsearch.google.com/");
        try {
            int res = t.discover(blog, url);
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
