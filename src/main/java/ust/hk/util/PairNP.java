package ust.hk.util;

public class PairNP <K, V> {
	
	private K key;
	private V value;
	
	public PairNP(K k, V v) {
		key = k;
		value = v;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
//		System.out.println(obj.getClass() + "\t" + this.getClass());
		if (obj.getClass() != this.getClass()) return false;
		@SuppressWarnings("unchecked")
		PairNP<K, V> pair = (PairNP<K, V>) obj;
		if (this.key != null && pair.key != null && !this.key.equals(pair.key)) return false;
		if (this.value != null && pair.value != null && !this.value.equals(pair.value)) return false;
		return true;
	}

	@Override
	public String toString() {
		String content = "";
		if (key != null) content = key.toString().replaceAll("\n", " ");
		if (value != null) content += "\t" + value.toString().replaceAll("\n", " ");
		return content;
	}
	
	public K getKey() {
		return key;
	}
	
	public V getValue() {
		return value;
	}
}
