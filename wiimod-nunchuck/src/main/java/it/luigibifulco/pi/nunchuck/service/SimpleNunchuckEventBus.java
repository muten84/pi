package it.luigibifulco.pi.nunchuck.service;

import it.luigibifulco.pi.nunchuck.INunchuck;
import it.luigibifulco.pi.nunchuck.NunchuckCostants;
import it.luigibifulco.pi.nunchuck.NunchuckData;
import it.luigibifulco.pi.nunchuck.NunchuckListener;
import it.luigibifulco.pi.nunchuck.event.NunchuckEvent;
import it.luigibifulco.pi.nunchuck.event.NunchuckEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleNunchuckEventBus implements NunchuckEventBus {

	protected ExecutorService worker;

	protected static final int MAX_BUFFER_SIZE = 4;
	protected static final int MIN_SIZE = 2;
	protected static final int STATIONARY_THRESHOLD = 5;

	private INunchuck nunchuck;
	private Map<DataType, TreeSet<Integer>> buffer = new HashMap<DataType, TreeSet<Integer>>();
	private AtomicReference<JxStatus> jxStatus = new AtomicReference<SimpleNunchuckEventBus.JxStatus>(
			JxStatus.NONE);
	private AtomicReference<JyStatus> jyStatus = new AtomicReference<SimpleNunchuckEventBus.JyStatus>(
			JyStatus.NONE);
	private List<NunchuckEventListener> motionEventListeners = new ArrayList<>();

	private static enum JxStatus {
		NONE, CENTER, LEFT, RIGHT;
	}

	private static enum JyStatus {
		NONE, CENTER, UP, DOWN;
	}

	private static enum DataType {
		JX, JY;
	}

	public SimpleNunchuckEventBus(INunchuck nunchuck) {
		this.nunchuck = nunchuck;
	}

	private class Task implements Callable<Boolean> {
		private NunchuckData data;
		private DataType type;

		public Task(NunchuckData data, DataType type) {
			this.data = data;
			this.type = type;
		}

		@Override
		public Boolean call() throws Exception {
			processData(data, type);
			return true;
		}
	}

	@Override
	public void init() {
		worker = Executors.newFixedThreadPool(2);
		nunchuck.startListen(new NunchuckListener() {

			@Override
			public void onNuncuckData(NunchuckData data) {
				Task jXTask = new Task(data, DataType.JX);
				Task jYTask = new Task(data, DataType.JY);
				Task[] tasks = new Task[] { jXTask, jYTask };
				try {
					List<Future<Boolean>> taskss = worker.invokeAll(Arrays
							.asList(tasks));
					for (Future<Boolean> future : taskss) {
						if (!future.isDone()) {
							future.get();
						}
					}
				} catch (InterruptedException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (ExecutionException e) {
					System.out.println("Error: " + e.getMessage());
				}
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
		case JY:
			processJyData(data.getjY());
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
			} else if ((lower >= (NunchuckCostants.JX_STATIONARY - STATIONARY_THRESHOLD) && lower <= (NunchuckCostants.JX_STATIONARY + STATIONARY_THRESHOLD))
					|| (higher >= (NunchuckCostants.JX_STATIONARY - STATIONARY_THRESHOLD) && higher <= (NunchuckCostants.JX_STATIONARY + STATIONARY_THRESHOLD))) {
				setJxStatus(JxStatus.CENTER, higher);
				return;
			} else if (higher >= (NunchuckCostants.MAX_JX - 50)
					&& higher <= (NunchuckCostants.MAX_JX + 50)) {
				setJxStatus(JxStatus.RIGHT, higher);
				return;
			} else if (lower >= (NunchuckCostants.MIN_JX - 0)
					&& lower <= (NunchuckCostants.JX_STATIONARY)) {
				setJxStatus(JxStatus.LEFT, lower);
				return;
			}
		}
	}

	private void processJyData(int jy) {
		TreeSet<Integer> set = buffer.get(DataType.JY);
		if (set == null) {
			set = new TreeSet<>(new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
			});
			buffer.put(DataType.JY, set);
		}
		if (set.size() <= MAX_BUFFER_SIZE) {
			set.add(jy);
		}
		if (set.size() > MIN_SIZE) {
			int lower = defaultIfEmpty(set.pollFirst(), 0);
			int higher = defaultIfEmpty(set.pollLast(), 0);
			int diff = higher - lower;
			if (diff >= 0 && diff <= STATIONARY_THRESHOLD) {
				setJyStatus(JyStatus.NONE, higher);
				return;
			} else if ((lower >= (NunchuckCostants.JY_STATIONARY - STATIONARY_THRESHOLD) && lower <= (NunchuckCostants.JY_STATIONARY + STATIONARY_THRESHOLD))
					|| (higher >= (NunchuckCostants.JY_STATIONARY - STATIONARY_THRESHOLD) && higher <= (NunchuckCostants.JY_STATIONARY + STATIONARY_THRESHOLD))) {
				setJyStatus(JyStatus.CENTER, higher);
				return;
			} else if (higher >= (NunchuckCostants.MAX_JY - 10)
					&& higher <= (NunchuckCostants.MAX_JY + 10)) {
				setJyStatus(JyStatus.UP, higher);
				return;
			} else if (lower >= (NunchuckCostants.MIN_JY - 0)
					&& lower <= (NunchuckCostants.JY_STATIONARY)) {
				setJyStatus(JyStatus.DOWN, lower);
				return;
			}
		}
	}

	private void setJxStatus(JxStatus newStatus, int accel) {
		JxStatus oldStatus = this.jxStatus.getAndSet(newStatus);
		if (oldStatus != newStatus) {
			// System.out.println(oldStatus + " - " + newStatus);
		}
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

	private void setJyStatus(JyStatus newStatus, int accel) {
		JyStatus oldStatus = this.jyStatus.getAndSet(newStatus);
		if (oldStatus != newStatus) {
			// System.out.println(oldStatus + " - " + newStatus);
		}
		switch (newStatus) {
		case UP:
			fireEvent(new NunchuckEvent(NunchuckEvent.JOYSTICK_UP, accel));
			break;
		case DOWN:
			fireEvent(new NunchuckEvent(NunchuckEvent.JOYSTICK_DOWN, accel));
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
