package io.vertigo.redis.alphaserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class RedisQueue {
	private final List<String> values = new ArrayList<>();

	//	private final BlockingDeque<String> blockingDeque = new LinkedBlockingDeque<>();

	//		element = list.pollFirst(1L, TimeUnit.SECONDS);
	void lpushx(final String value) {
		//		blockingDeque.addFirst(value);
		values.add(0, value);
	}

	void rpushx(final String value) {
		//		blockingDeque.add(value);
		values.add(value);
	}

	int lrem(final int count, final String value) {
		//count > 0: Remove elements equal to value moving from head to tail.
		//count < 0: Remove elements equal to value moving from tail to head.
		//count = 0: Remove all elements equal to value.
		int removed = 0;

		if (count >= 0) {
			for (final Iterator<String> it = values.iterator(); it.hasNext();) {
				final String current = it.next();
				if (value == null) {
					if (current == null) {
						it.remove();
						removed++;
					}
				} else {
					if (value.equals(current)) {
						it.remove();
						removed++;
					}
				}
				if (removed == count && count > 0) {
					break;
				}
			}
		}
		return removed;
	}

	List<String> lrange(final int start, final int stop) {
		final int maxIdx;
		if (stop < 0) {
			maxIdx = values.size() - 1;
		} else {
			maxIdx = stop;
		}

		int index;
		if (start < 0) {
			//-1=> n
			//-2 => n-1
			//-100 => 0
			index = values.size() + start;
			if (index < 0) {
				index = 0;
			}
		} else {
			index = start;
		}
		if (index >= 0 && index < values.size()) {
			//				System.out.println(">>>>>>>>>>>>> index: " + index);
			//				System.out.println(">>>>>>>>>>>>> stop: " + stop);
			//				System.out.println(">>>>>>>>>>>>> size: " + values.size());
			final List<String> elements = new ArrayList<>();
			for (int i = index; i <= maxIdx; i++) {
				elements.add(values.get(i));
			}
			return elements;
		}
		return Collections.<String> emptyList();
	}

	public String lindex(final int idx) {
		int index;
		if (idx < 0) {
			index = values.size() + idx;
			if (index < 0) {
				index = 0;
			}
			//-1=> n
			//-2 => n-1
		} else {
			index = idx;
		}
		if (index >= 0 && index < values.size()) {
			return values.get(index);
		}
		return null;
	}

	int size() {
		return values.size();
		//			return blockingDeque.size();
	}

	String lpop() {
		if (values.isEmpty()) {
			return null;
		}
		return values.remove(0);
		//return blockingDeque.removeFirst();
	}

	String rpop() {
		if (values.isEmpty()) {
			return null;
		}
		return values.remove(values.size() - 1);
		//return blockingDeque.removeLast();
	}

	String blpop() {
		throw new UnsupportedOperationException();
		//			try {
		//			return blockingDeque.pollFirst(1L, TimeUnit.SECONDS);
		//			} catch (final InterruptedException e) {
		//				return  null;
		//			}
	}

	String brpop() {
		throw new UnsupportedOperationException();
		//			try {
		//			return blockingDeque.pollLast(1L, TimeUnit.SECONDS);
		//			} catch (final InterruptedException e) {
		//				return  null;
		//			}
	}
}
