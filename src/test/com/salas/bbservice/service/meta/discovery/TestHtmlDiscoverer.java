// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002, 2003, 2004 by R. Pito Salas
//
// This program is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software Foundation;
// either version 2 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
// without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with this program;
// if not, write to the Free Software Foundation, Inc., 59 Temple Place,
// Suite 330, Boston, MA 02111-1307 USA
//
// Contact: R. Pito Salas
// mailto:pito_salas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: TestHtmlDiscoverer.java,v 1.3 2007/12/11 12:15:54 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @see HtmlDiscoverer
 */
public class TestHtmlDiscoverer extends TestCase
{
    /**
     * @see HtmlDiscoverer#discover(String)
     */
    public void testDiscover1()
    {
        String head =
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"ru-RU\" lang=\"ru-RU\">\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
            "<title>noizzze</title>\n" +
            "<base href=\"http://localhost/b2evolution/blogs/skins/wpc_aubmach2/\" />\n" +
            "<meta name=\"description\" content=\"\" />\n" +
            "<meta name=\"keywords\" content=\"\" />\n" +
            "<meta name=\"generator\" content=\"b2evolution 0.9.0.11\" /> <!-- Please leave this for stats -->\n" +
            "<link rel=\"alternate\" type=\"text/xml\" title=\"RDF\" href=\"http://localhost/b2evolution/blogs/xmlsrv/rdf.php?blog=8\" />\n" +
            "<link rel=\"alternate\" type=\"text/xml\" title=\"RSS .92\" href=\"http://localhost/b2evolution/blogs/xmlsrv/rss.php?blog=8\" />\n" +
            "<link rel=\"alternate\" type=\"text/xml\" title=\"RSS 2.0\" href=\"http://localhost/b2evolution/blogs/xmlsrv/rss2.php?blog=8\" />\n" +
            "<link rel=\"alternate\" type=\"application/atom+xml\" title=\"Atom\" href=\"http://localhost/b2evolution/blogs/xmlsrv/atom.php?blog=8\" />\n" +
            "<link rel=\"pingback\" href=\"http://localhost/b2evolution/blogs/xmlsrv/xmlrpc.php\" />\n" +
            "\n" +
            "<style type=\"text/css\">\n" +
            "\t@import url(../../rsc/img.css);\t/* Import standard image styles */\n" +
            "\t@import url(../../rsc/blog_elements.css);\t/* Import standard blog elements styles */\n" +
            "\t@import url(style.css);\n" +
            "</style>";

        HtmlDiscoverer hd = new HtmlDiscoverer();
        assertEquals("http://localhost/b2evolution/blogs/xmlsrv/atom.php?blog=8", hd.discover(head));
    }

    /**
     * @see HtmlDiscoverer#discover(String)
     */
    public void testDiscover2()
    {
        String head =
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
            "<html>\n" +
            "<head>\n" +
            "\t<title>java.net - The Source for Java Technology Collaboration</title>\n" +
            "\t<meta name=\"description\" content=\"This is where Java[TM] technology collaboration happens. java.net is a new central meeting place for developers and Java technology enthusiasts to collaborate on projects, share ideas, and create the next big thing. Whether your project is industry specific...\" />\n" +
            "\t<meta name=\"keywords\" content=\"java.net,java.net,java,technology,central meeting place,java developers,java technology enthusiasts,collaborate,projects,,,\" >\n" +
            "\t<meta name=\"date\" content=\"October 20, 2004\" />\n" +
            "\n" +
            "<!--  CSS stylesheet  -->\n" +
            "<link rel=\"stylesheet\" href=\"/css/simple.css\" type=\"text/css\">\n" +
            "\n" +
            "<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS\" href=\"http://today.java.net/pub/q/java_today_rss?x-ver=1.0\" />\n" +
            "\n" +
            "</head>\n" +
            "<!-- stopindex -->\n" +
            "<!--  Common body tag  -->\n" +
            "<body leftmargin=\"0\" topmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" rightmargin=\"0\" bgcolor=\"#ffffff\">\n" +
            "<a name=\"top\"></a>\n" +
            "\n" +
            "<!--  Masthead  -->\n" +
            "<div>\n" +
            "<form name=\"loginform\" id=\"loginform\"  action=\"https://www.dev.java.net/servlets/TLogin\" method=\"post\">\n" +
            "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" height=\"25\" style=\"background:url(/images/header_new_bg.gif);\">\n" +
            "<tr";

        HtmlDiscoverer hd = new HtmlDiscoverer();
        assertEquals("http://today.java.net/pub/q/java_today_rss?x-ver=1.0", hd.discover(head));
    }

