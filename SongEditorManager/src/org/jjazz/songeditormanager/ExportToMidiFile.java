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
package org.jjazz.songeditormanager;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.jjazz.filedirectorymanager.FileDirectoryManager;
import org.jjazz.harmony.TimeSignature;
import org.jjazz.leadsheet.chordleadsheet.api.item.CLI_ChordSymbol;
import org.jjazz.leadsheet.chordleadsheet.api.item.CLI_Section;
import org.jjazz.leadsheet.chordleadsheet.api.item.Position;
import org.jjazz.midi.InstrumentMix;
import org.jjazz.midi.MidiConst;
import org.jjazz.midi.MidiUtilities;
import org.jjazz.midimix.MidiMix;
import org.jjazz.midimix.MidiMixManager;
import org.jjazz.outputsynth.OutputSynth;
import org.jjazz.outputsynth.OutputSynthManager;
import org.jjazz.rhythm.api.RhythmVoice;
import org.jjazz.rhythm.parameters.RP_SYS_TempoFactor;
import org.jjazz.rhythmmusicgeneration.MidiSequenceBuilder;
import org.jjazz.rhythmmusicgeneration.MusicGenerationContext;
import org.jjazz.rhythm.api.MusicGenerationException;
import org.jjazz.song.api.Song;
import org.jjazz.song.api.SongFactory;
import org.jjazz.songstructure.api.SongPart;
import org.jjazz.songstructure.api.SongStructure;
import org.jjazz.ui.utilities.Utilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 * Export song to a midi file.
 */
@ActionID(category = "MusicControls", id = "org.jjazz.ui.musiccontrolactions.exporttomidifile")
@ActionRegistration(displayName = "#CTL_ExportToMidiFile", lazy = true)
@ActionReferences(
        {
            @ActionReference(path = "Menu/File", position = 1580, separatorAfter = 1590)
        })
@NbBundle.Messages(
        {
            "CTL_ExportToMidiFile=Export to Midi file..."
        })
public class ExportToMidiFile extends AbstractAction
{

    private Song song;
    private static File saveExportDir = null;

    private static final Logger LOGGER = Logger.getLogger(ExportToMidiFile.class.getSimpleName());

