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
package org.jjazz.ui.ss_editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jjazz.rhythm.parameters.RhythmParameter;
import org.jjazz.ui.ss_editor.api.RL_EditorTopComponent;
import org.jjazz.ui.ss_editor.api.SS_Editor;
import org.jjazz.ui.ss_editor.api.RL_SelectionUtilities;
import org.jjazz.songstructure.api.SongPartParameter;
import org.jjazz.songstructure.api.SongPart;

public class JumpToHome extends AbstractAction
{

    @Override
    public void actionPerformed(ActionEvent e)
    {
        SS_Editor editor = RL_EditorTopComponent.getActive().getRL_Editor();
        RL_SelectionUtilities selection = new RL_SelectionUtilities(editor.getLookup());
        SongPart spt = editor.getModel().getSongParts().get(0);
        if (selection.isSongPartSelected() || selection.isEmpty())
        {
            selection.unselectAll(editor);
            editor.selectSongPart(spt, true);
            editor.setFocusOnSongPart(spt);
            editor.makeSptViewerVisible(spt);
        } else
        {
            SongPartParameter sptp = selection.getSelectedSongPartParameters().get(0);
            selection.unselectAll(editor);
            // Find first compatible rp
            RhythmParameter<?> rp = RhythmParameter.Utilities.findFirstCompatibleRp(spt.getRhythm().getRhythmParameters(), sptp.getRp());
            if (rp != null)
            {
                editor.selectRhythmParameter(spt, rp, true);
                editor.setFocusOnRhythmParameter(spt, rp);
                editor.makeSptViewerVisible(spt);
            } else
            {
                editor.selectSongPart(spt, true);
                editor.setFocusOnSongPart(spt);
                editor.makeSptViewerVisible(spt);
            }
        }
    }
}
