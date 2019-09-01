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
package org.jjazz.instrumentsoptions;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.jjazz.defaultinstruments.DefaultInstruments;
import org.jjazz.defaultinstruments.Delegate2DefaultInstrument;
import org.jjazz.midi.Instrument;
import org.jjazz.instrumentchooser.api.InstrumentChooserDialog;
import org.jjazz.rhythm.api.RvType;
import org.jjazz.util.Filter;

final class DefaultInstrumentsPanel extends javax.swing.JPanel
{

    private final DefaultInstrumentsOptionsPanelController controller;
    private InstrumentChooserDialog chooserDlg = InstrumentChooserDialog.getDefault();
    private Instrument ins1, ins2, ins3, ins4, ins5, ins6, ins7, ins8, ins9;
    private int transpose1, transpose2, transpose3, transpose4, transpose5, transpose6, transpose7, transpose8, transpose9;
    private NoDelegateFilter insFilter = new NoDelegateFilter();

    DefaultInstrumentsPanel(DefaultInstrumentsOptionsPanelController controller)
    {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
    }

    private String getBtnString(Instrument ins)
    {
        if (ins == null)
        {
            return "Not Set";
        }
        return ins.getPatchName();
    }

    private Instrument showDialog(Instrument ins, int transp, String typeName)
    {
        String title = "Choose default instrument for " + typeName;
        chooserDlg.preset(ins, transp, 1, title, insFilter);           // Showing delegate instruments is a non-sense!
        chooserDlg.setVisible(true);
        return chooserDlg.getSelectedInstrument();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of
     * this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        helpTextArea1 = new org.jjazz.ui.utilities.HelpTextArea();
        jPanel1 = new javax.swing.JPanel();
        lbl_ins1 = new javax.swing.JLabel();
        btn_ins1 = new javax.swing.JButton();
        lbl_ins2 = new javax.swing.JLabel();
        btn_ins2 = new javax.swing.JButton();
        lbl_ins3 = new javax.swing.JLabel();
        btn_ins3 = new javax.swing.JButton();
        btn_ins4 = new javax.swing.JButton();
        btn_ins6 = new javax.swing.JButton();
        btn_ins7 = new javax.swing.JButton();
        btn_ins8 = new javax.swing.JButton();
        btn_ins5 = new javax.swing.JButton();
        lbl_ins4 = new javax.swing.JLabel();
        lbl_ins5 = new javax.swing.JLabel();
        lbl_ins6 = new javax.swing.JLabel();
        btn_ins9 = new javax.swing.JButton();
        lbl_ins7 = new javax.swing.JLabel();
        lbl_ins8 = new javax.swing.JLabel();
        lbl_ins9 = new javax.swing.JLabel();

        helpTextArea1.setColumns(20);
        helpTextArea1.setRows(5);
        helpTextArea1.setText(org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.helpTextArea1.text")); // NOI18N

        lbl_ins1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(lbl_ins1, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.lbl_ins1.text")); // NOI18N
        lbl_ins1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        org.openide.awt.Mnemonics.setLocalizedText(btn_ins1, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.btn_ins1.text")); // NOI18N
        btn_ins1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ins1ActionPerformed(evt);
            }
        });

        lbl_ins2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(lbl_ins2, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.lbl_ins2.text")); // NOI18N
        lbl_ins2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        org.openide.awt.Mnemonics.setLocalizedText(btn_ins2, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.btn_ins2.text")); // NOI18N
        btn_ins2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ins2ActionPerformed(evt);
            }
        });