    /**
     * @see HtmlDiscoverer#discover(String)
     */
    public void testDiscover3()
    {
        String head =
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
            "<html>\n" +
            "<head>\n" +
            "  <title>freshmeat.net: Welcome to freshmeat.net</title>\n" +
            "  <meta name=\"description\" content=\"freshmeat maintains the Web's largest index of Unix and cross-platform open source software. Thousands of applications are meticulously cataloged in the freshmeat database, and links to new code are added daily.\">\n" +
            "  <meta name=\"keywords\" content=\"freshmeat free software open source opensource gpl gnu fsf license linux unix application apps applications latest new fresh recent linux download code project release update editorial\">\n" +
            "  <link rel=\"icon\" href=\"/favicon.ico\" type=\"image/ico\" />\n" +
            "  <link rel=\"shortcut icon\" href=\"/favicon.ico\" />\n" +
            "  <link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS feed\" href=\"/backend/fm-releases.rdf\" />\n" +
            "    <style type=\"text/css\">\n" +
            "  <!--\n" +
            "    BODY\n" +
            "  {\n" +
            "    margin: 0;\n" +
            "    padding: 0;\n" +
            "  }\n" +
            "\n" +
            "  BODY, P, DIV, TD, TH, TR, FORM, OL, UL, LI, INPUT, TEXTAREA, SELECT, A\n" +
            "  {\n" +
            "    font-family: Verdana, Tahoma, Arial, Helvetica, sans-serif;\n" +
            "      }\n" +
            "\n" +
            "  A:hover  {\n" +
            "    color: #3366CC;\n" +
            "  \ttext-decoration: none;\n" +
            "  }\n" +
            "\n" +
            "  A  {\n" +
            "    color: #3366CC;\n" +
            "  \ttext-decoration: underline;\n" +
            "  }";
        HtmlDiscoverer hd = new HtmlDiscoverer();
        assertEquals("/backend/fm-releases.rdf", hd.discover(head));
    }

    /**
     * @see HtmlDiscoverer#discover(String)
     */
    public void testDiscover4()
    {
        String head =
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n" +
            "\n" +
            "<title>Pito's Blog</title>\n" +
            "\n" +
            "<link rel=\"stylesheet\" href=\"http://www.salas.com/styles-site.css\" type=\"text/css\" />\n" +
            "<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS\" href=\"http://www.salas.com/index.rdf\" />\n" +
            "<link rel=\"alternate\" type=\"application/atom+xml\" title=\"Atom\" href=\"http://www.salas.com/atom.xml\" />\n" +
            "<link rel=\"EditURI\" type=\"application/rsd+xml\" title=\"RSD\" href=\"http://www.salas.com/rsd.xml\" />\n" +
            "\n" +
            "<script language=\"javascript\" type=\"text/javascript\">\n" +
            "function OpenComments (c) {\n" +
            "    window.open(c,\n" +
            "                    'comments',\n" +
            "                    'width=480,height=480,scrollbars=yes,status=yes');\n" +
            "}\n" +
            "\n" +
            "function OpenTrackback (c) {\n" +
            "    window.open(c,\n" +
            "                    'trackback',\n" +
            "                    'width=480,height=480,scrollbars=yes,status=yes');\n" +
            "}\n" +
            "</script>\n" +
            "\n" +
            "\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "\n" +
            "<div id=\"banner\">\n" +
            "<h1><a href=\"http://www.salas.com/\" accesskey=\"1\">Pito's Blog</a></h1>\n" +
            "<span class=\"description\">Some stuff I just figured out</span>\n" +
            "</div>\n" +
            "\n" +
            "<div id=\"content\">\n" +
            "\n" +
            "<div class=\"blog\">\n" +
            "";
        HtmlDiscoverer hd = new HtmlDiscoverer();
        assertEquals("http://www.salas.com/index.rdf", hd.discover(head));
    }

