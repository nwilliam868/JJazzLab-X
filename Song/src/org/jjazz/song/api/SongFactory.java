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
package org.jjazz.song.api;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jjazz.harmony.TimeSignature;
import org.jjazz.leadsheet.chordleadsheet.api.ChordLeadSheet;
import org.jjazz.leadsheet.chordleadsheet.api.ChordLeadSheetFactory;
import org.jjazz.leadsheet.chordleadsheet.api.UnsupportedEditException;
import org.jjazz.leadsheet.chordleadsheet.api.item.CLI_Section;
import org.jjazz.rhythm.parameters.RhythmParameter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.jjazz.songstructure.api.SongStructure;
import org.jjazz.songstructure.api.SongPart;
import org.jjazz.songstructure.api.SongStructureFactory;
import org.openide.util.Exceptions;

/**
 * Manage the creation and the registration of the songs.
 * <p>
 * All songs created by this factory are automatically registered. Registered songs are unregistered when song is closed.
 */
public class SongFactory implements PropertyChangeListener
{

    static private SongFactory INSTANCE;
    // Use WeakReference to avoid a memory leak if for some reason a closed song was not unregistered. Integer value is not used. 
    private WeakHashMap<Song, Integer> songs;
    /**
     * Used to make sure we don't have the same name twice.
     */
    private static int counter = 1;

    private static final Logger LOGGER = Logger.getLogger(SongFactory.class.getSimpleName());

    static public SongFactory getInstance()
    {
        synchronized (SongFactory.class)
        {
            if (INSTANCE == null)
            {
                INSTANCE = new SongFactory();
            }
        }
        return INSTANCE;
    }

    private SongFactory()
    {
        songs = new WeakHashMap<>();
    }

    /**
     * All songs created by this object are automatically registered.
     *
     *
     * @return A list of the songs registered by this object.
     */
    public List<Song> getRegisteredSongs()
    {
        return new ArrayList<>(songs.keySet());
    }

    /**
     * Register a song if it was not created by the SongManager.
     *
     * @param sg
     */
    public void registerSong(Song sg)
    {
        if (!songs.keySet().contains(sg))
        {
            songs.put(sg, 0);
            sg.addPropertyChangeListener(this);
        }
    }

    /**
     * Provide a new song name which is not used by any currently opened song.
     *
     * @return
     */
    public String getNewSongName()
    {
        String name = "NewSong" + counter;
        while (!isSongNameUsed(name))
        {
            counter++;
            name = "NewSong" + counter;
        }
        return name;
    }

    /**
     * Get a Song object from a file.
     * <p>
     * Song's getFile() will return f. <br>
     * Song's getName() will return f.getName(). <br>
     * Notify user with a Dialog if a problem prevented the creation of the song object.
     *
     * @param f
     * @return Null if problem.
     */
    public Song createFromFile(File f)
    {
        if (f == null)
        {
            throw new IllegalArgumentException("f=" + f);
        }
        Song song = null;
// Commented out: rely on XStream exception that will be generated by fromXML() if file can't be read.
// If it explanation message is not clear enough use code below.
//        if (!f.canRead())
//        {
//            String msg = ERR_AccessFile() + ": " + f.getAbsolutePath();
//            throw new XStreamException(msg);    
//        }
        XStream xstream = new XStream();
        try
        {
            song = (Song) xstream.fromXML(f);
        } catch (XStreamException ex)
        {
            String msg = "Problem reading file: " + f.getAbsolutePath() + ".\n" + ex.getLocalizedMessage();
            LOGGER.log(Level.WARNING, "loadFromFile() - {0}", msg);
            NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return null;
        }
        song.setFile(f);
        song.setName(Song.removeSongExtension(f.getName()));
        song.resetNeedSave();
        registerSong(song);
        return song;
    }

    /**
     * Remove a song from the list returned by getRegisteredSong().
     *
     * @param song
     */
    public void unregisterSong(Song song)
    {
        songs.remove(song);
        song.removePropertyChangeListener(this);
    }

    /**
     * Find in the created song the first one which uses the specified SongStructure.
     *
     * @param sgs
     * @return
     */
    public Song findSong(SongStructure sgs)
    {
        Song res = null;
        for (Song song : songs.keySet())
        {
            if (song.getSongStructure() == sgs)
            {
                res = song;
                break;
            }
        }
        return res;
    }

    /**
     * Find in the created song the first one which uses the specified ChordLeadSheet.
     *
     * @param cls
     * @return
     */
    public Song findSong(ChordLeadSheet cls)
    {
        Song res = null;
        for (Song song : songs.keySet())
        {
            if (song.getChordLeadSheet() == cls)
            {
                res = song;
                break;
            }
        }
        return res;
    }

    /**
     * Create a Song from the specified chordleadsheet.
     *
     * @param name
     * @param cls
     * @return
     * @throws UnsupportedEditException Can happen if too many timesignature changes resulting in not enough Midi channels for the
     *                                  various rhythms.
     */
    public Song createSong(String name, ChordLeadSheet cls) throws UnsupportedEditException
    {
        if (name == null || name.isEmpty() || cls == null)
        {
            throw new IllegalArgumentException("name=" + name + " cls=" + cls);
        }
        Song song = new Song(name, cls);
        registerSong(song);
        return song;
    }

    /**
     * Create a 8-bar empty song.
     *
     * @param name
     * @return
     */
    public Song createEmptySong(String name)
    {
        return createEmptySong(name, 8);
    }