        lbl_ins3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(lbl_ins3, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.lbl_ins3.text")); // NOI18N
        lbl_ins3.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        org.openide.awt.Mnemonics.setLocalizedText(btn_ins3, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.btn_ins3.text")); // NOI18N
        btn_ins3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ins3ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btn_ins4, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.btn_ins4.text")); // NOI18N
        btn_ins4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ins4ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btn_ins6, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.btn_ins6.text")); // NOI18N
        btn_ins6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ins6ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btn_ins7, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.btn_ins7.text")); // NOI18N
        btn_ins7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ins7ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btn_ins8, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.btn_ins8.text")); // NOI18N
        btn_ins8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ins8ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btn_ins5, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.btn_ins5.text")); // NOI18N
        btn_ins5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ins5ActionPerformed(evt);
            }
        });

        lbl_ins4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(lbl_ins4, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.lbl_ins4.text")); // NOI18N
        lbl_ins4.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        lbl_ins5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(lbl_ins5, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.lbl_ins5.text")); // NOI18N
        lbl_ins5.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        lbl_ins6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(lbl_ins6, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.lbl_ins6.text")); // NOI18N
        lbl_ins6.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        org.openide.awt.Mnemonics.setLocalizedText(btn_ins9, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.btn_ins9.text")); // NOI18N
        btn_ins9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ins9ActionPerformed(evt);
            }
        });

        lbl_ins7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(lbl_ins7, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.lbl_ins7.text")); // NOI18N
        lbl_ins7.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        lbl_ins8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(lbl_ins8, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.lbl_ins8.text")); // NOI18N
        lbl_ins8.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        lbl_ins9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(lbl_ins9, org.openide.util.NbBundle.getMessage(DefaultInstrumentsPanel.class, "DefaultInstrumentsPanel.lbl_ins9.text")); // NOI18N
        lbl_ins9.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_ins9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_ins9, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_ins3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_ins2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_ins1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_ins4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btn_ins4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_ins3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_ins2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_ins1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_ins6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_ins7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_ins8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_ins5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_ins5, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_ins6, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_ins7, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_ins8, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_ins1)
                    .addComponent(btn_ins1)
                    .addComponent(btn_ins5)
                    .addComponent(lbl_ins5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_ins2)
                            .addComponent(btn_ins2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_ins3)
                            .addComponent(lbl_ins3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_ins4)
                            .addComponent(lbl_ins4)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_ins6)
                            .addComponent(lbl_ins6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_ins7)
                            .addComponent(lbl_ins7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_ins8)
                            .addComponent(lbl_ins8))))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_ins9)
                    .addComponent(lbl_ins9))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(helpTextArea1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(helpTextArea1, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ins1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_ins1ActionPerformed
    {//GEN-HEADEREND:event_btn_ins1ActionPerformed
        Instrument ins = showDialog(ins1, transpose1, lbl_ins1.getText());
        if (ins != null)
        {
            ins1 = ins;
            transpose1 = chooserDlg.getTransposition();
            updateButton(null, null, btn_ins1, ins1);
        }
    }//GEN-LAST:event_btn_ins1ActionPerformed

    private void btn_ins2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_ins2ActionPerformed
    {//GEN-HEADEREND:event_btn_ins2ActionPerformed
        Instrument ins = showDialog(ins2, transpose2, lbl_ins2.getText());
        if (ins != null)
        {
            ins2 = ins;
            transpose2 = chooserDlg.getTransposition();
            updateButton(null, null, btn_ins2, ins2);
        }
    }//GEN-LAST:event_btn_ins2ActionPerformed

    private void btn_ins3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_ins3ActionPerformed
    {//GEN-HEADEREND:event_btn_ins3ActionPerformed
        Instrument ins = showDialog(ins3, transpose3, lbl_ins3.getText());
        if (ins != null)
        {
            ins3 = ins;
            transpose3 = chooserDlg.getTransposition();
            updateButton(null, null, btn_ins3, ins3);
        }
    }//GEN-LAST:event_btn_ins3ActionPerformed

    private void btn_ins4ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_ins4ActionPerformed
    {//GEN-HEADEREND:event_btn_ins4ActionPerformed
        Instrument ins = showDialog(ins4, transpose4, lbl_ins4.getText());
        if (ins != null)
        {
            ins4 = ins;
            transpose4 = chooserDlg.getTransposition();
            updateButton(null, null, btn_ins4, ins4);
        }
    }//GEN-LAST:event_btn_ins4ActionPerformed

    private void btn_ins5ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_ins5ActionPerformed
    {//GEN-HEADEREND:event_btn_ins5ActionPerformed
        Instrument ins = showDialog(ins5, transpose5, lbl_ins5.getText());
        if (ins != null)
        {
            ins5 = ins;
            transpose5 = chooserDlg.getTransposition();
            updateButton(null, null, btn_ins5, ins5);
        }
    }//GEN-LAST:event_btn_ins5ActionPerformed

    private void btn_ins6ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_ins6ActionPerformed
    {//GEN-HEADEREND:event_btn_ins6ActionPerformed
        Instrument ins = showDialog(ins6, transpose6, lbl_ins6.getText());
        if (ins != null)
        {
            ins6 = ins;
            transpose6 = chooserDlg.getTransposition();
            updateButton(null, null, btn_ins6, ins6);
        }
    }//GEN-LAST:event_btn_ins6ActionPerformed

    private void btn_ins7ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_ins7ActionPerformed
    {//GEN-HEADEREND:event_btn_ins7ActionPerformed
        Instrument ins = showDialog(ins7, transpose7, lbl_ins7.getText());
        if (ins != null)
        {
            ins7 = ins;
            transpose7 = chooserDlg.getTransposition();
            updateButton(null, null, btn_ins7, ins7);
        }
    }//GEN-LAST:event_btn_ins7ActionPerformed

    private void btn_ins8ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_ins8ActionPerformed
    {//GEN-HEADEREND:event_btn_ins8ActionPerformed
        Instrument ins = showDialog(ins8, transpose8, lbl_ins8.getText());
        if (ins != null)
        {
            ins8 = ins;
            transpose8 = chooserDlg.getTransposition();
            updateButton(null, null, btn_ins8, ins8);
        }
    }//GEN-LAST:event_btn_ins8ActionPerformed

    private void btn_ins9ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_ins9ActionPerformed
    {//GEN-HEADEREND:event_btn_ins9ActionPerformed
        Instrument ins = showDialog(ins9, transpose9, lbl_ins9.getText());
        if (ins != null)
        {
            ins9 = ins;
            transpose9 = chooserDlg.getTransposition();
            updateButton(null, null, btn_ins9, ins9);
        }
    }//GEN-LAST:event_btn_ins9ActionPerformed

    void load()
    {
        // TODO read settings and initialize GUI
        // Example:        
        // someCheckBox.setSelected(Preferences.userNodeForPackage(DefaultInstrumentsPanel.class).getBoolean("someFlag", false));
        // or for org.openide.util with API spec. version >= 7.4:
        // someCheckBox.setSelected(NbPreferences.forModule(DefaultInstrumentsPanel.class).getBoolean("someFlag", false));
        // or:
        // someTextField.setText(SomeSystemOption.getDefault().getSomeStringProperty()); 

        DefaultInstruments di = DefaultInstruments.getInstance();

        RvType t = RvType.Drums;
        ins1 = di.getInstrument(t);
        transpose1 = di.getTranspose(t);
        updateButton(lbl_ins1, t, btn_ins1, ins1);

        t = RvType.Bass;
        ins2 = di.getInstrument(t);
        transpose2 = di.getTranspose(t);
        updateButton(lbl_ins2, t, btn_ins2, ins2);

        t = RvType.Guitar;
        ins3 = di.getInstrument(t);
        transpose3 = di.getTranspose(t);
        updateButton(lbl_ins3, t, btn_ins3, ins3);;

        t = RvType.Keyboard;
        ins4 = di.getInstrument(t);
        transpose4 = di.getTranspose(t);
        updateButton(lbl_ins4, t, btn_ins4, ins4);

        t = RvType.Percussion;
        ins5 = di.getInstrument(t);
        transpose5 = di.getTranspose(t);
        updateButton(lbl_ins5, t, btn_ins5, ins5);

        t = RvType.Horn_Section;
        ins6 = di.getInstrument(t);
        transpose6 = di.getTranspose(t);
        updateButton(lbl_ins6, t, btn_ins6, ins6);

        t = RvType.Pad;
        ins7 = di.getInstrument(t);
        transpose7 = di.getTranspose(t);
        updateButton(lbl_ins7, t, btn_ins7, ins7);

        t = RvType.Other;
        ins8 = di.getInstrument(t);
        transpose8 = di.getTranspose(t);
        updateButton(lbl_ins8, t, btn_ins8, ins8);

        ins9 = di.getUserInstrument();
        transpose9 = di.getUserInstrumentTranspose();
        lbl_ins9.setIcon(getIcon(RvType.Other));
        lbl_ins9.setText("User Channel");
        btn_ins9.setText(getBtnString(ins9));
        btn_ins9.setToolTipText(ins9 == null ? "Click to set the default instrument" : ins9.getBank().getName());

    }

    private void updateButton(JLabel lbl, RvType t, JButton btn, Instrument ins)
    {
        if (lbl != null)
        {
            lbl.setText(t.toLongString());
            lbl.setIcon(getIcon(t));
        }
        btn.setText(getBtnString(ins));
        btn.setToolTipText(ins == null ? "-" : ins.getBank().getMidiSynth().getName() + " - " + ins.getBank().getName());
    }

    void store()
    {
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(DefaultInstrumentsPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or for org.openide.util with API spec. version >= 7.4:
        // NbPreferences.forModule(DefaultInstrumentsPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
        DefaultInstruments di = DefaultInstruments.getInstance();
        di.setInstrument(RvType.Drums, ins1);
        di.setTranspose(RvType.Drums, transpose1);
        di.setInstrument(RvType.Bass, ins2);
        di.setTranspose(RvType.Bass, transpose2);
        di.setInstrument(RvType.Guitar, ins3);
        di.setTranspose(RvType.Guitar, transpose3);
        di.setInstrument(RvType.Keyboard, ins4);
        di.setTranspose(RvType.Keyboard, transpose4);
        di.setInstrument(RvType.Percussion, ins5);
        di.setTranspose(RvType.Percussion, transpose5);
        di.setInstrument(RvType.Horn_Section, ins6);
        di.setTranspose(RvType.Horn_Section, transpose6);
        di.setInstrument(RvType.Pad, ins7);
        di.setTranspose(RvType.Pad, transpose7);
        di.setInstrument(RvType.Other, ins8);
        di.setTranspose(RvType.Other, transpose8);
        di.setUserInstrument(ins9);
        di.setUserInstrumentTranspose(transpose9);
    }

    boolean valid()
    {
        // TODO check whether form is consistent and complete
        return true;
    }

    private Icon getIcon(RvType type)
    {
        Icon icon;
        switch (type)
        {
            case Drums:
                icon = new ImageIcon(getClass().getResource("resources/Drums-48x48.png"));
                break;
            case Guitar:
                icon = new ImageIcon(getClass().getResource("resources/Guitar-48x48.png"));
                break;
            case Keyboard:
                icon = new ImageIcon(getClass().getResource("resources/Keyboard-48x48.png"));
                break;
            case Percussion:
                icon = new ImageIcon(getClass().getResource("resources/Percu-48x48.png"));
                break;
            case Bass:
                icon = new ImageIcon(getClass().getResource("resources/Bass-48x48.png"));
                break;
            case Horn_Section:
                icon = new ImageIcon(getClass().getResource("resources/HornSection-48x48.png"));
                break;
            case Pad:
                icon = new ImageIcon(getClass().getResource("resources/Strings-48x48.png"));
                break;
            default: // Accompaniment
                icon = new ImageIcon(getClass().getResource("resources/Notes-48x48.png"));
        }
        return icon;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_ins1;
    private javax.swing.JButton btn_ins2;
    private javax.swing.JButton btn_ins3;
    private javax.swing.JButton btn_ins4;
    private javax.swing.JButton btn_ins5;
    private javax.swing.JButton btn_ins6;
    private javax.swing.JButton btn_ins7;
    private javax.swing.JButton btn_ins8;
    private javax.swing.JButton btn_ins9;
    private org.jjazz.ui.utilities.HelpTextArea helpTextArea1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbl_ins1;
    private javax.swing.JLabel lbl_ins2;
    private javax.swing.JLabel lbl_ins3;
    private javax.swing.JLabel lbl_ins4;
    private javax.swing.JLabel lbl_ins5;
    private javax.swing.JLabel lbl_ins6;
    private javax.swing.JLabel lbl_ins7;
    private javax.swing.JLabel lbl_ins8;
    private javax.swing.JLabel lbl_ins9;
    // End of variables declaration//GEN-END:variables

    private class NoDelegateFilter implements Filter<Instrument>
    {

        @Override
        public boolean accept(Instrument ins)
        {
            return !(ins instanceof Delegate2DefaultInstrument);
        }
    }
}
