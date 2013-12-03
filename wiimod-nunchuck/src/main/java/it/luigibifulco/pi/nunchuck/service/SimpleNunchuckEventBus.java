package it.luigibifulco.pi.nunchuck.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import it.luigibifulco.pi.nunchuck.INunchuck;
import it.luigibifulco.pi.nunchuck.NunchuckCostants;
import it.luigibifulco.pi.nunchuck.NunchuckData;
import it.luigibifulco.pi.nunchuck.NunchuckListener;
import it.luigibifulco.pi.nunchuck.event.NunchuckEvent;
import it.luigibifulco.pi.nunchuck.event.NunchuckEventListener;

public class SimpleNunchuckEventBus implements NunchuckEventBus {

	protected static final int MAX_BUFFER_SIZE = 2;
	protected static final int MIN_SIZE = 1;
	protected static final int STATIONARY_THRESHOLD = 20;

	private INunchuck nunchuck;
	private Map<DataType, TreeSet<Integer>> buffer = new HashMap<DataType, TreeSet<Integer>>();
	private AtomicReference<JxStatus> jxStatus = new AtomicReference<SimpleNunchuckEventBus.JxStatus>(
			JxStatus.NONE);
	private List<NunchuckEventListener> motionEventListeners = new ArrayList<>();

	private static enum JxStatus {
		NONE, CENTER, LEFT, RIGHT;
	}

	private static enum DataType {
		JX;
	}

	public SimpleNunchuckEventBus(INunchuck nunchuck) {
		this.nunchuck = nunchuck;
	}

	@Override
	public void init() {
		nunchuck.startListen(new NunchuckListener() {

			@Override
			public void onNuncuckData(NunchuckData data) {
				processData(data, DataType.JX);
			}

		});
	}

	private int defaultIfEmpty(Integer value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	@Override
	public void addMotionEventListener(NunchuckEventListener nel) {
		motionEventListeners.add(nel);

	}

	@Override
	public void fireEvent(NunchuckEvent event) {
		// System.out.println(event.getType());
		if (!motionEventListeners.isEmpty())
			for (NunchuckEventListener nel : motionEventListeners) {
				nel.onMotionEvent(event);
			}
	}

	protected void processData(NunchuckData data, DataType type) {

		switch (type) {
		case JX:
			processJxData(data.getjX());
			return;
		default:
			return;
		}

	}

	private void processJxData(int jx) {
		TreeSet<Integer> set = buffer.get(DataType.JX);
		if (set == null) {
			set = new TreeSet<>(new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
			});
			buffer.put(DataType.JX, set);
		}
		if (set.size() <= MAX_BUFFER_SIZE) {
			set.add(jx);
		}
		if (set.size() > MIN_SIZE) {
			int lower = defaultIfEmpty(set.pollFirst(), 0);
			int higher = defaultIfEmpty(set.pollLast(), 0);
			int diff = higher - lower;
			if (diff >= 0 && diff <= STATIONARY_THRESHOLD) {
				setJxStatus(JxStatus.NONE, higher);
				return;
			} else if (higher >= (NunchuckCostants.JX_STATIONARY - STATIONARY_THRESHOLD)
					&& higher <= (NunchuckCostants.JX_STATIONARY + STATIONARY_THRESHOLD)) {
				setJxStatus(JxStatus.CENTER, higher);
				return;
			} else if (higher >= (NunchuckCostants.MAX_JX - STATIONARY_THRESHOLD)
					&& higher <= (NunchuckCostants.MAX_JX + STATIONARY_THRESHOLD)) {
				setJxStatus(JxStatus.RIGHT, higher);
				return;
			} else if (lower >= (NunchuckCostants.MIN_JX - STATIONARY_THRESHOLD)
					&& lower <= (NunchuckCostants.MIN_JX + STATIONARY_THRESHOLD)) {
				setJxStatus(JxStatus.LEFT, lower);
				return;
			}
		}
	}

	private void setJxStatus(JxStatus newStatus, int accel) {
		JxStatus oldStatus = this.jxStatus.getAndSet(newStatus);
		switch (newStatus) {
		case LEFT:
			fireEvent(new NunchuckEvent(NunchuckEvent.JOYSTICK_LEFT, accel));
			break;
		case RIGHT:
			fireEvent(new NunchuckEvent(NunchuckEvent.JOYSTICK_RIGHT, accel));
			break;
		case CENTER:
			fireEvent(new NunchuckEvent(NunchuckEvent.JOYSTICK_CENTER, accel));
			break;
		case NONE:

			break;
		default:
			break;
		}

	}
}
