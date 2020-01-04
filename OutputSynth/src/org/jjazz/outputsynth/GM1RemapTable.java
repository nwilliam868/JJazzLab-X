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
package org.jjazz.outputsynth;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;
import org.jjazz.midi.GM1Bank;
import org.jjazz.midi.GM1Instrument;
import org.jjazz.midi.Instrument;

/**
 * The table can associate an Instrument to each of the GM1 instruments + drums/percussion instruments.
 * <p>
 * Association can also be done at the GM1Bank.Family level.
 */
public class GM1RemapTable implements Serializable
{

    /** oldValue=Family, newValue=Instrument */
    public static final String PROP_FAMILY = "Family";
    /** oldValue=GM1Instrument, newValue=Instrument */
    public static final String PROP_INSTRUMENT = "Instrument";
    public static final String PROP_DRUMS_INSTRUMENT = "DrumsInstrument";
    public static final String PROP_PERC_INSTRUMENT = "PercInstrument";
    private Instrument insDrums;
    private Instrument insPerc;
    private HashMap<GM1Instrument, Instrument> mapVoiceInstruments = new HashMap<>();
    private HashMap<GM1Bank.Family, Instrument> mapFamilyInstruments = new HashMap<>();
    private final transient PropertyChangeSupport pcs = new java.beans.PropertyChangeSupport(this);

    /**
     * Create an empty DefaultInstruments instance.
     */
    public GM1RemapTable()
    {

    }

    /**
     * Get the remapped instrument for the specified GM1 instrument.
     *
     * @param insGM1
     * @return Null if no mapping defined for insGM1.
     */
    public Instrument getInstrument(GM1Instrument insGM1)
    {
        if (insGM1 == null)
        {
            throw new NullPointerException("insGM1");
        }
        return mapVoiceInstruments.get(insGM1);
    }

    /**
     * Set the remapped instrument for the specified GM1 instrument.
     *
     * @param insGM1
     * @param ins    Can be null
     */
    public void setInstrument(GM1Instrument insGM1, Instrument ins)
    {
        if (insGM1 == null)
        {
            throw new IllegalArgumentException("insGM1=" + insGM1 + " ins=" + ins);
        }
        Instrument oldIns = mapVoiceInstruments.put(insGM1, ins);
        if (!Objects.equals(ins, oldIns))
        {
            pcs.firePropertyChange(PROP_INSTRUMENT, insGM1, ins);
        }
    }

    /**
     * Get the remapped instrument for the specified instrument family.
     *
     * @param family 
     * @return Null if no mapping defined for this family.
     */
    public Instrument getInstrument(GM1Bank.Family family)
    {
        if (family == null )
        {
            throw new NullPointerException("family");
        }
        return mapFamilyInstruments.get(family);
    }

    /**
     * Set the remapped instrument for the specified GM1 instrument.
     *
     * @param family
     * @param ins    Can be null
     */
    public void setInstrument(GM1Bank.Family family, Instrument ins)
    {
        if (family == null)
        {
            throw new IllegalArgumentException("family=" + family + " ins=" + ins);
        }
        Instrument oldIns = mapFamilyInstruments.put(family, ins);
        if (!Objects.equals(ins, oldIns))
        {
            pcs.firePropertyChange(PROP_FAMILY, family, ins);
        }
    }

    /**
     * Get the remapped instrument for drums.
     *
     * @return Null if no mapping defined for drums.
     */
    public Instrument getDrumsInstrument()
    {
        return insDrums;
    }

    /**
     * Set the remapped instrument for the drums instrument.
     *
     * @param ins Can be null
     */
    public void setDrumsInstrument(Instrument ins)
    {
        Instrument oldIns = insDrums;
        insDrums = ins;
        if (!Objects.equals(ins, oldIns))
        {
            pcs.firePropertyChange(PROP_DRUMS_INSTRUMENT, oldIns, ins);
        }
    }

    /**
     * Get the remapped instrument for percussion.
     *
     * @return Null if no mapping defined for percussion.
     */
    public Instrument getPercussionInstrument()
    {
        return insPerc;
    }

    /**
     * Set the remapped instrument for the percussion instrument.
     *
     * @param ins Can be null
     */
    public void setPercussionInstrument(Instrument ins)
    {
        Instrument oldIns = insPerc;
        insPerc = ins;
        if (!Objects.equals(ins, oldIns))
        {
            pcs.firePropertyChange(PROP_PERC_INSTRUMENT, oldIns, ins);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        pcs.removePropertyChangeListener(l);
    }

}