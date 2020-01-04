/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright @2019 Jerome Lelasseux. All rights reserved.
 *
 * This file is part of the JJazzLab-X software.
 *
 * JJazzLab-X is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License (LGPLv3) 
 * as published by the Free Software Foundation, either version 3 of the License, 
 * or (at your option) any later version.
 *
 * JJazzLab-X is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JJazzLab-X.  If not, see <https://www.gnu.org/licenses/>
 *
 * Contributor(s): 
 *
 */
package org.jjazz.outputsynth.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import org.jjazz.midi.GM1Bank;
import org.jjazz.midi.GM1Instrument;
import org.jjazz.midi.Instrument;
import org.jjazz.midi.StdSynth;
import org.jjazz.outputsynth.GM1RemapTable;

public class RemapTableUIModel extends AbstractTableModel implements PropertyChangeListener
{

    public static final int ROW_DRUMS = 0;
    public static final int ROW_PERC = 1;
    public static final int ROW_VOICE_START = 2;
    public static final int COL_GM1_FAM = 0;
    public static final int COL_GM1_INS = 1;
    public static final int COL_MAP_INS = 2;
    public static final int COL_MAP_INS_FAM = 3;
    private HashMap<GM1Bank.Family, Integer> mapFamilyFirstRow = new HashMap<>();
    private HashMap<GM1Bank.Family, Integer> mapFamilyCenterRow = new HashMap<>();
    private HashMap<GM1Bank.Family, Integer> mapFamilyLastRow = new HashMap<>();
    private GM1RemapTable remapTable = null;
    private static final Logger LOGGER = Logger.getLogger(RemapTableUIModel.class.getSimpleName());

    public RemapTableUIModel()
    {
        // Precalculate the first/last row per family
        GM1Bank gm1Bank = StdSynth.getGM1Bank();
        GM1Bank.Family prevFamily = null;
        for (int i = 0; i < gm1Bank.getSize(); i++)
        {
            GM1Bank.Family family = gm1Bank.getInstrument(i).getFamily();
            if (!family.equals(prevFamily))
            {
                mapFamilyFirstRow.put(family, i + ROW_VOICE_START);
                if (prevFamily != null)
                {
                    int rowLast = i - 1 + ROW_VOICE_START;
                    int rowFirst = mapFamilyFirstRow.get(prevFamily);
                    int rowCenter = (rowLast - rowFirst + 1) / 2 + rowFirst;
                    mapFamilyLastRow.put(prevFamily, rowLast);
                    mapFamilyCenterRow.put(prevFamily, rowCenter);
                }
            }
            prevFamily = family;
        }
        int rowLast = 127 + ROW_VOICE_START;
        int rowFirst = mapFamilyFirstRow.get(prevFamily);
        int rowCenter = (rowLast - rowFirst + 1) / 2 + rowFirst;
        mapFamilyLastRow.put(prevFamily, rowLast);
        mapFamilyCenterRow.put(prevFamily, rowCenter);
    }

