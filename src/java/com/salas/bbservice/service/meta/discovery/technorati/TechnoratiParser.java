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
// $Id: TechnoratiParser.java,v 1.1.1.1 2006/10/23 13:55:46 alg Exp $
//

package com.salas.bbservice.service.meta.discovery.technorati;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.nava.informa.utils.NoOpEntityResolver;
import com.salas.bbservice.utils.StringUtils;

/**
 * Parser of Technorati (www.technorati.com) response.
 */
public class TechnoratiParser
{
    /**
     * Parses XML given as input and returns outline entry.
     *
     * @param xml xml data from technorati.
     *
     * @return response: outline or error.
     *
     * @throws TechnoratiParserException if format is invalid or this is
     *                                   error response data.
     */
    public TechnoratiResponse parse(String xml) throws TechnoratiParserException
    {

        final SAXBuilder builder = new SAXBuilder(false);

        // Turn off DTD loading
        builder.setEntityResolver(new NoOpEntityResolver());
        Document doc;

        TechnoratiResponse response;
        try
        {
            doc = builder.build(new StringReader(xml));
            response = parseRoot(doc.getRootElement());
        } catch (JDOMException e)
        {
            if (!xml.endsWith("</tapi>"))
            {
                throw new TechnoratiParserException("Data is currupted or non-XML format.");
            } else
            {
                // If there's a problem parsing the response, report the error
                response = new TechnoratiError("Broken XML", false);
            }
        } catch (IOException e)
        {
            throw new TechnoratiParserException("Error reading data.");
        }

        return response;
    }

    /**
     * Parses root element of the outline.
     *
     * @param tapiE root element.
     *
     * @return response: outline or error.
     *
     * @throws TechnoratiParserException if format is incorrect or error message.
     */
    private TechnoratiResponse parseRoot(Element tapiE) throws TechnoratiParserException
    {
        final Element documentE = getMandatoryChildElement(tapiE, "document");
        final Element resultE = getMandatoryChildElement(documentE, "result");

        return parseResult(resultE);
    }

    /**
     * Parses result element. If result is error exception is thrown.
     * If it normal outline then it gets parsed.
     *
     * @param resultE result element.
     *
     * @return response: outline or error.
     *
     * @throws TechnoratiParserException in case of error response or invalid format.
     */
    TechnoratiResponse parseResult(Element resultE) throws TechnoratiParserException
    {
        // check if we have error packet
        final Element errorE = resultE.getChild("error");
        if (errorE != null)
        {
            return parseError(errorE);
        }

        // not an error packer - parse outline
        return parseOutline(resultE);
    }

    /**
     * Parses valid data outline.
     *
     * @param resultE result element with outline.
     *
     * @return outline object.
     *
     * @throws TechnoratiParserException if format of elements is incorrect.
     */
    TechnoratiResult parseOutline(Element resultE) throws TechnoratiParserException
    {

        TechnoratiResult o = new TechnoratiResult();
        o.setUrl(resultE.getChildTextTrim("url"));
        o.setInboundBlogs(convertToInteger(resultE.getChildTextTrim("inboundblogs")));
        o.setInboundLinks(convertToInteger(resultE.getChildTextTrim("inboundlinks")));

        List weblogEL = resultE.getChildren("weblog");
        for (int i = 0; i < weblogEL.size(); i++)
        {
            Element weblogE = (Element)weblogEL.get(i);
            o.addWebLog(parseWeblog(weblogE));
        }

        return o;
    }

    /**
     * Parses weblog entry in results.
     *
     * @param weblogE weblog entry element.
     *
     * @return weblog object.
     *
     * @throws TechnoratiParserException if mandatory elements are not present or format is invalid.
     */
    TechnoratiResultWeblog parseWeblog(Element weblogE) throws TechnoratiParserException
    {
        TechnoratiResultWeblog w = new TechnoratiResultWeblog();
        w.setName(weblogE.getChildTextTrim("name"));
        w.setUrl(weblogE.getChildTextTrim("url"));
        w.setRssUrl(weblogE.getChildTextTrim("rssurl"));
        w.setAtomUrl(weblogE.getChildTextTrim("atomurl"));
        w.setInboundBlogs(convertToInt(weblogE.getChildTextTrim("inboundblogs"), "inboundblogs"));
        w.setInboundLinks(convertToInt(weblogE.getChildTextTrim("inboundlinks"), "inboundlinks"));
        w.setLastUpdate(convertToDate(weblogE.getChildTextTrim("lastupdate")));
        w.setRank(convertToInteger(weblogE.getChildTextTrim("rank")));
        w.setLang(convertToInteger(weblogE.getChildTextTrim("lang")));

        return w;
    }

    /**
     * Converts string to date. Parser accepts date in the following format:
     * <em>yyyy-MM-dd HH:mm:ss z</em>, corresponds to
     * <em>2004-08-11 13:51:46 GMT</em>.
     *
     * @param value string value.
     *
     * @return date object or NULL if source value was NULL.
     *
     * @throws TechnoratiParserException if format is invalid.
     */
    static Date convertToDate(String value) throws TechnoratiParserException
    {

        Date date = null;
        try
        {
            if (value != null)
            {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse(value);
            }
        } catch (Exception e)
        {
            throw new TechnoratiParserException("Invalid format of data.");
        }

        return date;
    }

    /**
     * Converts string value to integer. Value is mandatory and if it's not
     * present (NULL) or non-integer format then format violation exception
     * will be thrown.
     *
     * @param value source value for conversion.
     * @param key   name of the key for error details.
     *
     * @return converted value.
     *
     * @throws TechnoratiParserException in case if mandatory value is not
     *                                   present or non-integer format.
     */
    static int convertToInt(String value, String key) throws TechnoratiParserException
    {
        return convertToInt(value, key, 0);
    }

    /**
     * Converts string value to integer.
     *
     * @param value source value for conversion.
     * @param key   name of the key for error details.
     * @param def   default value for the case when the value isn't present.
     *
     * @return converted value.
     *
     * @throws TechnoratiParserException in case if mandatory value is not
     *                                   present or non-integer format.
     */
    static int convertToInt(String value, String key, int def) throws TechnoratiParserException
    {
        if (StringUtils.isEmpty(value)) return def;

        try
        {
            def = Integer.parseInt(value);
        } catch (Exception e)
        {
            throw new TechnoratiParserException("Invalid format of data for " + key);
        }

        return def;
    }

    /**
     * Converts string representation of integer to Integer object.
     * If source value is NULL then result also be NULL. If format of the value
     * is not Integer then result also be NULL.
     *
     * @param value value to convert.
     *
     * @return integer object or NULL.
     */
    static Integer convertToInteger(String value)
    {
        Integer result = null;

        if (value != null)
        {
            try
            {
                result = new Integer(Integer.parseInt(value));
            } catch (Exception e)
            {
                result = null;
            }
        }

        return result;
    }

    /**
     * Parses error packet.
     *
     * @param errorE error element.
     */
    TechnoratiError parseError(Element errorE)
    {
        return new TechnoratiError(errorE.getTextTrim());
    }

    /**
     * Returns the child element.
     *
     * @param parent    parent element.
     * @param childName name of child element.
     *
     * @return element.
     *
     * @throws TechnoratiParserException if child element not found.
     */
    Element getMandatoryChildElement(Element parent, String childName)
        throws TechnoratiParserException
    {

        final Element child = parent.getChild(childName);
        if (child == null)
        {
            throw new TechnoratiParserException("Invalid format of data.");
        }

        return child;
    }
}
