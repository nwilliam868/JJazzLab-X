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
package org.jjazz.midi.recorder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
import org.jjazz.midi.JJazzMidiSystem;
import org.jjazz.midi.MidiConst;
import org.jjazz.midi.MidiUtilities;
import org.jjazz.util.Utilities;

/**
 * Record and store incoming Note_ON/OFF and Controller Midi messages.
 */
public class MidiRecorder implements MetaEventListener
{

    private static final int MSG_BUFFER_SIZE = 0x100000;     // 1M messages
    private static final String START_MARKER = "Recording Start";
    private static final Logger LOGGER = Logger.getLogger(MidiRecorder.class.getSimpleName());


    private final Transmitter midiInTransmitter;
    private final MyReceiver myReceiver;
    private final boolean useMidiEventTimeStamps;
    private long usOffset = -1;
    private final long[] midiMessagesUsPositions = new long[MSG_BUFFER_SIZE]; // In micro-seconds, 0 is start of sequence
    private final ShortMessage[] midiMessages = new ShortMessage[MSG_BUFFER_SIZE];
    private int nbMessages;
    private boolean recording;
    private boolean recordingOccured;
    private final long seqStartInPPQ;
    private final Sequence sequence;

    /**
     * Create an object ready to record Midi input in sync with the specified playback sequence.
     * <p>
     * Once created it will capture Midi input if isRecordingEnabled() is true, and as soon as JJazzMidiSystem default sequencer
     * is started (no need to call Sequencer.enableRecording()). Input data is stored internally. Use fillTrack() to actually add
     * MidiEvents to a sequence's track.
     * <p>
     * If not already present a special marker MetaEvent will be inserted on track 0 at position seqStartInPPQ.
     *
     * @param seq Must contain at least one playback track.
     * @param seqStartInPPQ The position in ticks where playback will start.
     * @throws MidiUnavailableException
     */
    public MidiRecorder(Sequence seq, long seqStartInPPQ) throws MidiUnavailableException
    {
        if (seq == null || seqStartInPPQ < 0 || seq.getTracks().length == 0)
        {
            throw new IllegalArgumentException("seq=" + seq + " seqStartInPPQ=" + seqStartInPPQ);
        }
        this.sequence = seq;
        this.seqStartInPPQ = seqStartInPPQ;


        addStartMarkerEvent(this.sequence, this.seqStartInPPQ);


        // Does Midi IN device supports time stamping ?
        var jms = JJazzMidiSystem.getInstance();
        var midiIn = jms.getDefaultInDevice();      // Don't use getJJazzMidiInDevice() here, we need getMicrosecondPosition() support
        assert midiIn != null;
        useMidiEventTimeStamps = midiIn.getMicrosecondPosition() != -1;


        LOGGER.fine("MidiRecorder() ppqOffset=" + seqStartInPPQ + " useMidiEventTimeStamps=" + useMidiEventTimeStamps);


        // Connect Midi IN to our receiver
        myReceiver = new MyReceiver();
        midiInTransmitter = midiIn.getTransmitter();        // Possible MidiUnavailableException here
        midiInTransmitter.setReceiver(myReceiver);


        // To be notified as soon as sequencer starts
        jms.getDefaultSequencer().addMetaEventListener(this);

    }

