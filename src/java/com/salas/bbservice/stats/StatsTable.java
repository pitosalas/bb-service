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
// $Id: StatsTable.java,v 1.1.1.1 2006/10/23 13:55:50 alg Exp $
//

package com.salas.bbservice.stats;

/**
 * Statistical information presented as table.
 */
public class StatsTable
{
    private String[] columnTitles;
    private String[] rowTitles;
    private double[][] data;

    private int[] maxIndexes = null;
    private double[] totalsRow = null;

    /**
     * Constructs statistics table.
     *
     * @param columnTitles  titles of columns.
     * @param rowTitles     titles of rows.
     * @param data          data for cells.
     */
    public StatsTable(String[] columnTitles, String[] rowTitles, double[][] data)
    {
        this.columnTitles = columnTitles;
        this.rowTitles = rowTitles;
        this.data = data;
    }

    /**
     * Returns number of columns.
     *
     * @return count.
     */
    public int getColumnsCount()
    {
        return columnTitles.length;
    }

    /**
     * Returns number of rows.
     *
     * @return count.
     */
    public int getRowsCount()
    {
        return rowTitles.length;
    }

    /**
     * Returns title of specified column.
     *
     * @param i index.
     *
     * @return column title.
     */
    public String getColumnTitle(int i)
    {
        return columnTitles[i];
    }

    /**
     * Returns title of specified row.
     *
     * @param i index.
     *
     * @return row title.
     */
    public String getRowTitle(int i)
    {
        return rowTitles[i];
    }

    /**
     * Returns data for the cell.
     *
     * @param c column index.
     * @param r row index.
     *
     * @return cell data.
     */
    public double getData(int c, int r)
    {
        return data[r][c];
    }

    /**
     * Returns the list of indexes of rows with maximum values across the columns.
     *
     * @return list of maximum indexes.
     */
    public int[] getMaxIndexes()
    {
        if (maxIndexes == null) calcMaxIndexes();

        return maxIndexes;
    }

    /**
     * Returns row of totals.
     *
     * @return totals.
     */
    public double[] getTotalsRow()
    {
        if (totalsRow == null) calcTotalsRow();

        return totalsRow;
    }

    /**
     * Calculates totals.
     */
    private void calcTotalsRow()
    {
        final int columnsCount = getColumnsCount() - 1;
        final int rowsCount = getRowsCount();

        totalsRow = new double[columnsCount];
        for (int c = 0; c < columnsCount; c++)
        {
            totalsRow[c] = 0;
            for (int r = 0; r < rowsCount; r++)
            {
                totalsRow[c] += getData(c, r);
            }
        }
    }

    /**
     * Calculates maximum indexes in columns.
     */
    private void calcMaxIndexes()
    {
        final int columnsCount = getColumnsCount() - 1;
        final int rowsCount = getRowsCount();

        maxIndexes = new int[columnsCount];
        for (int c = 0; c < columnsCount; c++)
        {
            maxIndexes[c] = 0;
            double m = 0;
            for (int r = 0; r < rowsCount; r++)
            {
                if (m < getData(c, r))
                {
                    m = getData(c, r);
                    maxIndexes[c] = r;
                }
            }
        }
    }

}
