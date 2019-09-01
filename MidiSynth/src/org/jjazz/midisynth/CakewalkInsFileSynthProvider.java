/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 *  Copyright @2019 Jerome Lelasseux. All rights reserved.
 *
 *  This file is part of the JJazzLabX software.
 *   
 *  JJazzLabX is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License (LGPLv3) 
 *  as published by the Free Software Foundation, either version 3 of the License, 
 *  or (at your option) any later version.
 *
 *  JJazzLabX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JJazzLabX.  If not, see <https://www.gnu.org/licenses/>
 * 
 *  Contributor(s): 
 */
package org.jjazz.midisynth;

import org.jjazz.midisynth.spi.AbstractMidiSynthProvider;
import org.jjazz.midisynth.spi.MidiSynthProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jjazz.midi.AbstractInstrumentBank;
import org.jjazz.midi.Instrument;
import org.jjazz.midi.InstrumentBank;
import org.jjazz.midi.MidiSynth;
import org.openide.util.lookup.ServiceProvider;

/**
 * A MidiSynth provider reading Cakewalk .ins instrument definition files.
 */
@ServiceProvider(service = MidiSynthProvider.class)
public class CakewalkInsFileSynthProvider extends AbstractMidiSynthProvider
{

    private static final Logger LOGGER = Logger.getLogger(CakewalkInsFileSynthProvider.class.getSimpleName());
    private final FileNameExtensionFilter FILTER = new FileNameExtensionFilter("Cakewalk instrument files (.ins)", "ins");

    public CakewalkInsFileSynthProvider()
    {
        super("InsFileSynthProvider");
        // addBuiltinSynth(new Synth1());
        // addBuiltinSynth(new Synth2());
    }

