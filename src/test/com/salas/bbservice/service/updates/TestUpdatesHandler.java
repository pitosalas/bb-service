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
// mailto:pitosalas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: TestUpdatesHandler.java,v 1.1.1.1 2006/10/23 13:56:03 alg Exp $
//

package com.salas.bbservice.service.updates;

import com.salas.bbservice.domain.Version;
import com.salas.bbservice.domain.VersionChange;
import junit.framework.TestCase;
import org.jdom.Document;
import org.jdom.Element;

import java.util.Arrays;
import java.util.List;

/**
 * This suite contains tests for <code>UpdatesHandler</code> unit.
 */
public abstract class TestUpdatesHandler extends TestCase
{
    /**
     * Tests converting version change item to document element.
     */
    public void testConvertChangeToElement()
    {
        VersionChange change = createVersionChange(VersionChange.TYPE_FEATURE, "details");

        Element element = UpdatesHandler.convertChangeToElement(change);
        assertEquals(UpdatesHandler.EL_CHANGE, element.getName());
        assertEquals("Type isn't set.", Integer.toString(change.getType()),
            element.getAttributeValue(UpdatesHandler.EL_CHANGE_TYPE));
        assertEquals("Details aren't set.", change.getDetails(),
            element.getTextTrim());
    }

    private static VersionChange createVersionChange(int type, String details)
    {
        return new VersionChange(-1, type, details);
    }

    /**
     * Tests converting list of changes into document element.
     */
    public void testConvertChangesToElement()
    {
        VersionChange[] changes = new VersionChange[]
        {
            createVersionChange(VersionChange.TYPE_FEATURE, "0"),
            createVersionChange(VersionChange.TYPE_FIX, "1")
        };

        Element element = UpdatesHandler.convertChangesToElement(Arrays.asList(changes));
        assertEquals(UpdatesHandler.EL_CHANGES, element.getName());
        assertEquals(changes.length, element.getChildren(UpdatesHandler.EL_CHANGE).size());
    }

    /**
     * Tests building locations element.
     */
    public void testCreateLocationsElement()
    {
        Element element = UpdatesHandler.createLocationsElement("1.0");

        assertEquals(UpdatesHandler.EL_LOCATIONS, element.getName());
        List locationElements = element.getChildren(UpdatesHandler.EL_LOCATION);
        assertTrue(locationElements.size() > 0);
    }

    /**
     * Tests building response document for some recent version.
     */
    public void testVersionToResponse()
    {
        Version recentVersion = new Version("1.0", 1, true);
        VersionChange[] changes = new VersionChange[]
        {
            createVersionChange(VersionChange.TYPE_FEATURE, "0"),
            createVersionChange(VersionChange.TYPE_FIX, "1")
        };

        Document doc = UpdatesHandler.versionToResponse(recentVersion, Arrays.asList(changes));
        Element root = doc.getRootElement();
        assertEquals(UpdatesHandler.EL_RECENT_VERSION, root.getName());
        assertEquals("1.0", root.getChildTextTrim(UpdatesHandler.EL_VERSION));
        assertEquals("1", root.getChildTextTrim(UpdatesHandler.EL_RELEASE_TIME));
        assertNotNull(root.getChild(UpdatesHandler.EL_LOCATIONS));
        assertNotNull(root.getChild(UpdatesHandler.EL_CHANGES));
    }
}
