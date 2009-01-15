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
// $Id: UpdatesHandler.java,v 1.1.1.1 2006/10/23 13:55:48 alg Exp $
//

package com.salas.bbservice.service.updates;

import com.salas.bbservice.persistence.IVersionsDao;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.domain.Version;
import com.salas.bbservice.domain.VersionChange;
import com.salas.bbservice.utils.Configuration;
import com.salas.bbutilities.opml.utils.Transformation;
import com.salas.bbutilities.VersionUtils;

import java.util.List;
import java.io.File;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Handler of requests for updates.
 *
 * Client can ask service to itemize all updates happened to application
 * since given version. If service has some information on that, it lists
 * the updates in this form:
 * <pre>
 * &lt;recentVersion&gt;
 *   &lt;version&gt;3.0&lt;/version&gt;
 *   &lt;releaseTime&gt;1234567890&lt;/releaseTime&gt;
 *   &lt;locations&gt;
 *     &lt;location type="debian" size="12345" description="Debian Linux" path="http://.../blogbirdge-3.0.deb"/&gt;
 *     &lt;location type="gentoo" size="..." description="..." path="http://.../blogbirdge-3.0.tgz"/&gt;
 *     &lt;location type="tgz" size="..." description="..." path="http://.../blogbirdge-3.0.tgz"/&gt;
 *     &lt;location type="windows" size="..." description="..." path="http://.../blogbirdge-3.0.exe"/&gt;
 *     &lt;location type="mac" size="..." description="..." path="http://.../blogbirdge-3.0.dmg"/&gt;
 *   &lt;/locations&gt;
 *   &lt;changes&gt;
 *     &lt;change type="0"&gt;&lt;![CDATA[This is new feature]]&lt;/change&gt;
 *     &lt;change type="1"&gt;&lt;![CDATA[This is bug fix]]&lt;/change&gt;
 *   &lt;/changes&gt;
 * &lt;/recentVersion&gt;
 * </pre>
 *
 * When there are no new releases the empty string will be returned.
 */
public final class UpdatesHandler
{
    static final String BASE_DEPLOY_URL         = Configuration.getDeployURL();
    static final String BASE_DEPLOY_PATH        = Configuration.getDeployPath();

    static final String EL_RECENT_VERSION       = "recentVersion";
    static final String EL_RELEASE_TIME         = "releaseTime";
    static final String EL_VERSION              = "version";

    static final String EL_LOCATIONS            = "locations";
    static final String EL_LOCATION             = "location";
    static final String EL_LOCATION_PATH        = "path";
    static final String EL_LOCATION_TYPE        = "type";
    static final String EL_LOCATION_DESCRIPTION = "description";
    static final String EL_LOCATION_SIZE        = "size";

    static final String EL_CHANGE               = "change";
    static final String EL_CHANGE_TYPE          = "type";
    static final String EL_CHANGES              = "changes";

    /**
     * Returns XML with description of updates since given version.
     *
     * @param version current client version.
     *
     * @return XML with updates.
     */
    public String checkForUpdates(String version)
    {
        return checkForUpdates(version, true);
    }

    /**
     * Returns XML with description of updates since given version.
     *
     * @param version current client version.
     * @param productionOnly report updates between production versions only.
     *
     * @return XML with updates.
     */
    public String checkForUpdates(String version, boolean productionOnly)
    {
        String response = "";

        if (version.indexOf('.') != -1)
        {
            IVersionsDao dao = (IVersionsDao)DaoConfig.getDao(IVersionsDao.class);
            Version recentVersion = dao.getRecentVersion(productionOnly);

            // If user's version less than recent version we continue
            if (recentVersion != null &&
                VersionUtils.versionCompare(version, recentVersion.getVersion()) < 0)
            {
                // Get changs from current version to recent version.
                // If current version isn't found then get changes only for recent version.
                List changes = dao.listChangesFrom(version, productionOnly);
                if (changes == null) changes = dao.listChangesOfVersion(recentVersion.getId());

                Document document = versionToResponse(recentVersion, changes);
                if (document != null) response = Transformation.documentToString(document);
            }
        }

        return response;
    }

    /**
     * Converts version to response object.
     *
     * @param aRecentVersion    version information.
     * @param aChanges          list of changes to this version.
     *
     * @return XML response or <code>NULL</code> if release has no files.
     */
    static Document versionToResponse(Version aRecentVersion, List aChanges)
    {
        Document doc = null;

        Element locationsElement = createLocationsElement(aRecentVersion.getVersion());
        if (locationsElement != null)
        {
            Element root = new Element(EL_RECENT_VERSION);
            doc = new Document(root);

            Element versionElement = new Element(EL_VERSION);
            versionElement.setText(aRecentVersion.getVersion());
            root.addContent(versionElement);

            Element releaseTimeElement = new Element(EL_RELEASE_TIME);
            releaseTimeElement.setText(Long.toString(aRecentVersion.getReleaseTime()));
            root.addContent(releaseTimeElement);

            root.addContent(locationsElement);
            root.addContent(convertChangesToElement(aChanges));
        }

        return doc;
    }

    /**
     * Creates locations map for given version.
     *
     * @param aVersion  version.
     *
     * @param aVersion
     * @return initialized locations element.
     */
    static Element createLocationsElement(String aVersion)
    {
        Element locations = new Element(EL_LOCATIONS);
        addLocation(locations, aVersion, "debian", "Debian and Ubuntu Linux", "deb");
        addLocation(locations, aVersion, "gentoo", "Gentoo Linux", "tgz");
        addLocation(locations, aVersion, "tgz", "Tar.gz Archive", "tgz");
        addLocation(locations, aVersion, "windows", "Windows Installer", "exe");
        addLocation(locations, aVersion, "mac", "Mac OS X Installer", "dmg");

        return locations.getChildren(EL_LOCATION).size() == 0 ? null : locations;
    }

    /**
     * Adds location link to locations element if only the file is in release directory.
     *
     * @param locations     locations element.
     * @param aVersion      target version.
     * @param type          type of file.
     * @param description   description of type.
     * @param extension     file extension.
     */
    static void addLocation(Element locations, String aVersion, String type, String description,
        String extension)
    {
        String filename = "blogbridge-" + aVersion + "." + extension;
        String filepath = BASE_DEPLOY_PATH + "/" + filename;
        File file = new File(filepath);

        if (file.exists())
        {
            String filelink = BASE_DEPLOY_URL + "/" + filename;

            Element location = new Element(EL_LOCATION);
            location.setAttribute(EL_LOCATION_TYPE, type);
            location.setAttribute(EL_LOCATION_DESCRIPTION, description);
            location.setAttribute(EL_LOCATION_PATH, filelink);
            location.setAttribute(EL_LOCATION_SIZE, Long.toString(file.length()));

            locations.addContent(location);
        }
    }

    /**
     * Converts list of version change items into element.
     *
     * @param aChanges changes.
     *
     * @return element.
     */
    static Element convertChangesToElement(List aChanges)
    {
        Element changes = new Element(EL_CHANGES);

        for (int i = 0; i < aChanges.size(); i++)
        {
            VersionChange change = (VersionChange)aChanges.get(i);
            changes.addContent(convertChangeToElement(change));
        }

        return changes;
    }

    /**
     * Converts single version change item into element.
     *
     * @param aChange version change item.
     *
     * @return element.
     */
    static Element convertChangeToElement(VersionChange aChange)
    {
        Element change = new Element(EL_CHANGE);
        change.setAttribute(EL_LOCATION_TYPE, Integer.toString(aChange.getType()));
        change.setText(aChange.getDetails());

        return change;
    }
}
