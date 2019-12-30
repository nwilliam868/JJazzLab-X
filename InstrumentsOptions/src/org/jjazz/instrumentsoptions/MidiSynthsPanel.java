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

import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import static org.jjazz.instrumentsoptions.Bundle.CTL_BuiltinSynth;
import static org.jjazz.instrumentsoptions.Bundle.ERR_BankNotGM1;
import static org.jjazz.instrumentsoptions.Bundle.ERR_NotSupportedExtension;
import org.jjazz.midi.GM1Bank;
import org.jjazz.midi.GMSynth;
import org.jjazz.midi.Instrument;
import org.jjazz.midi.InstrumentBank;
import org.jjazz.midi.MidiSynth;
import org.jjazz.synthmanager.api.MidiSynthManager;
import org.jjazz.synthmanager.spi.MidiSynthProvider;
import org.openide.*;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

@NbBundle.Messages(
        {
            "CTL_BuiltinSynth=Builtin Synth",
            "ERR_BankNotGM1=Selected bank is not a valid GM1 Bank with the 128 standard GM instruments.",
            "ERR_NotSupportedExtension=Not a valid file extension : "
        })
final class MidiSynthsPanel extends javax.swing.JPanel implements PropertyChangeListener
{

    private final MidiSynthsOptionsPanelController controller;
    private BankTableModel tableModel;
    private static final Logger LOGGER = Logger.getLogger(MidiSynthsPanel.class.getSimpleName());

    /**
     *
     * @param controller
     * @todo Get rid of the size in pixels
     */
    MidiSynthsPanel(MidiSynthsOptionsPanelController controller)
    {
        this.controller = controller;
        initComponents();

        // JTable parameters
        tableModel = new BankTableModel();
        tbl_Instruments.setModel(tableModel);
        tbl_Instruments.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tbl_Instruments.getTableHeader().setResizingAllowed(true);
        TableColumnModel cm = tbl_Instruments.getColumnModel();
        // Using pixels size, NOT nice but simple for a start...
        cm.getColumn(0).setPreferredWidth(130);
        cm.getColumn(1).setPreferredWidth(20);
        cm.getColumn(2).setPreferredWidth(20);
        cm.getColumn(3).setPreferredWidth(20);

        this.list_MidiSynths.setCellRenderer(new SynthCellRenderer());
        this.list_Banks.setCellRenderer(new BankCellRenderer());
        // TODO listen to changes in form fields and call controller.changed()

        // Listen to MidiSynthManager updates
        MidiSynthManager.getInstance().addPropertyChangeListener(this);
    }

    //-----------------------------------------------------------------------
    // Implementation of the PropertiesListener interface
    //-----------------------------------------------------------------------
    @SuppressWarnings(
            {
                "unchecked", "rawtypes"
            })
    @Override
    public void propertyChange(PropertyChangeEvent e)
    {
        if (e.getSource() == MidiSynthManager.getInstance())
        {
            if (e.getPropertyName() == MidiSynthManager.PROP_USER_SYNTH)
            {
                // A synth was added or removed : rebuild the MidiSynth list
                int saveIndex = list_MidiSynths.getSelectedIndex();
                List<MidiSynth> synths = MidiSynthManager.getInstance().getSynths();
                list_MidiSynths.setListData(synths.toArray(new MidiSynth[0]));
                saveIndex = saveIndex < synths.size() ? saveIndex : synths.size() - 1;
                if (saveIndex >= 0)
                {
                    list_MidiSynths.setSelectedIndex(saveIndex);
                }
            } else if (e.getPropertyName() == MidiSynthManager.PROP_GM1_DELEGATE_BANK)
            {
                updateCurrentGM1BankText((InstrumentBank) e.getNewValue());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of
     * this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jScrollPane1 = new javax.swing.JScrollPane();
        list_MidiSynths = new javax.swing.JList<>();
        btn_SetAsGM1Bank = new javax.swing.JButton();
        btn_AddSynth = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txt_CurrentGM1Bank = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        list_Banks = new javax.swing.JList<>();
        btn_RemoveSynth = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbl_Instruments = new javax.swing.JTable();
        helpTextArea1 = new org.jjazz.ui.utilities.HelpTextArea();

        list_MidiSynths.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list_MidiSynths.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                list_MidiSynthsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(list_MidiSynths);

        org.openide.awt.Mnemonics.setLocalizedText(btn_SetAsGM1Bank, org.openide.util.NbBundle.getMessage(MidiSynthsPanel.class, "MidiSynthsPanel.btn_SetAsGM1Bank.text")); // NOI18N
        btn_SetAsGM1Bank.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btn_SetAsGM1BankActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btn_AddSynth, org.openide.util.NbBundle.getMessage(MidiSynthsPanel.class, "MidiSynthsPanel.btn_AddSynth.text")); // NOI18N
        btn_AddSynth.setToolTipText(org.openide.util.NbBundle.getMessage(MidiSynthsPanel.class, "MidiSynthsPanel.btn_AddSynth.toolTipText")); // NOI18N
        btn_AddSynth.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btn_AddSynthActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(MidiSynthsPanel.class, "MidiSynthsPanel.jLabel5.text")); // NOI18N

        txt_CurrentGM1Bank.setEditable(false);
        txt_CurrentGM1Bank.setText(org.openide.util.NbBundle.getMessage(MidiSynthsPanel.class, "MidiSynthsPanel.txt_CurrentGM1Bank.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MidiSynthsPanel.class, "MidiSynthsPanel.jLabel1.text")); // NOI18N

        list_Banks.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list_Banks.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                list_BanksValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(list_Banks);

        org.openide.awt.Mnemonics.setLocalizedText(btn_RemoveSynth, org.openide.util.NbBundle.getMessage(MidiSynthsPanel.class, "MidiSynthsPanel.btn_RemoveSynth.text")); // NOI18N
        btn_RemoveSynth.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btn_RemoveSynthActionPerformed(evt);
            }
        });