    /**
     * @see HtmlDiscoverer#discover(String)
     */
    public void testDiscover5()
    {
        String head =
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
            "<HTML><HEAD>\n" +
            "   \n" +
            "<script type=\"text/javascript\">\n" +
            "var hs = new Array();\n" +
            "function myCount(id){\n" +
            "if (hs[id]){\n" +
            "if (hs[id] == 1){\n" +
            "document.write(\" (1)\");\n" +
            "} else {\n" +
            "document.write(\"s (\" + hs[id] + \")\" );\n" +
            "}\n" +
            "} else {\n" +
            "document.write(\" (0)\");\n" +
            "}\n" +
            "}\n" +
            "</script>\n" +
            "\n" +
            "email:<a href=\"mailto:atrios@comcast.net\">atrios@comcast.net</a><BR><BR>\n" +
            "<a href=\"http://atrios.blogspot.com/2003/09/brief-bio.html\">Brief Bio</a><br><Br>\n" +
            "<a href=\"http://atrios.blogspot.com/rss/atrios.xml\">RSS Feed</a><BR><br>\n" +
            "<DIV class=posts>\n" +
            "<a href=\"http://atrios.blogspot.com\">Latest</a> <BR><br><a href=\"atrios_archive.html\">Archives</a> <br>\n" +
            "<BR>";
        HtmlDiscoverer hd = new HtmlDiscoverer();
        assertEquals("http://atrios.blogspot.com/rss/atrios.xml", hd.discover(head));
    }

    /**
     * Tests how the pattern detects links.
     */
    public void testLinkDetectionPattern()
    {
        assertFalse(checkAgainstLinksPattern("<a href='test'>"));
        assertTrue(checkAgainstLinksPattern("<a href='test.xml'>"));
        assertTrue(checkAgainstLinksPattern("<a href='t.rss'>"));
        assertTrue(checkAgainstLinksPattern("<a href='t.atom'>"));
        assertTrue(checkAgainstLinksPattern("<a href='t.rdf'>"));
        assertFalse(checkAgainstLinksPattern("<a href='t.rds'>"));
        assertFalse(checkAgainstLinksPattern("<a hre='t.rds'>"));
        assertFalse(checkAgainstLinksPattern("<a hre = 't.rds'>"));
        assertTrue(checkAgainstLinksPattern("<a href = 't.rdf'>"));
        assertFalse(checkAgainstLinksPattern("<ah hre = 't.rdf'>"));
        assertFalse(checkAgainstLinksPattern("a href='t.rdf'>"));
        assertFalse(checkAgainstLinksPattern("<a href='t.rdf'"));
        assertFalse(checkAgainstLinksPattern("<a href='.rdf'>"));
        assertTrue(checkAgainstLinksPattern("<a test href='a.rdf' >"));
        assertTrue(checkAgainstLinksPattern("<a test href='a.rdf' test >"));
        assertTrue(checkAgainstLinksPattern("<A test HREF='a.rdf' test >"));
        assertTrue(checkAgainstLinksPattern("<A test HREF=\"a.rdf\" test >"));
    }

    private boolean checkAgainstLinksPattern(String str)
    {
        return HtmlDiscoverer.PATTERN_FILTER_LINKS_4.matcher(str).find();
    }

    /**
     * @see HtmlDiscoverer#convertToAbsolute
     *
     * @throws Exception I/O error.
     */
    public void testConvertToAbsolute() throws Exception
    {
        URL base;

        base = new URL("http://www.g.com");
        assertEquals("http://www.g.com/test", HtmlDiscoverer.convertToAbsolute(base, "/test"));
        assertEquals("http://www.g.com/test", HtmlDiscoverer.convertToAbsolute(base, "test"));
        assertEquals("http://www.n.com", HtmlDiscoverer.convertToAbsolute(base, "http://www.n.com"));

        base = new URL("http://www.g.com/archive/a.html");
        assertEquals("http://www.g.com/test", HtmlDiscoverer.convertToAbsolute(base, "/test"));
        assertEquals("http://www.g.com/archive/test", HtmlDiscoverer.convertToAbsolute(base, "test"));
        assertEquals("http://www.n.com", HtmlDiscoverer.convertToAbsolute(base, "http://www.n.com"));
    }

