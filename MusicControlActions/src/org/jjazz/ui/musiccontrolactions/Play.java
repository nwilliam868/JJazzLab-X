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
package org.jjazz.ui.musiccontrolactions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.jjazz.activesong.ActiveSongManager;
import org.jjazz.musiccontrol.MusicController;
import org.jjazz.rhythmmusicgeneration.spi.MusicGenerationException;
import org.jjazz.song.api.Song;
import org.jjazz.ui.flatcomponents.FlatToggleButton;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.BooleanStateAction;

/**
 * Show/hide the playback point in editors during song playback.
 */
@ActionID(category = "MusicControls", id = "org.jjazz.ui.musiccontrolactions.play")
@ActionRegistration(displayName = "#CTL_Play", lazy = false)
@ActionReferences(
        {
            // 
            @ActionReference(path = "Shortcuts", name = "SPACE")
        })
@NbBundle.Messages(
        {
            "CTL_Play=Play",
            "CTL_PlayToolTip=Start/Pause playback (space key)"
        })
public class Play extends BooleanStateAction implements PropertyChangeListener, LookupListener
{

    private Lookup.Result<Song> lookupResult;
    private Song currentSong;
    private static final Logger LOGGER = Logger.getLogger(Play.class.getSimpleName());

    public Play()
    {
        setBooleanState(false);

        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/org/jjazz/ui/musiccontrolactions/resources/PlayButtonBorder-24x24.png")));
        putValue(Action.LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/jjazz/ui/musiccontrolactions/resources/PlayButtonBorderOn-24x24.png")));
        putValue(Action.SHORT_DESCRIPTION, Bundle.CTL_PlayToolTip());
        putValue("hideActionText", true);

        // Listen to playbackState and position changes
        MusicController.getInstance().addPropertyChangeListener(this);

        // Listen to the Midi active song changes
        ActiveSongManager.getInstance().addPropertyListener(this);

        // Listen to the current Song changes
        lookupResult = Utilities.actionsGlobalContext().lookupResult(Song.class);
        lookupResult.addLookupListener(this);
        currentSongChanged();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        setSelected(!getBooleanState());
    }

    public void setSelected(boolean newState)
    {
        if (newState == getBooleanState())
        {
            return;
        }

        setBooleanState(newState);      // Notify all listeners eg UI button                

        MusicController mc = MusicController.getInstance();
        MusicController.State playBackState = mc.getPlaybackState();
        LOGGER.fine("setSelected() newState=" + newState + " playBackState=" + playBackState);
        int fromBarIndex = 0;         // By default start playback from the beginning
        switch (playBackState)
        {
            case PLAYBACK_PAUSED:
                fromBarIndex = -1;        // Start from last position
            case PLAYBACK_STOPPED:
                if (newState)
                {
                    // Start or restart playback
                    assert currentSong != null; // Otherwise button should be disabled
                    try
                    {
                        mc.start(currentSong, fromBarIndex);
                    } catch (MusicGenerationException | PropertyVetoException ex)
                    {
                        if (ex.getLocalizedMessage() != null)
                        {
                            NotifyDescriptor d = new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                            DialogDisplayer.getDefault().notify(d);
                        }
                        setBooleanState(!newState);
                        return;
                    }
                } else
                {
                    // Nothing
                }
                break;

            case PLAYBACK_STARTED:
                if (newState)
                {
                    // Nothing
                } else
                {
                    // Pause playback, might actually be equivalent to a stop() if song was modified
                    mc.pause();
                }
                break;
            default:
                throw new IllegalArgumentException("playBackState=" + playBackState + " newState=" + newState);
        }
    }

    @Override
    public void resultChanged(LookupEvent ev)
    {
        int i = 0;
        Song newSong = null;
        for (Song s : lookupResult.allInstances())
        {
            newSong = s;
            i++;
        }
        assert i < 2 : "i=" + i + " lookupResult.allInstances()=" + lookupResult.allInstances();
        if (newSong != null)
        {
            // Current song has changed
            currentSong = newSong;
            currentSongChanged();
        } else
        {
            // Do nothing : player is still using the last valid song
        }
    }

    @Override
    public String getName()
    {
        return Bundle.CTL_Play();
    }

    @Override
    public HelpCtx getHelpCtx()
    {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public Component getToolbarPresenter()
    {
        return new FlatToggleButton(this);
    }

    // ======================================================================
    // PropertyChangeListener interface
    // ======================================================================    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        MusicController mc = MusicController.getInstance();
        if (evt.getSource() == mc)
        {
            if (evt.getPropertyName() == MusicController.PROP_PLAYBACK_STATE)
            {
                playbackStateChanged();
            }
        } else if (evt.getSource() == ActiveSongManager.getInstance())
        {
            if (evt.getPropertyName() == ActiveSongManager.PROP_ACTIVE_SONG)
            {
                activeSongChanged();
            }
        }
    }

    // ======================================================================
    // Private methods
    // ======================================================================   
    private void activeSongChanged()
    {
        currentSongChanged();    // Enable/Disable components            
    }

    private void currentSongChanged()
    {
        Song activeSong = ActiveSongManager.getInstance().getActiveSong();
        boolean b = (currentSong != null && currentSong == activeSong);
        setEnabled(b);
    }

    private void playbackStateChanged()
    {
        MusicController mc = MusicController.getInstance();
        LOGGER.fine("playbackStateChanged() actionState=" + getBooleanState() + " mc.getPlaybackState()=" + mc.getPlaybackState());
        setBooleanState(mc.getPlaybackState() == MusicController.State.PLAYBACK_STARTED);
    }

}