        tbl_Instruments.setAutoCreateRowSorter(true);
        jScrollPane4.setViewportView(tbl_Instruments);

        helpTextArea1.setColumns(20);
        helpTextArea1.setRows(5);
        helpTextArea1.setText(org.openide.util.NbBundle.getMessage(MidiSynthsPanel.class, "MidiSynthsPanel.helpTextArea1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_RemoveSynth, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(123, 123, 123)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_SetAsGM1Bank, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(txt_CurrentGM1Bank)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_AddSynth, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addComponent(helpTextArea1, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_AddSynth, btn_RemoveSynth});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jScrollPane1, jScrollPane3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_AddSynth)
                            .addComponent(btn_SetAsGM1Bank))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_RemoveSynth)
                            .addComponent(txt_CurrentGM1Bank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(helpTextArea1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void list_MidiSynthsValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_list_MidiSynthsValueChanged
    {//GEN-HEADEREND:event_list_MidiSynthsValueChanged
        // Update the list of banks
        if (evt.getValueIsAdjusting())
        {
            return;
        }
        MidiSynth synth = list_MidiSynths.getSelectedValue();

        // Update banks
        clearBanks();
        if (synth != null)
        {
            List<InstrumentBank<?>> banks = synth.getBanks();
            if (!banks.isEmpty())
            {
                list_Banks.setListData(banks.toArray(new InstrumentBank<?>[0]));
                list_Banks.setSelectedIndex(0);
            }
        }
        btn_RemoveSynth.setEnabled(synth != null && !MidiSynthManager.getInstance().getBuiltinSynths().contains(synth));
    }//GEN-LAST:event_list_MidiSynthsValueChanged

    private void list_BanksValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_list_BanksValueChanged
    {//GEN-HEADEREND:event_list_BanksValueChanged
        // Update the bank details
        if (evt.getValueIsAdjusting())
        {
            return;
        }
        InstrumentBank<?> bank = list_Banks.getSelectedValue();
        tbl_Instruments.setEnabled(bank != null);
        tableModel.setBank(bank);
        InstrumentBank<?> gmBank = MidiSynthManager.getInstance().getGM1DelegateBank();
        if (gmBank == null)
        {
            gmBank = GMSynth.getInstance().getGM1Bank();
        }
        btn_SetAsGM1Bank.setEnabled(bank != null && bank != gmBank);

    }//GEN-LAST:event_list_BanksValueChanged

    private void btn_RemoveSynthActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_RemoveSynthActionPerformed
    {//GEN-HEADEREND:event_btn_RemoveSynthActionPerformed
        MidiSynth synth = this.list_MidiSynths.getSelectedValue();
        if (synth == null)
        {
            // Normally useless if button is correctly enabled
            return;
        }
        MidiSynthManager.getInstance().removeUserSynth(synth);
    }//GEN-LAST:event_btn_RemoveSynthActionPerformed

    private void btn_AddSynthActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_AddSynthActionPerformed
    {//GEN-HEADEREND:event_btn_AddSynthActionPerformed
        // First collect all file extensions managed by the MidiSynthProviders
        HashMap<String, MidiSynthProvider> mapExtProvider = new HashMap<>();
        List<FileNameExtensionFilter> allFilters = new ArrayList<>();
        for (MidiSynthProvider p : MidiSynthProvider.Utilities.getProviders())
        {
            List<FileNameExtensionFilter> filters = p.getSupportedFileTypes();
            for (FileNameExtensionFilter filter : filters)
            {
                allFilters.add(filter);
                for (String s : filter.getExtensions())
                {
                    mapExtProvider.put(s.toLowerCase(), p);
                }
            }
        }

        // Initialize the file chooser
        JFileChooser chooser = org.jjazz.ui.utilities.Utilities.getFileChooserInstance();
        chooser.resetChoosableFileFilters();
        for (FileNameExtensionFilter filter : allFilters)
        {
            chooser.addChoosableFileFilter(filter);
        }
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Add a Midi synth description file");
        chooser.setCurrentDirectory(MidiSynthManager.getInstance().getMidiSynthFilesDir());

        if (chooser.showOpenDialog(WindowManager.getDefault().getMainWindow()) != JFileChooser.APPROVE_OPTION)
        {
            // User cancel
            return;
        }

        // Process selected files
        File[] synthFiles = new File[]
        {
            chooser.getSelectedFile()           // We don't allow multi file selection anymore...
        };
        for (File f : synthFiles)
        {
            String ext = org.jjazz.util.Utilities.getExtension(f.getAbsolutePath());
            MidiSynthProvider p = mapExtProvider.get(ext.toLowerCase());
            if (p == null)
            {
                // Extension not managed by any MidiSynthProvider
                String msg = ERR_NotSupportedExtension() + f.getAbsolutePath();
                LOGGER.log(Level.WARNING, msg);
                NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                continue;
            }
            // Ask provider to read the file
            List<MidiSynth> synths = null;
            try
            {
                synths = p.getSynthsFromFile(f);
            } catch (IOException ex)
            {
                String msg = "Problem reading file : " + ex.getLocalizedMessage();
                LOGGER.log(Level.WARNING, msg);
                NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                continue;
            }
            // Add the non-empty synths
            for (MidiSynth synth : synths)
            {
                if (synth.getNbPatches() > 0)
                {
                    MidiSynthManager.getInstance().addUserSynth(p, synth);
                }
            }
        }
    }//GEN-LAST:event_btn_AddSynthActionPerformed

    private void btn_SetAsGM1BankActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_SetAsGM1BankActionPerformed
    {//GEN-HEADEREND:event_btn_SetAsGM1BankActionPerformed
        InstrumentBank<?> bank = list_Banks.getSelectedValue();
        if (bank == null)
        {
            // Should not happen if button is correctly enabled
            return;
        }
        if (bank == GMSynth.getInstance().getGM1Bank())
        {
            // Easy, no need of delegate since we want to use the standard GM1Bank
            MidiSynthManager.getInstance().setGM1DelegateBank(null, null);
            return;
        }
        if (!GM1Bank.isGM1Compatible(bank))
        {
            NotifyDescriptor d = new NotifyDescriptor.Message(ERR_BankNotGM1(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return;
        }
        // Ok let's set the delegate
        MidiSynthProvider p = MidiSynthManager.getInstance().getProvider(bank.getMidiSynth());
        assert p != null : "p=" + p + " bank=" + bank;
        MidiSynthManager.getInstance().setGM1DelegateBank(p, bank);
    }//GEN-LAST:event_btn_SetAsGM1BankActionPerformed

    void load()
    {
        // TODO read settings and initialize GUI
        // Example:        
        // someCheckBox.setSelected(Preferences.userNodeForPackage(MidiSynthsPanel.class).getBoolean("someFlag", false));
        // or for org.openide.util with API spec. version >= 7.4:
        // someCheckBox.setSelected(NbPreferences.forModule(MidiSynthsPanel.class).getBoolean("someFlag", false));
        // or:
        // someTextField.setText(SomeSystemOption.getDefault().getSomeStringProperty());
        MidiSynthManager msm = MidiSynthManager.getInstance();
        List<MidiSynth> synths = msm.getSynths();
        list_MidiSynths.setListData(synths.toArray(new MidiSynth[0]));
        if (!synths.isEmpty())
        {
            list_MidiSynths.setSelectedIndex(0);
        } else
        {
            clearBanks();
        }
        updateCurrentGM1BankText(MidiSynthManager.getInstance().getGM1DelegateBank());
    }

    private void updateCurrentGM1BankText(InstrumentBank<?> bank)
    {
        if (bank == null)
        {
            // It means we use the standard GM1 Bank
            bank = GMSynth.getInstance().getGM1Bank();
        }
        String s = bank.getMidiSynth().getName() + ":" + bank.getName();
        txt_CurrentGM1Bank.setText(s);
        txt_CurrentGM1Bank.setToolTipText(s);
    }

    private void clearBanks()
    {
        list_Banks.setListData(new InstrumentBank<?>[0]);
    }

    void store()
    {
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(MidiSynthsPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or for org.openide.util with API spec. version >= 7.4:
        // NbPreferences.forModule(MidiSynthsPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
    }

    boolean valid()
    {
        // TODO check whether form is consistent and complete
        return true;
    }

    private class SynthCellRenderer extends DefaultListCellRenderer
    {

        @Override
        @SuppressWarnings("rawtypes")
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            Component c = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            MidiSynth synth = (MidiSynth) value;
            String s = synth.getName() + " (" + synth.getNbPatches() + ")";
            setText(s);
            File f = synth.getFile();
            if (f == null)
            {
                s = CTL_BuiltinSynth();
                Font ft = getFont();
                setFont(ft.deriveFont(Font.ITALIC));
            } else
            {
                s = f.getName();
            }
            setToolTipText(s);
            return c;
        }
    }

    private class BankCellRenderer extends DefaultListCellRenderer
    {

        @Override
        @SuppressWarnings("rawtypes")
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            InstrumentBank<?> bank = (InstrumentBank<?>) value;
            setText(bank.getName() + " (" + bank.getSize() + ")");
            setToolTipText("Bank select method: " + bank.getBankSelectMethod().toString());
            return c;
        }
    }

    private class BankTableModel extends AbstractTableModel
    {

        InstrumentBank<? extends Instrument> bank;
        List<? extends Instrument> instruments;

        public void setBank(InstrumentBank<?> b)
        {
            bank = b;
            if (bank != null)
            {
                instruments = bank.getInstruments();
            }
            fireTableDataChanged();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            return columnIndex == 0 ? String.class : Integer.class;
        }

        @Override
        public String getColumnName(int columnIndex)
        {
            String s;
            switch (columnIndex)
            {
                case 3:
                    s = "LSB";
                    break;
                case 2:
                    s = "MSB";
                    break;
                case 1:
                    s = "PC";
                    break;
                case 0:
                    s = "Program Name";
                    break;
                default:
                    throw new IllegalStateException("columnIndex=" + columnIndex);
            }
            return s;
        }

        @Override
        public int getRowCount()
        {
            return bank == null ? 0 : bank.getSize();
        }

        @Override
        public int getColumnCount()
        {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            Instrument ins = instruments.get(rowIndex);
            switch (columnIndex)
            {
                case 3:
                    return ins.getBankSelectLSB();
                case 2:
                    return (Integer) ins.getBankSelectMSB();
                case 1:
                    return (Integer) ins.getProgramChange();
                case 0:
                    return ins.getPatchName();
                default:
                    throw new IllegalStateException("columnIndex=" + columnIndex);
            }
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AddSynth;
    private javax.swing.JButton btn_RemoveSynth;
    private javax.swing.JButton btn_SetAsGM1Bank;
    private org.jjazz.ui.utilities.HelpTextArea helpTextArea1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList<InstrumentBank<?>> list_Banks;
    private javax.swing.JList<MidiSynth> list_MidiSynths;
    private javax.swing.JTable tbl_Instruments;
    private javax.swing.JTextField txt_CurrentGM1Bank;
    // End of variables declaration//GEN-END:variables
}
