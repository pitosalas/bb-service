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
// $Id: StatsOutputHelper.java,v 1.1.1.1 2006/10/23 13:55:49 alg Exp $
//

package com.salas.bbservice.stats;

import com.salas.bbservice.utils.StringUtils;

/**
 * Helper class for generation of stats output.
 */
public final class StatsOutputHelper
{
    /**
     * Hidden utility class constructor.
     */
    private StatsOutputHelper()
    {
    }

    /**
     * Generates HTML table with header and enclosed in div with appropriate id.
     *
     * @param table         table info.
     * @param id            id for div.
     * @param title         title of table.
     * @param isInt         output integer values.
     * @param showMax       TRUE to show maximum marks.
     * @param showTotalsRow TRUE to show totals row.
     *
     * @return HTML code ready for insertion.
     */
    public static String generateStatsTable(StatsTable table, String id, String title,
                                            boolean isInt, boolean showMax, boolean showTotalsRow)
    {
        int[] maxIndexes = showMax ? table.getMaxIndexes() : null;
        double[] totalsRow = showTotalsRow ? table.getTotalsRow() : null;

        StringBuffer buf = new StringBuffer();

        buf.append("<div class='data' id='").append(id).append("'>");

        buf.append("<table class='datatable'>");

        buf.append("<thead><td colspan=\"").append(table.getColumnsCount()).append("\">");
        buf.append(title).append("</td></thead>");

        // header row
        buf.append("<tr class=\"subheader\">");
        for (int i = 0; i < table.getColumnsCount(); i++)
        {
            buf.append("<td>").append(table.getColumnTitle(i)).append("</td>");
        }
        buf.append("</tr>");

        // data rows
        final int rowsCount = table.getRowsCount();
        final int columnsCount = table.getColumnsCount() - 1;
        for (int r = 0; r < rowsCount; r++)
        {
            buf.append("<tr>");
            buf.append("<td class='rowheader'>").append(table.getRowTitle(r)).append("</td>");

            for (int c = 0; c < columnsCount; c++)
            {
                final double data = table.getData(c, r);
                buf.append("<td");
                if (showMax && maxIndexes[c] == r) buf.append(" class='max'");
                buf.append(">");
                buf.append(isInt ? Integer.toString((int)data) : Double.toString(data));
                buf.append("</td>");
            }
            buf.append("</tr>");
        }

        if (showTotalsRow)
        {
            buf.append("<tr class='totals'>");
            buf.append("<td class='rowheader'>Total</td>");

            for (int c = 0; c < columnsCount; c++)
            {
                buf.append("<td>");
                buf.append(isInt ? Integer.toString((int)totalsRow[c])
                    : Double.toString(totalsRow[c]));
                buf.append("</td>");
            }

            buf.append("</tr>");
        }
        buf.append("</table>");

        buf.append("</div>");

        return buf.toString();
    }

    /**
     * Generates CSV table.
     *
     * @param table         table info.
     * @param title         title of table.
     * @param isInt         output integer values.
     * @param showTotalsRow TRUE to show totals row.
     *
     * @return CSV ready for output.
     */
    public static String generateCSV(StatsTable table, String title, boolean isInt,
                                     boolean showTotalsRow)
    {
        double[] totalsRow = showTotalsRow ? table.getTotalsRow() : null;

        StringBuffer buf = new StringBuffer();

        // header row
        outputCSVCell(buf, table.getColumnTitle(0));
        for (int i = 1; i < table.getColumnsCount(); i++)
        {
            buf.append(",");
            outputCSVCell(buf, table.getColumnTitle(1));
        }
        buf.append("\n");

        // data rows
        final int rowsCount = table.getRowsCount();
        final int columnsCount = table.getColumnsCount() - 1;
        for (int r = 0; r < rowsCount; r++)
        {
            outputCSVCell(buf, table.getRowTitle(r));

            for (int c = 0; c < columnsCount; c++)
            {
                buf.append(",");
                outputCSVCell(buf, table.getData(c, r), isInt);
            }

            buf.append("\n");
        }

        if (showTotalsRow)
        {
            outputCSVCell(buf, "Total:");
            for (int c = 0; c < columnsCount; c++)
            {
                buf.append(",");
                outputCSVCell(buf, totalsRow[c], isInt);
            }

            buf.append("\n");
        }
        buf.append("\n");

        return buf.toString();
    }

    private static void outputCSVCell(StringBuffer buffer, double value, boolean asInt)
    {
        outputCSVCell(buffer, asInt ? Integer.toString((int)value) : Double.toString(value));
    }

    private static void outputCSVCell(StringBuffer buffer, String value)
    {
        buffer.append("\"").append(StringUtils.escapeForCDL(value)).append("\"");
    }
}
