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

import org.jjazz.ui.ss_editor.api.RL_ContextActionSupport;
import org.jjazz.ui.ss_editor.api.RL_ContextActionListener;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.NAME;
import javax.swing.KeyStroke;
import org.jjazz.rhythm.parameters.RhythmParameter;
import static org.jjazz.ui.ss_editor.actions.Bundle.*;
import org.jjazz.ui.ss_editor.api.RL_SelectionUtilities;
import org.jjazz.songstructure.api.SongPartParameter;
import org.jjazz.undomanager.JJazzUndoManagerFinder;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.jjazz.songstructure.api.SongStructure;
import org.jjazz.songstructure.api.SongPart;

@ActionID(category = "JJazz", id = "org.jjazz.ui.rl_editor.actions.nextrpvalue")
@ActionRegistration(displayName = "#CTL_NextRpValue", lazy = false)
@ActionReferences(
        {
            @ActionReference(path = "Actions/RhythmParameter", position = 400),
        })
@Messages("CTL_NextRpValue=Next Value")
public final class NextRpValue extends AbstractAction implements ContextAwareAction, RL_ContextActionListener
{

    private Lookup context;
    private RL_ContextActionSupport cap;
    private String undoText = CTL_NextRpValue();
    private static final Logger LOGGER = Logger.getLogger(NextRpValue.class.getSimpleName());

    public NextRpValue()
    {
        this(Utilities.actionsGlobalContext());
    }

    public NextRpValue(Lookup context)
    {
        this.context = context;
        cap = RL_ContextActionSupport.getInstance(this.context);
        cap.addListener(this);
        putValue(NAME, CTL_NextRpValue());                          // For popupmenu display only
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control UP"));    // For popupmenu display only
    }

    @SuppressWarnings(
            {
                "unchecked", "rawtypes"
            })
    @Override
    public void actionPerformed(ActionEvent e)
    {
        RL_SelectionUtilities selection = cap.getSelection();
        SongStructure sgs = selection.getModel();
        LOGGER.log(Level.FINE, "actionPerformed() sgs=" + sgs + " selection=" + selection);
        JJazzUndoManagerFinder.getDefault().get(sgs).startCEdit(undoText);
        for (SongPartParameter sptp : selection.getSelectedSongPartParameters())
        {
            RhythmParameter rp = sptp.getRp();
            SongPart spt = sptp.getSpt();
            Object newValue = rp.getNextValue(spt.getRPValue(rp));
            sgs.setRhythmParameterValue(spt, rp, newValue);
        }
        JJazzUndoManagerFinder.getDefault().get(sgs).endCEdit(undoText);
    }

    @Override
    public void selectionChange(RL_SelectionUtilities selection)
    {
        boolean b = selection.isRhythmParameterSelected();
        setEnabled(b);
        LOGGER.log(Level.FINE, "selectionChange() b=" + b);
    }

    @Override
    public Action createContextAwareInstance(Lookup context)
    {
        return new NextRpValue(context);
    }
}