    /**
     * Use the recorded Midi data to fill the specified track.
     * <p>
     * Do nothing is no recording done yet. Take into account possible SongPart tempo factor controller events.
     *
     * @param track Track must be part of the current sequence.
     * @param songTempoFactor A value in the range [0.5;2]. Sequence actual tempo=songTempoFactor * MidiConstSEQUENCER_REF_TEMPO.
     */
    public void fillTrack(Track track, double songTempoFactor)
    {
        assert Arrays.asList(sequence.getTracks()).contains(track);

        if (!isRecordingOccured())
        {
            return;
        }


        // Collect all tempo changes (tempo is required for tick-us conversion)
        // TempoChanges positions are relative to seqStartInPPQ
        Track track0 = sequence.getTracks()[0];
        var tempoFactorChangeEvents = MidiUtilities.getMidiEvents(track0, ShortMessage.class,
                sm -> sm.getCommand() == ShortMessage.CONTROL_CHANGE && sm.getData1() == MidiConst.CTRL_CHG_JJAZZ_TEMPO_FACTOR,
                seqStartInPPQ, Long.MAX_VALUE);
        var tempoChanges = tempoFactorChangeEvents.stream()
                .map(me -> new TempoChange(me.getTick() - seqStartInPPQ, MidiUtilities.getTempoFactor((ShortMessage) me.getMessage())))
                .collect(Collectors.toList());


        // Add an initial tempo change if not already present
        if (tempoChanges.isEmpty() || tempoChanges.get(0).tickPPQ > 0)
        {
            MidiEvent lastTempoEvent = MidiUtilities.getLastMidiEvent(track0, ShortMessage.class,
                    sm -> sm.getCommand() == ShortMessage.CONTROL_CHANGE && sm.getData1() == MidiConst.CTRL_CHG_JJAZZ_TEMPO_FACTOR,
                    seqStartInPPQ);


            double tempo = MidiConst.SEQUENCER_REF_TEMPO * songTempoFactor;
            if (lastTempoEvent != null)
            {
                // There is a SongPart tempo factor
                tempo *= MidiUtilities.getTempoFactor((ShortMessage) lastTempoEvent.getMessage());
            }

            TempoChange tfc = new TempoChange(0, tempo);
            tempoChanges.add(0, tfc);
        }


        LOGGER.fine("fillTrack() tempoChanges=" + tempoChanges);


        // Add MidiEvents
        for (int i = 0; i < nbMessages; i++)
        {
            ShortMessage sm = midiMessages[i];
            long usTick = midiMessagesUsPositions[i];
            long ppqTick = toPPQTick(usTick, tempoChanges) + seqStartInPPQ;
            // LOGGER.severe("fillTrack() usTick=" + usTick + " ppqTick=" + ppqTick);
            MidiEvent me = new MidiEvent(sm, ppqTick);
            track.add(me);
        }
    }

    public void setRecordingEnabled(boolean b)
    {
        recording = b;
        if (recording)
        {
            recordingOccured = true;
        }
    }

    public boolean isRecordingEnabled()
    {
        return recording;
    }

    /**
     * True if recording was enabled at least once since the creation of this object.
     *
     * @return
     */
    public boolean isRecordingOccured()
    {
        return recordingOccured;
    }

    /**
     * Must be called when this object is not used anymore.
     */
    public void cleanup()
    {
        JJazzMidiSystem.getInstance().getDefaultSequencer().removeMetaEventListener(this);
        myReceiver.close();
        midiInTransmitter.close();
    }

    public void dump()
    {
        LOGGER.info("dump() MidiRecorder:");
        for (int i = 0; i < nbMessages; i++)
        {
            LOGGER.info(String.format(" 0x%04x: %s  usPos=%d", i, MidiUtilities.toString(midiMessages[i], -1), midiMessagesUsPositions[i]));
        }
    }

    //-----------------------------------------------------------------------
    // Implementation of the MetaEventListener interface
    //-----------------------------------------------------------------------
    @Override
    public void meta(MetaMessage meta)
    {
//        LOGGER.log(Level.SEVERE, "meta() msg={0} data={1}", new Object[]
//        {
//            MidiUtilities.toString(meta, -1), Utilities.toString(meta.getData())
//        });
        if (usOffset == -1)
        {
            if (meta.getType() == 6 && START_MARKER.equals(Utilities.toString(meta.getData())))
            {
                usOffset = useMidiEventTimeStamps ? JJazzMidiSystem.getInstance().getDefaultInDevice().getMicrosecondPosition() : System.nanoTime() / 1000;
                assert usOffset != -1;
                LOGGER.log(Level.FINE, "meta() useTimeStamp={0} usOffset={1}", new Object[]
                {
                    useMidiEventTimeStamps, usOffset
                });
            }
        }
    }

    // ==========================================================================================
    // Private methods
    // ==========================================================================================    
    /**
     * Add a MetaEvent marker to be notified as soon as playback starts.
     *
     * @param sequence
     * @param seqStartInPPQ
     */
    private void addStartMarkerEvent(Sequence sequence, long seqStartInPPQ)
    {
        Track track = sequence.getTracks()[0];

        assert seqStartInPPQ < track.ticks() : "seqStartInPPQ=" + seqStartInPPQ + " track.ticks()=" + track.ticks();

        List<MidiEvent> res = MidiUtilities.getMidiEvents(track, MetaMessage.class, mm
                -> mm.getType() == 6 && Utilities.toString(mm.getData()).equals(START_MARKER),
                seqStartInPPQ, seqStartInPPQ);


        if (res.isEmpty())
        {
            // Add our special marker
            MidiMessage mm = MidiUtilities.getMarkerMetaMessage(START_MARKER);
            track.add(new MidiEvent(mm, seqStartInPPQ));
        }
    }

