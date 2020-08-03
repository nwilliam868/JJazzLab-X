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
package org.jjazz.songmemoviewer.api;

import org.jjazz.songmemoviewer.SongMemoEditor;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.jjazz.songnotesviewer.api//SongNotes//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "SongNotesTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "org.jjazz.songnotesviewer.api.SongNotesTopComponent")
@ActionReference(path = "Menu/Window" , position = 1, separatorAfter = 2 )
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SongNotesAction",
        preferredID = "SongNotesTopComponent"
)
@Messages(
        {
            "CTL_SongNotesAction=Song Memo",
            "CTL_SongNotesTopComponent=Song Memo",
            "HINT_SongNotesTopComponent=This is a Song Memo window"
        })
public final class SongMemoTopComponent extends TopComponent
{

    private final SongMemoEditor editor;

    public SongMemoTopComponent()
    {
        initComponents();
        setName(Bundle.CTL_SongNotesTopComponent());
        setToolTipText(Bundle.HINT_SongNotesTopComponent());

        editor = new SongMemoEditor();
        add(editor);

    }

    /**
     * 
     * @return Can be null
     */
    static public SongMemoTopComponent getInstance()
    {
        return (SongMemoTopComponent) WindowManager.getDefault().findTopComponent("SongNotesTopComponent");
    }

    @Override
    public UndoRedo getUndoRedo()
    {
        return editor.getUndoManager();
    }

    @Override
    public Lookup getLookup()
    {
        return editor.getLookup();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened()
    {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed()
    {
        editor.cleanup();
    }

    void writeProperties(java.util.Properties p)
    {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p)
    {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
