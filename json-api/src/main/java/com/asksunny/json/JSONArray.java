package com.asksunny.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JSONArray extends JSONObject 
implements List<Object>
{
	private ArrayList<Object> Objects = null;
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	public JSONArray() {
		Objects = new ArrayList<Object>();
	}

	public JSONArray(int initialCapacity) 
	{	
		Objects = new ArrayList<Object>(initialCapacity);
	}
	

	public JSONArray(Collection<? extends Object> c) {
		Objects.addAll(c);
	}

	public boolean contains(Object o) {		
		return Objects.contains(o);
	}

	public Iterator<Object> iterator() {
		
		return Objects.iterator();
	}

	public Object[] toArray() {
		
		return Objects.toArray();
	}

	public <T> T[] toArray(T[] a) {
		
		return Objects.toArray(a);
	}

	public boolean add(Object e) {
		
		return Objects.add(e);
	}

	public boolean containsAll(Collection<?> c) {
		
		return Objects.containsAll(c);
	}

	public boolean addAll(Collection<? extends Object> c) {
		
		return Objects.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends Object> c) {
		
		return Objects.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		
		return Objects.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		
		return Objects.retainAll(c);
	}

	public Object get(int index) {
		
		return Objects.get(index);
	}

	public Object set(int index, Object element) {
		
		return Objects.set(index, element);
	}

	public void add(int index, Object element) {
		Objects.add(index, element);		
	}

	public Object remove(int index) {
		
		return Objects.remove(index);
	}

	public int indexOf(Object o) {		
		return Objects.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		
		return Objects.lastIndexOf(o);
	}

	public ListIterator<Object> listIterator() {
		
		return Objects.listIterator();
	}

	public ListIterator<Object> listIterator(int index) {
		
		return Objects.listIterator(index);
	}

	public List<Object> subList(int fromIndex, int toIndex) {
		
		return Objects.subList(fromIndex, toIndex);
	}

	public int size() {		
		return Objects.size();
	}

	public boolean isEmpty() {		
		return Objects.isEmpty();
	}

	public boolean remove(Object o) {		
		return Objects.remove(o);
	}

	public void clear() {		
		Objects.clear();
	}

	@Override
	public String toString() {		
		StringBuilder buf = new StringBuilder();
		buf.append("[");
		int size = this.Objects.size();
		int cnt = 0;
		for (Object obj : this.Objects) {
			cnt++;
			if(obj instanceof CharSequence){
				buf.append('"').append(encodeJsonString((CharSequence)obj)).append('"');
			}else{
				buf.append(obj.toString());
			}
			if(cnt<size) buf.append(",");
		}		
		buf.append("]");
		return buf.toString();
	}
	
}