    public ExportToMidiFile(Song context)
    {
        song = context;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        assert song != null;

        if (song.getSongStructure().getSongParts().isEmpty())
        {
            String msg = "Can't export an empty song (no song parts)";
            NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        // Get the target midi file
        File midiFile = getMidiFile(song);
        if (midiFile == null)
        {
            String msg = "Can't build destination Midi file for song " + song.getName();
            NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        JFileChooser chooser = Utilities.getFileChooserInstance();
        chooser.resetChoosableFileFilters();
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setSelectedFile(midiFile);
        chooser.setDialogTitle("Export to Midi file");                
        int res = chooser.showSaveDialog(WindowManager.getDefault().getMainWindow());
        if (res != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        midiFile = chooser.getSelectedFile();
        saveExportDir = midiFile.getParentFile();

        if (midiFile.exists())
        {
            // File overwrite confirm dialog
            String msg = "File " + midiFile + " already exists. Confirm overwrite ?";
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION);
            Object result = DialogDisplayer.getDefault().notify(nd);
            if (result != NotifyDescriptor.OK_OPTION)
            {
                return;
            }
        }

        // Generate the sequence    
        MidiMix midiMix = null;
        try
        {
            midiMix = MidiMixManager.getInstance().findMix(song);
        } catch (MidiUnavailableException ex)
        {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            return;
        }

        // Check there is at least one unmuted track
        boolean allMuted = true;
        for (InstrumentMix insMix : midiMix.getInstrumentMixes())
        {
            if (!insMix.isMute())
            {
                allMuted = false;
                break;
            }
        }
        if (allMuted)
        {
            String msg = "Can't export to Midi file: all channels are muted.";
            LOGGER.warning(msg);
            NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        // Work on a copy
        SongFactory sf = SongFactory.getInstance();
        Song songCopy = sf.getCopy(song);

        // Build the sequence
        MidiSequenceBuilder seqBuilder = new MidiSequenceBuilder(new MusicGenerationContext(songCopy, midiMix));
        HashMap<RhythmVoice, Integer> mapRvTrackId = seqBuilder.getRvTrackIdMap();
        Sequence sequence = null;
        try
        {
            sequence = seqBuilder.buildSequence(false);
            mapRvTrackId = seqBuilder.getRvTrackIdMap();
        } catch (MusicGenerationException ex)
        {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            return;
        } finally
        {
            songCopy.close(false);
        }

        // Check Midi export capabilities
        int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
        boolean fileTypeOK = false;
        for (int fileType : fileTypes)
        {
            if (fileType == 1)
            {
                fileTypeOK = true;
                break;
            }
        }
        if (!fileTypeOK)
        {
            String msg = "Can't export to Midi file: MidiSystem does not support Midi file 1 format";
            LOGGER.warning(msg);
            NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        // Remove elements from muted tracks (don't remove track because impact on mapRvTrack + drumsrerouting)
        for (RhythmVoice rv : midiMix.getRhythmVoices())
        {
            if (midiMix.getInstrumentMixFromKey(rv).isMute())
            {
                Track track = sequence.getTracks()[mapRvTrackId.get(rv)];
                emptyTrack(track);
            }
        }

        // Apply Drums channel rerouting        
        List<Integer> toBeRerouted = midiMix.getDrumsReroutedChannels();
        MidiUtilities.rerouteShortMessages(sequence, toBeRerouted, MidiConst.CHANNEL_DRUMS);

        // Add chord symbols as markers
        addChordSymbolMarkers(sequence);


        // Modify sequence to make Midi file as portable as possible
        prepareForMidiFile(sequence, mapRvTrackId, midiMix);

        // Finally write to file
        LOGGER.info("actionPerformed() writing sequence to Midi file: " + midiFile.getAbsolutePath());
        try
        {
            MidiSystem.write(sequence, 1, midiFile);
            StatusDisplayer.getDefault().setStatusText("Midi sequence written to " + midiFile.getAbsolutePath());
        } catch (IOException ex)
        {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
    }

    // ======================================================================
    // Private methods
    // ======================================================================   
    /**
     *
     * @param sg
     * @return Can be null
     */
    private File getMidiFile(Song sg)
    {
        File f = null;
        File songFile = sg.getFile();
        String midiFilename = (songFile == null) ? sg.getName() + ".mid" : org.jjazz.util.Utilities.replaceExtension(songFile.getName(), ".mid");
        if (saveExportDir != null && !saveExportDir.isDirectory())
        {
            saveExportDir = null;
        }
        File dir = saveExportDir;
        if (dir == null)
        {
            if (songFile != null)
            {
                dir = songFile.getParentFile();         // Can be null
            }
            if (dir == null)
            {
                FileDirectoryManager fdm = FileDirectoryManager.getInstance();
                dir = fdm.getLastSongDirectory();       // Can be null                       
            }
        }
        if (dir != null)
        {
            f = new File(dir, midiFilename);
        }
        return f;
    }

    /**
     * Prepare the sequence for Midi file export.
     * <p>
     * Shift all events 1 bar to leave time to apply config changes.<br>
     * Add prog/bank changes messages, tempo, reset controllers, time signature changes, etc...
     *
     * @param sequence
     * @throws ArrayIndexOutOfBoundsException
     * @todo Should we convert tempo Midi message depending on TimeSignature (eg 4/4 or 6/8 don't have the same natural beat...) ?
     */
    private void prepareForMidiFile(Sequence sequence, HashMap<RhythmVoice, Integer> mapRvTrackId, MidiMix midiMix) throws ArrayIndexOutOfBoundsException
    {
        Track[] tracks = sequence.getTracks();
        if (tracks.length == 0)
        {
            LOGGER.warning("prepareForMidiFile() no track found in sequence ! mapRvTrackId=" + mapRvTrackId);
            return;
        }

        List<SongPart> spts = song.getSongStructure().getSongParts();
        SongPart spt0 = spts.get(0);
        TimeSignature ts0 = spt0.getRhythm().getTimeSignature();
        long initBarInTicks = (long) (ts0.getNbNaturalBeats() * MidiConst.PPQ_RESOLUTION);


        // Shift one bar except track names and initial time signature  
        for (Track track : tracks)
        {
            for (int i = track.size() - 1; i >= 0; i--)
            {
                MidiEvent me = track.get(i);
                long tick = me.getTick();
                MidiMessage mm = me.getMessage();
                if (mm instanceof MetaMessage)
                {
                    int type = ((MetaMessage) mm).getType();
                    // Track name=3 or initial time signature         
                    if (type == 3 || (type == 88 && tick == 0))
                    {
                        continue;
                    }
                }
                me.setTick(tick + initBarInTicks);
            }
        }


        // Add initialization messages on first track
        // Copyright
        Track firstTrack = tracks[0];
        MidiMessage mmCopyright = MidiUtilities.getCopyrightMetaMessage("JJazzLab Midi Export file");
        MidiEvent me = new MidiEvent(mmCopyright, 0);
        firstTrack.add(me);
        // Initial tempo
        int tempo = song.getTempo();
        RP_SYS_TempoFactor rp = RP_SYS_TempoFactor.getTempoFactorRp(spt0.getRhythm());
        int tempoFactor = -1;
        if (rp != null)
        {
            tempoFactor = spt0.getRPValue(rp);
            tempo = Math.round(tempoFactor / 100f * tempo);
        }
        me = new MidiEvent(MidiUtilities.getTempoMessage(0, tempo), 0);
        firstTrack.add(me);


        // Add XX mode ON initialization message
        OutputSynth os = OutputSynthManager.getInstance().getOutputSynth();
        SysexMessage sm = null;
        switch (os.getSendModeOnUponPlay())
        {
            case GM:
                sm = MidiUtilities.getGmModeOnSysExMessage();
                break;
            case GM2:
                sm = MidiUtilities.getGm2ModeOnSysExMessage();
                break;
            case GS:
                sm = MidiUtilities.getGsModeOnSysExMessage();
                break;
            case XG:
                sm = MidiUtilities.getXgModeOnSysExMessage();
                break;
            default:
            // Nothing
        }
        if (sm != null)
        {
            me = new MidiEvent(sm, 0);
            firstTrack.add(me);
        }


        // Add reset all controllers + instruments initialization messages for each track
        for (RhythmVoice rv : mapRvTrackId.keySet())
        {
            Track track = tracks[mapRvTrackId.get(rv)];
            int channel = midiMix.getChannel(rv);

            // Reset all controllers
            MidiMessage mmReset = MidiUtilities.getResetAllControllersMessage(channel);
            me = new MidiEvent(mmReset, 0);
            track.add(me);

            // Instrument + volume + pan etc.
            InstrumentMix insMix = midiMix.getInstrumentMixFromKey(rv);
            for (MidiMessage mm : insMix.getAllMidiMessages(channel))
            {
                me = new MidiEvent(mm, 0);
                track.add(me);
            }
        }


        // Add possible song part tempo changes
        int lastTempoFactor = tempoFactor;
        for (int i = 1; i < spts.size(); i++)
        {
            SongPart spt = spts.get(i);
            rp = RP_SYS_TempoFactor.getTempoFactorRp(spt.getRhythm());
            if (rp != null)
            {
                tempoFactor = spt.getRPValue(rp);
                if (tempoFactor != lastTempoFactor)
                {
                    tempo = Math.round(tempoFactor / 100f * song.getTempo());
                    float beatPos = song.getSongStructure().getPositionInNaturalBeats(spt.getStartBarIndex());
                    long tickPos = initBarInTicks + Math.round(beatPos * MidiConst.PPQ_RESOLUTION);
                    me = new MidiEvent(MidiUtilities.getTempoMessage(0, tempo), tickPos);
                    firstTrack.add(me);
                    lastTempoFactor = tempoFactor;
                }
            }
        }
    }

    private void addChordSymbolMarkers(Sequence seq)
    {
        Track firstTrack = seq.getTracks()[0];
        SongStructure ss = song.getSongStructure();
        for (SongPart spt : ss.getSongParts())
        {
            CLI_Section section = spt.getParentSection();
            for (CLI_ChordSymbol cliCs : song.getChordLeadSheet().getItems(section, CLI_ChordSymbol.class))
            {
                Position absPos = ss.getSptItemPosition(spt, cliCs);
                float posInBeats = ss.getPositionInNaturalBeats(absPos.getBar()) + absPos.getBeat();
                long tickPos = Math.round(posInBeats * MidiConst.PPQ_RESOLUTION);
                MidiEvent me = new MidiEvent(MidiUtilities.getMarkerMetaMessage(cliCs.getData().getName()), tickPos);
                firstTrack.add(me);
            }
        }
    }

    /**
     * Remove all events from the specified track.
     * <p>
     */
    private void emptyTrack(Track track)
    {
        // Track uses a simple List to store MidiEvents
        for (int i = track.size() - 1; i >= 0; i--)
        {
            track.remove(track.get(i));
        }
    }

}