    public void setPrimaryModel(GM1RemapTable model)
    {
        if (model == null)
        {
            throw new NullPointerException("model");
        }
        if (remapTable != null)
        {
            remapTable.removePropertyChangeListener(this);
        }
        this.remapTable = model;
        this.remapTable.addPropertyChangeListener(this);
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount()
    {
        return 128 + ROW_VOICE_START; // 128 + Drums + Percussion

    }

    @Override
    public int getColumnCount()
    {
        return 4;
    }

    @Override
    public String getColumnName(int col)
    {
        String res;
        switch (col)
        {
            case COL_GM1_FAM:
                res = "Family";
                break;
            case COL_GM1_INS:
                res = "GM1 Instrument";
                break;
            case COL_MAP_INS:
                res = "Remapped Instrument";
                break;
            case COL_MAP_INS_FAM:
                res = "Remapped Instrument";
                break;
            default:
                throw new IllegalArgumentException("col=" + col);
        }
        return res;
    }

    @Override
    public Class<?> getColumnClass(int col)
    {
        Class<?> res = Object.class;
        switch (col)
        {
            case COL_GM1_FAM:
                res = GM1Bank.Family.class;
                break;
            case COL_GM1_INS:
                res = Instrument.class;
                break;
            case COL_MAP_INS:
                res = Instrument.class;
                break;
            case COL_MAP_INS_FAM:
                res = Instrument.class;
                break;
            default:
                throw new IllegalArgumentException("col=" + col);
        }
        return res;
    }

    @Override
    public Object getValueAt(int row, int col)
    {
        Object res = null;
        if (remapTable == null)
        {
            return null;
        }
        switch (row)
        {
            case ROW_DRUMS:
                switch (col)
                {
                    case COL_GM1_FAM:
                        res = null;
                        break;
                    case COL_GM1_INS:
                        res = new Instrument(0, "Drums");
                        break;
                    case COL_MAP_INS:
                        res = remapTable.getDrumsInstrument();
                        break;
                    case COL_MAP_INS_FAM:
                        res = null;
                        break;
                    default:
                        throw new IllegalArgumentException("row=" + row + " col=" + col);
                }
                break;
            case ROW_PERC:
                switch (col)
                {
                    case COL_GM1_FAM:
                        res = null;
                        break;
                    case COL_GM1_INS:
                        res = new Instrument(0, "Percussion");
                        break;
                    case COL_MAP_INS:
                        res = remapTable.getPercussionInstrument();
                        break;
                    case COL_MAP_INS_FAM:
                        res = null;
                        break;
                    default:
                        throw new IllegalArgumentException("row=" + row + " col=" + col);
                }
                break;
            default:
                // GM1 voices
                GM1Bank.Family f = getFamily(row);
                int pc = row - ROW_VOICE_START;
                switch (col)
                {
                    case COL_GM1_FAM:
                        Integer rowCenter = mapFamilyCenterRow.get(f);
                        res = (row != rowCenter) ? null : f;
                        break;
                    case COL_GM1_INS:
                        res = StdSynth.getGM1Bank().getInstrument(pc);
                        break;
                    case COL_MAP_INS:
                        GM1Instrument insGM1 = StdSynth.getGM1Bank().getInstrument(pc);
                        res = remapTable.getInstrument(insGM1);
                        break;
                    case COL_MAP_INS_FAM:
                        rowCenter = mapFamilyCenterRow.get(f);
                        res = (row == rowCenter) ? null : remapTable.getInstrument(f);
                        break;
                    default:
                        throw new IllegalArgumentException("row=" + row + " col=" + col);
                }
        }
        // LOGGER.fine("getValueAt() row=" + row + " col" + col + " res=" + res);

        return res;
    }

    // =================================================================================
    // PropertyChangeListener methods
    // =================================================================================    
    @Override
    public void propertyChange(PropertyChangeEvent e)
    {
        switch (e.getPropertyName())
        {
            case "PROP_FAMILY":
                GM1Bank.Family f = (GM1Bank.Family) e.getOldValue();
                int row = mapFamilyCenterRow.get(f);
                fireTableCellUpdated(row, COL_MAP_INS_FAM);
                break;
            case "PROP_INSTRUMENT":
                GM1Instrument insGM1 = (GM1Instrument) e.getOldValue();
                fireTableCellUpdated(insGM1.getMidiAddress().getProgramChange() + ROW_VOICE_START, COL_MAP_INS);
                break;
            case "PROP_DRUMS_INSTRUMENT":
                fireTableCellUpdated(ROW_DRUMS, COL_MAP_INS);
                break;
            case "PROP_PERC_INSTRUMENT":
                fireTableCellUpdated(ROW_PERC, COL_MAP_INS);
                break;
            default:
                throw new IllegalArgumentException("e=" + e);
        }
    }

    // =============================================================================
    // Private methods
    // =============================================================================  
    private GM1Bank.Family getFamily(int row)
    {
        for (GM1Bank.Family f : mapFamilyFirstRow.keySet())
        {
            int first = mapFamilyFirstRow.get(f);
            int last = mapFamilyLastRow.get(f);
            if (row >= first && row <= last)
            {
                return f;
            }
        }
        throw new IllegalStateException("row=" + row);
    }
}