    /**
     * Convert a tick in micro-seconds into a tick in PPQ.
     *
     * @param usTick Position in micro-second relative to seqStartInPPQ
     * @param tempoChanges Ticks must be relative to seqStartInPPQ. Must contain at least one change at tick 0.
     * @return
     */
    private long toPPQTick(long usTick, List<TempoChange> tempoChanges)
    {
        int nbTempoChanges = tempoChanges.size();
        assert nbTempoChanges > 0 && tempoChanges.get(0).tickPPQ == 0 :
                "tempoChanges=" + tempoChanges;


        // PPQ depends on tempo, so we must take into account all tempo changes from start
        long prevTempoPPQTick = tempoChanges.get(0).tickPPQ;
        double prevTempoMPQ = tempoChanges.get(0).tempoMPQ;
        long lastTempoTickinUs = 0;


        for (int i = 1; i < nbTempoChanges; i++)
        {
            long curTempoPPQTick = tempoChanges.get(i).tickPPQ;
            prevTempoPPQTick = tempoChanges.get(i - 1).tickPPQ;
            prevTempoMPQ = tempoChanges.get(i - 1).tempoMPQ;

            long curTempoUsTick = lastTempoTickinUs + MidiUtilities.toTickInUs(curTempoPPQTick - prevTempoPPQTick, prevTempoMPQ, sequence.getResolution());
            if (curTempoUsTick > usTick)
            {
                break;
            }
            lastTempoTickinUs = curTempoUsTick;
        }

        long res = prevTempoPPQTick + MidiUtilities.toTickInPPQ(usTick - lastTempoTickinUs, prevTempoMPQ, sequence.getResolution());

        return res;

    }

    // ==========================================================================================
    // Inner classes
    // ==========================================================================================    
    private class TempoChange
    {

        public long tickPPQ;
        public double tempoBPM;
        public double tempoMPQ;

        public TempoChange(long tickPPQ, double tempoBPM)
        {
            this.tickPPQ = tickPPQ;
            this.tempoBPM = tempoBPM;
            this.tempoMPQ = MidiUtilities.toTempoMPQ(tempoBPM);
            // LOGGER.severe("TempoChange() tickPPQ=" + tickPPQ + " tempoBPM=" + tempoBPM + " tempoMPQ=" + tempoMPQ);
        }

        @Override
        public String toString()
        {
            return "tickPPQ=" + tickPPQ + ",tempoBPM=" + tempoBPM;
        }
    }

    private class MyReceiver implements Receiver
    {

        /**
         * Store incoming Note_ON/OFF and controller messages.
         *
         * @param msg
         * @param timeStamp
         */
        @Override
        public void send(MidiMessage msg, long timeStamp)
        {

            if (!recording || !(msg instanceof ShortMessage))
            {
                return;
            }

            ShortMessage sm = (ShortMessage) msg;
            if ((sm.getStatus() & 0xF0) == 0xF0)
            {
                // All real-time messages have 0xF in the high nibble of the status byte
                return;
            }

            // LOGGER.severe("send() " + MidiUtilities.toString(msg, timeStamp));

            if (nbMessages >= MSG_BUFFER_SIZE)
            {
                LOGGER.log(Level.WARNING, "send() Midi message input buffer is full, ignoring message {0} timeStamp={1}", new Object[]
                {
                    sm, timeStamp
                });
                return;
            }


            if (usOffset >= 0)
            {
                midiMessages[nbMessages] = sm;
                midiMessagesUsPositions[nbMessages] = (useMidiEventTimeStamps ? timeStamp : System.nanoTime() / 1000) - usOffset;
                nbMessages++;
            } else
            {
                LOGGER.log(Level.WARNING, "send() Unexpected incoming Midi message={0} timeStamp={1} while usOffset={2}", new Object[]
                {
                    sm, timeStamp, usOffset
                });
            }

        }

        @Override
        public void close()
        {
            LOGGER.fine("close() --");
        }

    }

}