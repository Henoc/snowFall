package snowFall;

import java.io.FileInputStream;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class MIDIControl {
	Sequencer sequencer;

	public MIDIControl(String name) {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY); // 無限ループ
			sequencer.open();
			FileInputStream in = new FileInputStream(name);
			Sequence sequence = MidiSystem.getSequence(in);
			in.close();
			sequencer.setSequence(sequence);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play() {
		sequencer.start();
	}

	public void stop() {
		if (sequencer.isRunning()) {
			sequencer.stop();
		}
	}

	public void close() {
		stop();
		sequencer.close();
	}
}