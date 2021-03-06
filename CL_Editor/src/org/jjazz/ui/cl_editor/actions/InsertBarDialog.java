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
package org.jjazz.ui.cl_editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.*;
import org.jjazz.leadsheet.chordleadsheet.api.ChordLeadSheet;
import org.openide.windows.WindowManager;

public final class InsertBarDialog extends javax.swing.JDialog
{

    private static InsertBarDialog INSTANCE;
    private boolean exitOk;
    private static final Logger LOGGER = Logger.getLogger(InsertBarDialog.class.getSimpleName());

    public static InsertBarDialog getInstance()
    {
        synchronized (InsertBarDialog.class)
        {
            if (INSTANCE == null)
            {
                INSTANCE = new InsertBarDialog(WindowManager.getDefault().getMainWindow(), true);
            }
        }
        return INSTANCE;
    }

    private InsertBarDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
    }

    /**
     * Prepare the dialog.
     *
     * @param cls The model on which to operate.
     * @param fromBar Insertion will start from this bar, must be in the range [0,cls.getSize], the latter to append a bar at the
     * end.
     * @param nbBars For this number of bars.
     */
    public void preset(ChordLeadSheet cls, int fromBar, int nbBars)
    {
        if (cls == null || fromBar < 0 || nbBars < 0 || fromBar > cls.getSize())
        {
            throw new IllegalArgumentException("cls=" + cls + " fromBar=" + fromBar + " nbBars=" + nbBars);
        }
        lblFromBar.setText(String.valueOf(fromBar + 1));
        spnNbBars.setValue(Integer.valueOf(Math.min(nbBars, 200)));
    }

    /**
     * Return the nb of bars to be inserted.
     * <p>
     * Value is valid only if exitOk() returns true.
     *
     * @return
     */
    public int getNbBars()
    {
        return (Integer) spnNbBars.getValue();
    }

    public boolean exitedOk()
    {
        return exitOk;
    }

    public void cleanup()
    {
        // Nothing
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of
     * this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel2 = new javax.swing.JLabel();
        lblFromBar = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        spnNbBars = new org.jjazz.ui.utilities.WheelSpinner();

        setTitle(org.openide.util.NbBundle.getMessage(InsertBarDialog.class, "InsertBarDialog.title")); // NOI18N
        addWindowFocusListener(new java.awt.event.WindowFocusListener()
        {
            public void windowGainedFocus(java.awt.event.WindowEvent evt)
            {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt)
            {
            }
        });

        jLabel2.setText(org.openide.util.NbBundle.getMessage(InsertBarDialog.class, "InsertBarDialog.jLabel2.text")); // NOI18N

        lblFromBar.setText(org.openide.util.NbBundle.getMessage(InsertBarDialog.class, "InsertBarDialog.lblFromBar.text")); // NOI18N

        jButton1.setText(org.openide.util.NbBundle.getMessage(InsertBarDialog.class, "InsertBarDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(org.openide.util.NbBundle.getMessage(InsertBarDialog.class, "InsertBarDialog.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton2ActionPerformed(evt);
            }
        });

        spnNbBars.setModel(new javax.swing.SpinnerNumberModel(1, 1, 100, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spnNbBars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFromBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblFromBar)
                    .addComponent(spnNbBars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
        actionOK();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton2ActionPerformed
    {//GEN-HEADEREND:event_jButton2ActionPerformed
        actionCancel();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowGainedFocus
    {//GEN-HEADEREND:event_formWindowGainedFocus
        spnNbBars.getDefaultEditor().getTextField().requestFocusInWindow();
    }//GEN-LAST:event_formWindowGainedFocus

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblFromBar;
    private org.jjazz.ui.utilities.WheelSpinner spnNbBars;
    // End of variables declaration//GEN-END:variables

    /**
     * Overridden to add global key bindings
     *
     * @return
     */
    @Override
    protected JRootPane createRootPane()
    {
        JRootPane contentPane = new JRootPane();
//        {
//
//            @Override
//            protected boolean processKeyBinding(KeyStroke ks, KeyEvent ke, int condition, boolean pressed)
//            {
//                boolean ret = super.processKeyBinding(ks, ke, condition, pressed);
//                LOGGER.severe("processKeyBinding() ks=" + ks + " ke=" + ke + " ret=" + ret);
//                return ret;
//            }
//
//            @Override
//            protected void processKeyEvent(KeyEvent ke)
//            {
//                LOGGER.severe("processKeyEvent() ke=" + ke);
//            }
//        };
        contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "actionOk");
        // HACK ! On Windows (I was not able to test this on Linux/Mac), when first showing the Dialog, if pressing ENTER directly,
        // contentPane's processKeyBinding() receives a "released ENTER" keystroke !!?? It's like the "pressed ENTER" was captured 
        // somewhere by the JSpinner, and we only receive the last part of the event.
        // If pressing ENTER again then it's always the correct "pressed ENTER", problem disappears.
        // So we add a specific entry for "released ENTER" as well...
        contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released ENTER"), "actionOk");
        contentPane.getActionMap().put("actionOk", new AbstractAction("OK")
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                actionOK();
            }
        });

        contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "actionCancel");
        contentPane.getActionMap().put("actionCancel", new AbstractAction("Cancel")
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                actionCancel();
            }
        });
        return contentPane;
    }

    private void actionOK()
    {
        exitOk = true;
        setVisible(false);
    }

    private void actionCancel()
    {
        exitOk = false;
        setVisible(false);
    }

}