    /**
     * Create an empty song of specified length.
     *
     * @param name    The name of the song
     * @param clsSize The number of bars of the song.
     * @return
     */
    public Song createEmptySong(String name, int clsSize)
    {
        if (name == null || name.isEmpty() || clsSize < 1)
        {
            throw new IllegalArgumentException("name=" + name + " clsSize=" + clsSize);
        }
        ChordLeadSheetFactory clsf = ChordLeadSheetFactory.getDefault();
        ChordLeadSheet cls = clsf.createEmptyLeadSheet("A", TimeSignature.FOUR_FOUR, clsSize);
        Song song = null;
        try
        {
            song = new Song(name, cls);
        } catch (UnsupportedEditException ex)
        {
            // We should not be here
            throw new IllegalStateException("Unexpected 'UnsupportedEditException'.", ex);
        }
        int tempo = song.getSongStructure().getSongPart(0).getRhythm().getPreferredTempo();
        song.setTempo(tempo);
        song.resetNeedSave();
        registerSong(song);
        return song;
    }

    public boolean isSongNameUsed(String name)
    {
        boolean b = true;
        for (Song sg : getRegisteredSongs())
        {
            if (sg.getName().equals(name))
            {
                b = false;
                break;
            }
        }
        return b;
    }

    /**
     * Return a deep copy of the specified song.
     * <p>
     * Copy the following variables: chordleadsheet, songStructure, name, tempo, comments, tags<br>
     * Listeners or file are NOT copied. Created song is registered.
     *
     * @param song
     * @return
     */
    @SuppressWarnings(
            {
                "unchecked"
            })
    public Song getCopy(Song song)
    {
        ChordLeadSheetFactory clsf = ChordLeadSheetFactory.getDefault();
        ChordLeadSheet newCls = clsf.getCopy(song.getChordLeadSheet());

        Song s = null;
        try
        {
            s = new Song(song.getName(), newCls);       // SongStructure and ChordLeadsheet will be linked
        } catch (UnsupportedEditException ex)
        {
            // Should not occur since it's a clone, ie already accepted edits
            throw new IllegalArgumentException("clone() failed. Song's name=" + song.getName(), ex);
        }
        s.setComments(song.getComments());
        s.setTempo(song.getTempo());
        s.setTags(song.getTags());

        // Clean the default songStructure
        SongStructure newSgs = s.getSongStructure();
        newSgs.removeSongParts(newSgs.getSongParts());

        // Recreate each SongPart copy
        for (SongPart spt : song.getSongStructure().getSongParts())
        {
            CLI_Section newParentSection = newCls.getSection(spt.getParentSection().getData().getName());
            assert newParentSection != null : "spt=" + spt;
            SongPart sptCopy = spt.clone(spt.getRhythm(), spt.getStartBarIndex(), spt.getNbBars(), newParentSection);
            try
            {
                newSgs.addSongPart(sptCopy);        // Can raise UnsupportedEditException
            } catch (UnsupportedEditException ex)
            {
                // Should not occur since it's a clone, ie already accepted edits
                throw new IllegalArgumentException("getCopy() failed. Song's name=" + song.getName() + " newSgs=" + newSgs + " sptCopy=" + sptCopy, ex);
            }
        }
        s.resetNeedSave();
        registerSong(s);
        return s;
    }

    /**
     * Return a copy of the song where the SongStructure does NOT listen to the ChordLeadsheet changes.
     * <p>
     * WARNING: Because SongStructure and ChordLeadsheet are not linked, changing them might result in inconsistent states. This
     * should be used only in special cases.<p>
     * Copy the following variables: chordleadsheet, songStructure, name, tempo, comments, tags. Listeners or file are NOT copied.
     * Created song is registered.
     *
     * @param song
     * @return
     */
    @SuppressWarnings(
            {
                "unchecked"
            })
    public Song getCopyUnlinked(Song song)
    {
        ChordLeadSheet cls = ChordLeadSheetFactory.getDefault().getCopy(song.getChordLeadSheet());
        SongStructure ss = null;
        try
        {
            ss = SongStructureFactory.getDefault().createSgs(cls, false);     // Don't link sgs to cls.  Can raise UnsupportedEditException
            ss.removeSongParts(ss.getSongParts());
            for (SongPart spt : song.getSongStructure().getSongParts())
            {
                String parentSectionName = spt.getParentSection().getData().getName();
                CLI_Section parentSectionCopy = cls.getSection(parentSectionName);
                SongPart sptCopy = spt.clone(spt.getRhythm(), spt.getStartBarIndex(), spt.getNbBars(), parentSectionCopy);
                ss.addSongPart(sptCopy);        // Can raise UnsupportedEditException
            }
        } catch (UnsupportedEditException ex)
        {
            // Should not occur since it's a copy, ie already accepted edits
            throw new IllegalArgumentException("getCopyUnlinked() failed. Song's name=" + song.getName() + " ss=" + ss, ex);
        }

        // Now create the song copy
        Song s = new Song(song.getName(), cls, ss);
        s.setComments(song.getComments());
        s.setTempo(song.getTempo());
        s.setTags(song.getTags());

        s.resetNeedSave();
        registerSong(s);
        return s;
    }

    // =================================================================================
    // PropertyChangeListener methods
    // =================================================================================    
    @Override
    public void propertyChange(PropertyChangeEvent e)
    {
        if (e.getSource() instanceof Song)
        {
            Song song = (Song) e.getSource();
            assert songs.keySet().contains(song) : "song=" + song + " songs=" + songs.keySet();
            if (e.getPropertyName() == Song.PROP_CLOSED)
            {
                unregisterSong(song);
            }
        }
    }

    // =================================================================================
    // Private methods
    // =================================================================================
}
