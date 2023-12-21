package io.vertigo.redis.alphaserver;

import java.util.HashMap;

/**
 * A BlockingMap
 * @author pchretien
 *
 */
public final class BlockingMap<K, V> {
	private final Object lock = new Object();
	// synchronized (lock)
	private final HashMap<K, V> backingMap = new HashMap<>();

	public int size() {
		return backingMap.size();
	}

	public V get(final Object key) {
		return backingMap.get(key);
	}

	public V getAndWait(final Object key, final long timeoutInMillis) {
		try {
			return doGetAndWait(key, timeoutInMillis);
		} catch (final InterruptedException e) {
			return null;
		}
	}

	private V doGetAndWait(final Object key, final long timeoutInMillis) throws InterruptedException {
		V value;
		synchronized (lock) {
			//	do {
			value = get(key);
			System.out.println(">>>>>>>>>" + value);
			if (value == null) {
				lock.wait(timeoutInMillis);
			}
			//} while (value == null);
		}
		return value;
	}

	public V put(final K key, final V value) {
		synchronized (lock) {
			final V oldValue = backingMap.put(key, value);
			lock.notifyAll();
			return oldValue;
		}
	}

	@Override
	public String toString() {
		return backingMap.toString();
	}

	public static void main(final String[] args) throws InterruptedException {
		final BlockingMap<String, String> bmap = new BlockingMap<>();

		final Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println(">>" + bmap.getAndWait("aa", 1000));
				//bmap.put("bb", "titi");
			}
		});
		//---
		final Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				bmap.put("aa", "toto");
				System.out.println("putting in aa");
				System.out.println(">>" + bmap.getAndWait("bb", 10000));
			}
		});

		t1.start();
		t2.start();
		//---
		t1.join();
		t2.join();
		System.out.println("map =" + bmap);
	}
}