    @Override
    protected List<MidiSynth> readFile(File f) throws IOException
    {
        ArrayList<MidiSynth> synths = new ArrayList<>();
//        FileReader fileReader = null;
//        BufferedReader reader = null;
        // state=0: unknown or ignored section
        // state=1: within .Patch Names section
        // state=2: within .Instrument Definitions section
        int state = 0;
        InsBank currentBank = null;
        MidiSynth currentSynth = null;
        InstrumentBank.BankSelectMethod currentBsm = InstrumentBank.BankSelectMethod.MSB_LSB;
        // The BankName and the InstrumentBank
        HashMap<String, InsBank> mapNameBank = new HashMap<>();
        // try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f), 0X100000)))
        try (BufferedReader reader = new BufferedReader(new FileReader(f)))
        {
            int lineCount = 0;
            String line;
            Pattern pBank1 = Pattern.compile("^\\s*\\[(.+)\\]");  // [Bank PRE1]
            Pattern pPatch1 = Pattern.compile("^\\s*(\\d+)=(.*)");   // 1=Grand Piano
            Pattern pSynth2 = Pattern.compile("^\\s*\\[(.+)\\]");  // [Motif XS]
            Pattern pBsm2 = Pattern.compile("^\\s*BankSelMethod=([0-9])");  // BankSelMethod=2
            Pattern pPatch2 = Pattern.compile("^\\s*Patch\\[(\\d+)\\]=(.*)");  // Patch[1232]=PRE 1
            Pattern p2Patch2 = Pattern.compile("^\\s*Patch\\[\\*\\]=([^.]*)");  // Patch[*]=PRE 1, but not Patch[*]=1...128                                        
            // 
            // MAIN LOOP
            //
            while ((line = reader.readLine()) != null)
            {
                lineCount++;
                String l = line.replaceFirst(";.*", "");  // remove comments
                LOGGER.fine("readFile() l=" + lineCount + " state=" + state + " : " + l);

                if (l.matches("^\\s*\\..*"))    // ".something..."
                {
                    // It's a section change
                    if (l.contains("Patch Names"))
                    {
                        // Entering new section : bank + patch names
                        state = 1;

                    } else if (l.contains("Instrument Definitions"))
                    {
                        // Entering new section : LSB/MSB
                        state = 2;
                    } else
                    {
                        // Ignore other sections
                        state = 0;
                    }
                    currentBank = null;
                    continue;
                }

                // We are within a section
                if (state == 1)
                {
                    // We are in the Patch Names section
                    Matcher mBank = pBank1.matcher(l);
                    Matcher mPatch = pPatch1.matcher(l);
                    if (mBank.matches())
                    {
                        // Create a new bank 
                        String bankName = mBank.group(1).trim();
                        currentBank = new InsBank(bankName);
                        // And store it
                        mapNameBank.put(bankName, currentBank);
                        continue;
                    } else if (mPatch.matches())
                    {
                        // Add instrument to the current bank
                        if (currentBank == null)
                        {
                            LOGGER.warning("Patch name found in file " + f.getAbsolutePath() + " at line " + lineCount + " but no bank set.");
                            continue;
                        }
                        String spc = mPatch.group(1);
                        int pc = 0;
                        try
                        {
                            pc = Integer.valueOf(spc);
                        } catch (NumberFormatException e)
                        {
                            // Leave pc=0
                            LOGGER.warning("Can't read program change value in file " + f.getAbsolutePath() + " at line " + lineCount);
                        }
                        String patchName = mPatch.group(2);
                        if (patchName == null || patchName.trim().isEmpty())
                        {
                            LOGGER.warning("Can't read a valid patch name in file " + f.getAbsolutePath() + " at line " + lineCount);
                            patchName = "not set";
                        }
                        Instrument ins = new Instrument(pc, patchName);
                        currentBank.addInstrument(ins);
                        continue;
                    }
                } else if (state == 2)
                {
                    // We are in the Instrument Definition section
                    Matcher mSynth = pSynth2.matcher(l);
                    Matcher mBsm = pBsm2.matcher(l);
                    Matcher mPatch = pPatch2.matcher(l);
                    Matcher m2Patch = p2Patch2.matcher(l);
                    if (mSynth.matches())
                    {
                        // It's a new synth, create it
                        String synthName = mSynth.group(1);
                        currentSynth = new MidiSynth(synthName, "");
                        synths.add(currentSynth);
                    } else if (mBsm.matches())
                    {
                        // It's a BankSelectMethod specification
                        // IMPORTANT: it will impact only lines after this line ! So should be the first line after Synth name
                        if (currentSynth == null)
                        {
                            LOGGER.warning("BankSelectMethod found in file " + f.getAbsolutePath() + " at line " + lineCount + " but no instrument set.");
                            continue;
                        }
                        String s = mBsm.group(1);
                        int bsm = 0;
                        try
                        {
                            bsm = Integer.valueOf(s);
                        } catch (NumberFormatException e)
                        {
                            // Leave bsm=0
                            LOGGER.warning("Can't read BankSelectMethod in file " + f.getAbsolutePath() + " at line " + lineCount);
                        }
                        currentBsm = getBsm(bsm);
                        continue;
                    } else if (m2Patch.matches())
                    {
                        // It's a "patch[*]=SomeBankName"
                        // Find the bank by its name, set the special BankSelectMethod, attach to the current MidiSynth
                        if (currentSynth == null)
                        {
                            LOGGER.warning("Patch[*] found in file " + f.getAbsolutePath() + " at line " + lineCount + " but no instrument set.");
                            continue;
                        }
                        String bankName = m2Patch.group(1).trim();
                        InsBank bank = mapNameBank.get(bankName);
                        if (bank == null)
                        {
                            LOGGER.warning("Can't find bank " + bankName + " in file " + f.getAbsolutePath() + " at line " + lineCount);
                        } else if (bank.getMidiSynth() != null)
                        {
                            LOGGER.warning("Bank " + bankName + " already assigned to a synth in file " + f.getAbsolutePath() + " at line " + lineCount);
                        } else
                        {
                            // Update the bank 
                            bank.setBankSelectMethod(InstrumentBank.BankSelectMethod.PC_ONLY);
                            // Add it to the current Synth
                            if (!currentSynth.getBanks().contains(bank))
                            {
                                currentSynth.addBank(bank);
                            }
                        }
                    } else if (mPatch.matches())
                    {
                        // It's a patch[]=SomeBankName
                        // Find the bank by its name, set the MSB and LSB, attach to the current MidiSynth
                        if (currentSynth == null)
                        {
                            LOGGER.warning("Patch[] found in file " + f.getAbsolutePath() + " at line " + lineCount + " but no instrument set.");
                            continue;
                        }
                        String s = mPatch.group(1);
                        int value = 0;
                        try
                        {
                            value = Integer.valueOf(s);
                        } catch (NumberFormatException e)
                        {
                            // Leave i=0
                            LOGGER.warning("Can't read Patch[] integer value in file " + f.getAbsolutePath() + " at line " + lineCount);
                        }
                        int msb = value / 128;
                        int lsb = value - (msb * 128);
                        String bankName = mPatch.group(2).trim();
                        InsBank bank = mapNameBank.get(bankName);
                        if (bank == null)
                        {
                            LOGGER.warning("Can't find bank " + bankName + " in file " + f.getAbsolutePath() + " at line " + lineCount);
                        } else if (bank.getMidiSynth() != null)
                        {
                            LOGGER.warning("Bank " + bankName + " already assigned to a synth in file " + f.getAbsolutePath() + " at line " + lineCount);
                        } else
                        {
                            // Update the bank 
                            bank.setBankSelectMethod(currentBsm);
                            bank.setBankSelectLsb(lsb);
                            bank.setBankSelectMsb(msb);
                            // Add it to the current Synth
                            if (!currentSynth.getBanks().contains(bank))
                            {
                                currentSynth.addBank(bank);
                            }
                        }
                    }
                }
            }
            // We reached end of file

            // In some cases we may have synths with no banks, don't keep them
            // Example of such ins file : 
            // .Instrument Definitions
            // [Giga]
            // Patch[*]=Giga      
            // [MOXF]
            // Patch[239]=PRE 1
            // etc.
            // Set the file for the others
            for (MidiSynth synth : synths.toArray(new MidiSynth[0]))
            {
                if (synth.getBanks().isEmpty())
                {
                    synths.remove(synth);
                } else
                {
                    synth.setFile(f);
                }
            }
        }
        LOGGER.fine("readFile() EXIT synths.size()=" + synths.size());
        if (synths.isEmpty())
        {
            LOGGER.warning("No synth found in file " + f.getAbsolutePath());
        }
        return synths;
    }

