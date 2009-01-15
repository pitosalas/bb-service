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
// $Id: TestTechnoratiDiscoverer.java,v 1.1.1.1 2006/10/23 13:56:02 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import junit.framework.TestCase;
import com.salas.bbservice.service.meta.discovery.technorati.TechnoratiParser;
import com.salas.bbservice.service.meta.discovery.technorati.TechnoratiParserException;
import com.salas.bbservice.service.meta.discovery.technorati.TechnoratiResult;
import com.salas.bbservice.service.meta.discovery.technorati.TechnoratiError;
import com.salas.bbservice.domain.Blog;

/**
 * @see TechnoratiDiscoverer
 */
public class TestTechnoratiDiscoverer extends TestCase
{
    /**
     * Tests parsing of response.
     */
    public void testParsingOfResponse() throws TechnoratiParserException
    {
        Blog b = new Blog();
        TechnoratiParser tp = new TechnoratiParser();
        TechnoratiDiscoverer td = new TechnoratiDiscoverer();
        assertEquals(7, td.parseTechnoratiResult((TechnoratiResult)tp.parse(getXml()), b));
        assertEquals(3576, b.getRank());
    }

    /**
     * Tests parsing of response.
     */
    public void testParsingOfResponse2() throws TechnoratiParserException
    {
        Blog b = new Blog();
        TechnoratiParser tp = new TechnoratiParser();
        TechnoratiDiscoverer td = new TechnoratiDiscoverer();
        assertEquals(4, td.parseTechnoratiResult((TechnoratiResult)tp.parse(getXml2()), b));
        assertEquals(Blog.UNKNOWN, b.getRank());
    }

    /**
     * Tests parsing of response.
     */
    public void testParsingOfResponse3() throws TechnoratiParserException
    {
        Blog b = new Blog();
        TechnoratiParser tp = new TechnoratiParser();
        TechnoratiDiscoverer td = new TechnoratiDiscoverer();
        assertTrue(tp.parse(getXml3()) instanceof TechnoratiError);
    }

    private String getXml3()
    {
        return
            "<!-- generator=\"Technorati API version 1.0 /cosmos\" -->\n" +
            "<tapi version=\"1.0\">\n" +
            "<document>\n" +
            "\t<result>\n" +
            "\t\t<error>\n" +
            "\t\t\tCould not connect to database. Please try again later.\n" +
            "\t\t</error>\n" +
            "\t</result>\n" +
            "</document>\n" +
            "</tapi>";
    }

    private String getXml2()
    {
        return
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<!-- generator=\"Technorati API version 1.0 /cosmos\" -->\n" +
            "<!DOCTYPE tapi PUBLIC \"-//Technorati, Inc.//DTD TAPI 0.02//EN\" \"http://api.technorati.com/dtd/tapi-002.xml\">\n" +
            "<tapi version=\"1.0\">\n" +
            "<document>\n" +
            "<result>\n" +
            "  <url>http://localhost/b2evolution/blogs/xmlsrv/atom.php?blog=8</url>\n" +
            "  <inboundblogs>0</inboundblogs>\n" +
            "  <inboundlinks>0</inboundlinks>\n" +
            "  <rankingstart>1</rankingstart>\n" +
            "\n" +
            "</result>\n" +
            "</document>\n" +
            "</tapi>";
    }