    /**
     * Tests how invalid URL's are handled. No error should be generated.
     *
     * @throws Exception in case of error.
     */
    public void testErrorHandling() throws Exception
    {
        HtmlDiscoverer dis = new HtmlDiscoverer();

        // FileNotFound
        URL url1 = new URL("http://localhost/something/");

        // Auth required
        URL url2 = new URL("http://www.blogbridge.com:8080/bbservice/admin/");

        assertEquals(0, dis.discover(new Blog(), url1));
        assertEquals(0, dis.discover(new Blog(), url2));
    }

    /**
     * Tests parsing "colettas.org" page.
     *
     * @throws Exception I/O error.
     */
    public void testColettas() throws Exception
    {
        Blog source = new Blog();

        // Discover
        HtmlDiscoverer dis = new HtmlDiscoverer();
        int fields = dis.discover(source, dataURL("colettas_org.html"));

        // Check
        assertEquals(IBlogDiscoverer.FIELD_DATA_URL, fields);
        assertEquals("http://www.colettas.org/?feed=rss2", source.getDataUrl());
    }

    /**
     * Tests parsing "openid.net" page.
     *
     * @throws Exception I/O error.
     */
    public void testOpenidNet() throws Exception
    {
        Blog source = new Blog();

        // Discover
        HtmlDiscoverer dis = new HtmlDiscoverer();
        int fields = dis.discover(source, dataURL("openid_net.html"));

        // Check
        assertEquals(IBlogDiscoverer.FIELD_DATA_URL, fields);
        assertEquals("http://openid.net/feed/", source.getDataUrl());
    }

    /**
     * Tests detection and following HTML redirects.
     *
     * @throws Exception I/O error.
     */
    public void testFollowHTMLRedirects() throws Exception
    {
        Blog source = new Blog();

        // Discover
        HeadDataDiscoverer dis = new HeadDataDiscoverer();
        dis.add("http://thejoyofflex.com", data("thejoyofflex_com.html"));
        dis.add("http://www.colettas.org", data("colettas_org.html"));

        int fields = dis.discover(source, new URL("http://thejoyofflex.com"));

        // Check
        assertEquals(IBlogDiscoverer.FIELD_DATA_URL, fields);
        assertEquals("http://www.colettas.org/?feed=rss2", source.getDataUrl());
    }

    /**
     * Tests detection of the HTML redirection with the content URL on the left.
     *
     * @throws IOException I/O error.
     */
    public void testDetectRedirectionURL_FoundLeft() throws IOException
    {
        assertEquals("http://www.colettas.org",
            HtmlDiscoverer.detectHTMLRedirectionURL(data("thejoyofflex_com_left.html")));
    }

    /**
     * Tests detection of the HTML redirection with the content URL on the right.
     *
     * @throws IOException I/O error.
     */
    public void testDetectRedirectionURL_FoundRight() throws IOException
    {
        assertEquals("http://www.colettas.org",
            HtmlDiscoverer.detectHTMLRedirectionURL(data("thejoyofflex_com.html")));
    }

    /**
     * Tests handing when no redirection present.
     *
     * @throws IOException I/O error.
     */
    public void testDetectRedirectionURL_NotFound()  throws IOException
    {
        assertNull(HtmlDiscoverer.detectHTMLRedirectionURL(data("colettas_org.html")));
    }

    /**
     * Returns the test data file URL.
     *
     * @param filename file name.
     *
     * @return URL.
     */
    private URL dataURL(String filename)
    {
        return this.getClass().getClassLoader().getResource("data/" + filename);
    }

    /**
     * Returns the test data file contents.
     *
     * @param filename file name.
     *
     * @return contents.
     *
     * @throws IOException I/O error.
     */
    private String data(String filename) throws IOException
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("data/" + filename);
        int size = is.available();
        byte[] buf = new byte[size];
        is.read(buf, 0, size);
        return new String(buf);
    }
    /**
     * Custom URL to head mapping discoverer.
     */
    private class HeadDataDiscoverer extends HtmlDiscoverer
    {
        private Map<String, String> urlToHead = new HashMap<String, String>();

        /**
         * Adds a mapping between an URL and a head.
         *
         * @param url   url.
         * @param head  head.
         */
        public void add(String url, String head)
        {
            urlToHead.put(url, head);
        }

        @Override
        protected String getHeadData(URL url) throws IOException {
            return url == null ? null : urlToHead.get(url.toString());
        }
    }
}