    /**
     * Translate the Cakewalk number for BankSelectMethod into our data type.
     */
    private InstrumentBank.BankSelectMethod getBsm(int bsm)
    {
        switch (bsm)
        {
            case 1:
                return InstrumentBank.BankSelectMethod.MSB_ONLY;
            case 2:
                return InstrumentBank.BankSelectMethod.LSB_ONLY;
            case 3:
                return InstrumentBank.BankSelectMethod.PC_ONLY;
            default:
                return InstrumentBank.BankSelectMethod.MSB_LSB;
        }
    }

    @Override
    public List<FileNameExtensionFilter> getSupportedFileTypes()
    {
        return Arrays.asList(FILTER);
    }
}

/**
 * Our simplified InstrumentBank with convenience methods.
 */
class InsBank extends AbstractInstrumentBank<Instrument>
{

    public InsBank(String name)
    {
        super(name, null, 0, 0);
    }

    @Override
    public void addInstrument(Instrument ins)
    {
        super.addInstrument(ins);
    }

    public void setBankSelectMethod(BankSelectMethod m)
    {
        bsm = m;
    }

    public void setBankSelectLsb(int lsb)
    {
        if (lsb < 0 || lsb > 127)
        {
            throw new IllegalArgumentException("lsb=" + lsb);
        }
        this.lsb = lsb;
    }

    public void setBankSelectMsb(int msb)
    {
        if (msb < 0 || msb > 127)
        {
            throw new IllegalArgumentException("msb=" + msb);
        }
        this.msb = msb;
    }
}