    private String getXml()
    {
        return
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<!-- generator=\"Technorati API version 1.0 /cosmos\" -->\n" +
            "<!DOCTYPE tapi PUBLIC \"-//Technorati, Inc.//DTD TAPI 0.02//EN\" \"http://api.technorati.com/dtd/tapi-002.xml\">\n" +
            "<tapi version=\"1.0\">\n" +
            "<document>\n" +
            "<result>\n" +
            "  <url>http://blogs.salon.com/0001330</url>\n" +
            "  <weblog>\n" +
            "    <name>The Devil's Excrement</name>\n" +
            "    <url>http://blogs.salon.com/0001330</url>\n" +
            "    <rssurl>http://blogs.salon.com/0001330/rss.xml</rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>88</inboundblogs>\n" +
            "    <inboundlinks>120</inboundlinks>\n" +
            "    <lastupdate>2004-11-11 03:40:56 GMT</lastupdate>\n" +
            "    <rank>3576</rank>\n" +
            "  </weblog>\n" +
            "  <inboundblogs>88</inboundblogs>\n" +
            "  <inboundlinks>120</inboundlinks>\n" +
            "  <rankingstart>1</rankingstart>\n" +
            "</result>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Zumba2</name>\n" +
            "    <url>http://josero68.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>0</inboundblogs>\n" +
            "    <inboundlinks>0</inboundlinks>\n" +
            "    <lastupdate>2004-11-09 21:05:53 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink>http://josero68.blogspot.com/2004/11/good-bye-democracy-au-revoir.html</nearestpermalink>\n" +
            "  <excerpt>No, I'm not talking about the US, you might think. No, I'm talking about my homeland, Venezuela. The issue is way to extense to really go on and on, but there is plenty information here: Nueva Venezuela, and here Daniel's Blog, and here: The Devil's Excrement .    I wont dwell into this aweful and horrendous realm of what it's going on down there, I just pointing out and trying to vent a well rooted frustration.    Democracy, as a way o govern a country, my country of Birth, is gone. we're witnessing, what</excerpt>\n" +
            "  <linkcreated>2004-11-09 14:44:16 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Winds of Change.NET</name>\n" +
            "    <url>http://www.windsofchange.net</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>1391</inboundblogs>\n" +
            "    <inboundlinks>1935</inboundlinks>\n" +
            "    <lastupdate>2004-11-09 08:44:09 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt>opposition in regional elections last month. The net effect is that President Chávez's supporters now control 85% of the mayor's offices, 87% of the governor's offices and congress.   While Miguel Octávio, the author of the Venezuelan blog,' The Devil's Excrement  observes that the abstentions were the same level as in the 2000/2001 elections the effect of boycotting an election seems to remind me of the old saying about urinating while wearing a blue serge suit: it leaves you with a warm feeling and nobody</excerpt>\n" +
            "  <linkcreated>2004-11-09 03:48:02 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Winds of Change.NET</name>\n" +
            "    <url>http://www.windsofchange.net</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>1391</inboundblogs>\n" +
            "    <inboundlinks>1935</inboundlinks>\n" +
            "    <lastupdate>2004-11-09 08:44:11 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt>vez's supporters now control 85% of the mayor's offices, 87% of the governor's offices and congress.   While Miguel Octávio, the author of the Venezuelan blog, The Devil's Excrement observes that the' abstentions were the same level  as in the 2000/2001 elections the effect of boycotting an election seems to remind me of the old saying about urinating while wearing a blue serge suit: it leaves you with a warm feeling and nobody notices. The net effect is that Ch</excerpt>\n" +
            "  <linkcreated>2004-11-09 03:48:02 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/2004/11/02.html#a1871</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Topochoblog</name>\n" +
            "    <url>http://www.encaletado.com/topocho</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>33</inboundblogs>\n" +
            "    <inboundlinks>49</inboundlinks>\n" +
            "    <lastupdate>2004-11-09 03:30:34 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt> Flavor-ritos:  Caracas Chronicles EDaPina DTUP egg Blog fran en gotas IMAKINARIA infelix Maléfica Marcel Pedro Jorge Romero pulsoneuronal Superdharma' The Devil's Excrement  urbanoshopy.com The Rover's Trail Venezuela News And Views</excerpt>\n" +
            "  <linkcreated>2004-11-09 00:28:01 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Common Sense and Wonder</name>\n" +
            "    <url>http://commonsensewonder.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>299</inboundblogs>\n" +
            "    <inboundlinks>332</inboundlinks>\n" +
            "    <lastupdate>2004-11-06 04:27:40 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt> The Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-11-05 22:13:56 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Pollenatrix</name>\n" +
            "    <url>http://offpollen.typepad.com/pollenatrix</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>0</inboundblogs>\n" +
            "    <inboundlinks></inboundlinks>\n" +
            "    <lastupdate>2004-11-05 05:41:21 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink>http://offpollen.typepad.com/pollenatrix/2004/10/blog_find.html</nearestpermalink>\n" +
            "  <excerpt>By the way, here are a couple of my evening blog finds:   Horticultural, a personal blog written by an editor of the UK newspaper, The Guardian.   Garden + Living, nice pictures, good writing, but no rss feed... booo  And my all time favourite, Devil's Excrement . To quote the tag line, &quot;In Spanish one word for orchid collector is quite fitting: &quot;orquidiota&quot; or &quot;orchididiot&quot; in English. I confess being one.&quot;</excerpt>\n" +
            "  <linkcreated>2004-11-04 21:41:11 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/categories/orchids/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Romulo Lopez Cordero</name>\n" +
            "    <url>http://www.romulolopez.com/romax</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>1</inboundblogs>\n" +
            "    <inboundlinks>1</inboundlinks>\n" +
            "    <lastupdate>2004-11-01 23:13:14 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink>http://www.romulolopez.com/romax/2004/11/1984_in_venezue.html</nearestpermalink>\n" +
            "  <excerpt> elections. The opposition not even won the states in which they were popular. Big Brother has arrived to Venezuela and it seems that is going to stay in charge for a long time indeed. Check the Blog of the Devil's Excrement  for news on the current election.</excerpt>\n" +
            "  <linkcreated>2004-11-01 14:57:01 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>El Liberal Venezolano (La Bitácora)'</name>\n" +
            "    <url>http://libertario-venezolano.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>0</inboundblogs>\n" +
            "    <inboundlinks>0</inboundlinks>\n" +
            "    <lastupdate>2004-10-26 16:31:18 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt> The Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-10-26 11:59:26 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Sharp Knife</name>\n" +
            "    <url>http://sharpknife.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>105</inboundblogs>\n" +
            "    <inboundlinks>124</inboundlinks>\n" +
            "    <lastupdate>2004-10-23 01:35:49 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt> Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-10-22 22:26:27 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>ALT1040 | Netcultura</name>\n" +
            "    <url>http://www.alt1040.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>275</inboundblogs>\n" +
            "    <inboundlinks>386</inboundlinks>\n" +
            "    <lastupdate>2004-10-21 17:19:19 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt> The Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-10-21 15:04:06 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Country Store</name>\n" +
            "    <url>http://countrystore.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>191</inboundblogs>\n" +
            "    <inboundlinks>216</inboundlinks>\n" +
            "    <lastupdate>2004-10-20 15:50:44 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt> Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-10-20 13:36:51 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Headspace</name>\n" +
            "    <url>http://esui.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>0</inboundblogs>\n" +
            "    <inboundlinks></inboundlinks>\n" +
            "    <lastupdate>2004-10-03 03:39:12 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt>[IMG My Photo]    Name:eileen chew     I'm in Singapore. I grow my orchids on the common corridor. I would love to hear from you. If you like orchids too, here are links to orchid blogs I enjoy reading: Devil's Excrement , OrchidSex and  O is for Orchids.     Other parts of my Headspace: Headspace: Blog  Headspace: Stories     View my complete profile</excerpt>\n" +
            "  <linkcreated>2004-10-03 02:41:21 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/categories/orchids/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Rat Spleen</name>\n" +
            "    <url>http://ratspleen.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>5</inboundblogs>\n" +
            "    <inboundlinks>5</inboundlinks>\n" +
            "    <lastupdate>2004-10-01 19:35:55 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt>Right-Thinking from the Left Coast the evangelical outpost Vox Popoli  for symptoms  How's it going? LewRockwell.com Ludwig von Mises Institute Mark R. Sizer's Blog National Review Online One Hand Clapping Reason The Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-10-01 17:22:00 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>CRONICAS ABSURDAS</name>\n" +
            "    <url>http://elawacate.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>0</inboundblogs>\n" +
            "    <inboundlinks></inboundlinks>\n" +
            "    <lastupdate>2004-09-29 22:55:02 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt>VENETHINKER....... +18   SUPERDHARMA   MULTIVAK  Topocho Blogger  MALEFICA  NOLO Carolina's Bolg SATURNO Fran en Gotas JR TUGUES La Mirada de Amiga Luna ALMAROSA MARCEL Cero Pangola SIKANDA CHIRIMENO The Devil's Excrement  INITAVAL El Especialista TAUFPATE Lia Chan ER!KA METALHEN ELIO GUEVARA</excerpt>\n" +
            "  <linkcreated>2004-09-29 22:15:55 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Caracas Chronicles</name>\n" +
            "    <url>http://caracaschronicles.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>56</inboundblogs>\n" +
            "    <inboundlinks>65</inboundlinks>\n" +
            "    <lastupdate>2004-09-29 15:57:41 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink>http://caracaschronicles.blogspot.com/2004/09/paper-trail-as-entelechy.html</nearestpermalink>\n" +
            "  <excerpt>the strange comings and goings, the dancing REP, the illegal voting center shifts, the voting center personel shifts, the bidirectional communications, the aborted hot-audits, the anomalous exit poll results, the dodgy &quot;randomness&quot; of the cold-audit, the non-binomial distribution of the vote in some states, the Benford Law anomalies,  etc. - the CNE has an answer: trust us, the vote had to be fair. After all, there was a paper trail...everybody knows that, even Lyse Doucet knows that...    Join a moderated debate on this post.</excerpt>\n" +
            "  <linkcreated>2004-09-29 12:17:13 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/2004/09/25.html#a1786</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Haiti Pundit</name>\n" +
            "    <url>http://www.garraud.net/haitipundit</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>5</inboundblogs>\n" +
            "    <inboundlinks>8</inboundlinks>\n" +
            "    <lastupdate>2004-09-28 20:05:48 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt> The Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-09-28 12:48:36 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/categories/venezuela/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>From the inside looking out</name>\n" +
            "    <url>http://fromtheinsidelookingout.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>43</inboundblogs>\n" +
            "    <inboundlinks>54</inboundlinks>\n" +
            "    <lastupdate>2004-09-18 06:28:29 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt> The Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-09-18 01:20:02 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>..::Roms' Blog::.. ::..Los Libros Malditos..::</name>\n" +
            "    <url>http://themixedtape.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>0</inboundblogs>\n" +
            "    <inboundlinks>0</inboundlinks>\n" +
            "    <lastupdate>2004-09-18 03:16:41 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt>  Blogs   Fred's Blog  Veneblogs  venethinker  Coolio's  taufpate  impulsos y sentidos The Devil's Excrement   fasttagabs qtpd</excerpt>\n" +
            "  <linkcreated>2004-09-18 00:39:34 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>Ben Kepple</name>\n" +
            "    <url>http://www.benkepple.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>71</inboundblogs>\n" +
            "    <inboundlinks>84</inboundlinks>\n" +
            "    <lastupdate>2004-09-14 04:32:52 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt>NK Zone  Korea Watch   BSE 30  Suman Palit   TA-100  A. Kaplan Sommer   XETRA-DAX  Medienkritik   CAC-40  No Pasaran!   BEL-20  Live from Brussels   JSE Sec. Exch.  Commentary.co.za   Venezuela Electronic The Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-09-14 01:21:49 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330/</linkurl>\n" +
            "</item>\n" +
            "<item>\n" +
            "  <weblog>\n" +
            "    <name>venepoetics</name>\n" +
            "    <url>http://venepoetics.blogspot.com</url>\n" +
            "    <rssurl></rssurl>\n" +
            "    <atomurl></atomurl>\n" +
            "    <inboundblogs>72</inboundblogs>\n" +
            "    <inboundlinks>77</inboundlinks>\n" +
            "    <lastupdate>2004-09-11 01:20:14 GMT</lastupdate>\n" +
            "  </weblog>\n" +
            "  <nearestpermalink></nearestpermalink>\n" +
            "  <excerpt>Venezuela links  El Nacional El Universal TalCual El Meollo Venezuela Analítica  Descifrado El Nuevo Cojo Ilustrado Kalathos Exceso Fondo Editorial Pequeña Venecia Angria Ediciones La Liebre Libre Trama' The Devil's Excrement </excerpt>\n" +
            "  <linkcreated>2004-09-10 21:39:23 GMT</linkcreated>\n" +
            "  <linkurl>http://blogs.salon.com/0001330</linkurl>\n" +
            "</item>\n" +
            "</document>\n" +
            "</tapi>\n" +
            "";
    }
}
