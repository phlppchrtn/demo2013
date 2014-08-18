package io.vertigo.nitro.tcp.io;

import java.util.HashMap;

public final class BlockingMap<K, V> {
	private final Object lock = new Object();
	// synchronized (lock)
	private final HashMap<K, V> backingMap = new HashMap<K, V>();

	public int size() {
		return backingMap.size();
	}
	
	public V get(Object key) {
		return backingMap.get(key);
	}

	public V getAndWait(Object key, long timeout) {
		try {
			return doGetAndWait(key, timeout);
		} catch (InterruptedException e) {
			return null;
		}
	}

	private V doGetAndWait(Object key, long timeout) throws InterruptedException {
		V value;
		synchronized (lock) {
		//	do {
				value = get(key);
				System.out.println(">>>>>>>>>"+value);
				if (value == null){
					lock.wait(timeout);
				}	
			//} while (value == null);
		}
		return value;
	}

	public V put(K key, V value) {
		synchronized (lock) {
			final V oldValue = backingMap.put(key, value);
			lock.notifyAll();
			return oldValue;
		}
	}
	
	public String toString(){
		return backingMap.toString();
	}
	
	public static void main(String[] args) throws InterruptedException {
		final BlockingMap<String,String> bmap= new BlockingMap<>();

		Thread t1 = new Thread( new Runnable() {
			@Override
			public void run() {
				System.out.println(">>"+bmap.getAndWait("aa", 1000));
				//bmap.put("bb", "titi");
			}
		});
		//---
		Thread t2 = new Thread( new Runnable() {
			@Override
			public void run() {
				bmap.put("aa", "toto");
				System.out.println("putting in aa");
				System.out.println(">>"+bmap.getAndWait("bb", 10000));
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
