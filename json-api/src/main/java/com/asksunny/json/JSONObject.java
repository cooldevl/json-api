
package com.asksunny.json;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class JSONObject  {
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private HashMap<String, Object> attributes = null;
	
	
	public JSONObject() {
		attributes = new HashMap<String, Object>();
	}

	public JSONObject(int initialCapacity) {
		attributes = new HashMap<String, Object>(initialCapacity);
	}

	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttribute(String attName, Object attValue)
	{
		if(!isSupportedObjectType(attValue)) throw new JSONOperationException("Only primitive object type and JSONObject are accepted.");
		
		attributes.put(attName, attValue);	
	}
	
	public void put(String attName, Object attValue)
	{
		if(!isSupportedObjectType(attValue)) throw new JSONOperationException("Only primitive object type and JSONObject are accepted.");		
		attributes.put(attName, attValue);	
	}
	
	public Object get(String attName)
	{
		return attributes.get(attName);	
	}
	
	public Object getValue(String hirachyName)
	{
		JSONEditor editor = new  JSONEditor(this);
		return editor.get(hirachyName);
	}
	
	public List<Object> getValueAll(String hirachyName)
	{
		JSONEditor editor = new  JSONEditor(this);
		return editor.getAll(hirachyName);
	}
	
	public void putValue(String hirachyName, Object obj)
	{
		JSONEditor editor = new  JSONEditor(this);
		editor.put(hirachyName, obj);
	}
	
	public void putValueAll(String hirachyName, Object obj)
	{
		JSONEditor editor = new  JSONEditor(this);
		 editor.putAll(hirachyName, obj);
	}
	
	
	public Object getAttribute(String attName)
	{
		return attributes.get(attName);	
	}
	
	private boolean isSupportedObjectType(Object attValue)
	{
		return (attValue instanceof JSONObject  
				|| attValue instanceof CharSequence
				|| attValue instanceof Character
				|| attValue instanceof Double
				|| attValue instanceof Float
				|| attValue instanceof Long
				|| attValue instanceof BigDecimal
				|| attValue instanceof Integer
				|| attValue instanceof Short
				|| attValue instanceof Byte
				|| attValue instanceof Boolean				
				|| attValue instanceof CharSequence[]
				|| attValue instanceof Character[]
				|| attValue instanceof Double[]
				|| attValue instanceof Float[]
				|| attValue instanceof Long[]
				|| attValue instanceof BigDecimal[]
				|| attValue instanceof Integer[]
				|| attValue instanceof Short[]
				|| attValue instanceof Byte[]
				|| attValue instanceof Boolean[]			
				);
	}
	
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		Set<String> keys = this.attributes.keySet();
		//System.out.println("Key Size:" + keys.size());
		int size = keys.size();
		int cnt=0;
		buf.append("{");
		for (String key : keys) {
			//System.out.println("Key:" + key);
			Object obj = this.attributes.get(key);
			//System.out.println("VAL:" + obj.toString());
			cnt++;
			if(obj!=null){
				buf.append("\"").append(key).append("\":");
				if(obj instanceof CharSequence){					
					buf.append("\"");					
					buf.append(encodeJsonString((CharSequence)obj));
					buf.append("\"");
				}else{
					buf.append(obj.toString());					
				}
				if(cnt<size){
					buf.append(",");
				}
				
			}
			
		}
		buf.append("}");
		return buf.toString();
	}
	
	
	protected String encodeJsonString(CharSequence str){
		StringBuilder buf = new StringBuilder();
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c =  str.charAt(i);
			switch(c)
			{
			case '"':
				buf.append('\\').append('"');
				break;
			case '\\':
				buf.append('\\').append('\\');
				break;
			case '/':
				buf.append('\\').append('/');
				break;
			case '\b':
				buf.append('\\').append('b');
				break;
			case '\f':
				buf.append('\\').append('f');
				break;
			case '\n':
				buf.append('\\').append('n');
				break;
			case '\r':
				buf.append('\\').append('r');
				break;
			case '\t':
				buf.append('\\').append('t');
			break;
			default:
				buf.append(c);
				break;
			}
		}
		
		return buf.toString();
	}

}
