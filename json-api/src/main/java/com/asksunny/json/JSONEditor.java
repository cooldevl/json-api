package com.asksunny.json;

import java.util.ArrayList;
import java.util.List;

import com.asksunny.json.parser.JSONObjectParser;
import com.asksunny.json.parser.JSONParsingException;



public class JSONEditor {

	Object jsonObject = null;

	
	void setJsonObject(Object jsonObject) {
		this.jsonObject = jsonObject;
	}
	JSONEditor(Object jsonObject){this.jsonObject = jsonObject;}
	
	public JSONEditor(String jsonString) {		
		try {
			JSONObjectParser parser = new JSONObjectParser(jsonString);
			jsonObject = parser.parse();
		} catch (Exception e) {
			throw new JSONParsingException("Invalid JSON String", e);
		}
	}
	
	public Object getNativeJSONObject()
	{
		return this.jsonObject;
	}
	
	/**
	 * This aray element has to be a type JSONObject, JSONArray
	 * @param objArray
	 * @return
	 */
	
	public static Object toJSONObject(Object[] objArray)
	{
		JSONArray jsona = new JSONArray();
		for (int i = 0; i < objArray.length; i++) {
			jsona.add(objArray[i]);
		}		
		return jsona;
	}	
	
	
	public static Object toJSONObject(String[] strArray)
	{
		JSONArray jsona = new JSONArray();
		for (int i = 0; i < strArray.length; i++) {
			jsona.add(strArray[i]);
		}		
		return jsona;
	}	
	
	
	public static Object toJSONObject(double[] dblArray)
	{
		JSONArray jsona = new JSONArray();
		for (int i = 0; i < dblArray.length; i++) {
			jsona.add(new Double(dblArray[i]));
		}		
		return jsona;
	}
	
		
	public static Object toJSONObject(float[] dblArray)
	{
		JSONArray jsona = new JSONArray();
		for (int i = 0; i < dblArray.length; i++) {
			jsona.add(new Double(dblArray[i]));
		}		
		return jsona;
	}
	
	
	
	public static Object toJSONObject(long[] longArray)
	{
		JSONArray jsona = new JSONArray();
		for (int i = 0; i < longArray.length; i++) {
			jsona.add(new Long(longArray[i]));
		}		
		return jsona;
	}
	
	
	public static Object toJSONObject(int[] intArray)
	{
		JSONArray jsona = new JSONArray();
		for (int i = 0; i < intArray.length; i++) {
			jsona.add(new Integer(intArray[i]));
		}
		
		return jsona;
	}
	
	
	
	public static Object toJSONObject(short[] shortArray)
	{
		JSONArray jsona = new JSONArray();
		for (int i = 0; i < shortArray.length; i++) {
			jsona.add(new Integer(shortArray[i]));
		}
		
		return jsona;
	}
	
	
	public static Object toJSONObject(String jsonstring)
	{
		Object obj = null;
		try {
			JSONObjectParser parser = new JSONObjectParser(jsonstring);
			obj = parser.parse();
		} catch (Exception e) {
			throw new JSONParsingException("Invalid JSON String", e);
		}
		return obj;
	}
	
	public int rootElementCount()
	{
		if(jsonObject instanceof JSONArray){
			return ((JSONArray)jsonObject).size();
		}else {
			return -1;
		}
	}
	
	
	public Object rootElementByIndex(int index)
	{
		if(jsonObject instanceof JSONArray){
			return ((JSONArray)jsonObject).get(index);
		}else {
			throw new RuntimeException("Scala object can not be accessed by index.");
		}
	}
	
	
	
	
	
	public List<Object> getAll(String hirachyName) {
		JSONQuery[] queries = JSONQueryParse.parseQuery(hirachyName);
		ArrayList<Object> currObjs = new ArrayList<Object>();
		currObjs.add(this.jsonObject);
		ArrayList<Object> matchObjects = new ArrayList<Object>();
		int len = queries.length;		
		for (int i = 0; i < len ; i++) {		
			int cnt = currObjs.size();
			for(int k=0; k<cnt; k++)
			{
				Object currObj = currObjs.get(k);
				switch(queries[i].queryMethod())
				{
				case 0:
					if(currObj instanceof JSONArray){
						continue;
					}					
					JSONObject jobj  = (JSONObject)currObj;
					Object tobj = jobj.get(queries[i].getObjName());
					if(tobj!=null)matchObjects.add(tobj);
					break;
				case 1:
					if(!(currObj instanceof JSONArray)){
						continue;
					}
					JSONArray jaobj  = (JSONArray)currObj;
					matchObjects.add(jaobj.get(queries[i].getIndex()));								
					break;
				case 2:
					if(!(currObj instanceof JSONArray)){
						continue;
					}
					JSONArray jaobj2  = (JSONArray)currObj;
					int ct = jaobj2.size();					
					for(int j=0; j<ct; j++){
						if(jaobj2.get(j) instanceof JSONObject){
							JSONObject tmpObj = (JSONObject)jaobj2.get(j);
							boolean match = true;
							for(JSONQueryNameValuePair nvp:queries[i].getParams()){							
								Object tobj2 = tmpObj.get(nvp.getName());							
								if(tobj2==null || !tobj2.toString().equals(nvp.getValue())){
									match = false;
									break;
								}
							}
							if(!match){
								continue;
							}else{
								matchObjects.add(tmpObj);									
							}
						}
					}					
					break;
				}				
			} //end of k loop (src object count;
			
			if(i<len-1){
				currObjs.clear();
				currObjs.addAll(matchObjects);
				matchObjects.clear();
			}
		}//end of i loop (query steps)
		return matchObjects;
	}
		
	
	public void putAll(String hirachyName, Object value) {
		JSONQuery[] queries = JSONQueryParse.parseQuery(hirachyName);
		ArrayList<Object> currObjs = new ArrayList<Object>();
		currObjs.add(this.jsonObject);
		ArrayList<Object> matchObjects = new ArrayList<Object>();
		int len = queries.length;		
		for (int i = 0; i < len ; i++) {		
			int cnt = currObjs.size();
			for(int k=0; k<cnt; k++)
			{
				Object currObj = currObjs.get(k);
				switch(queries[i].queryMethod())
				{
				case 0:
					if(currObj instanceof JSONArray){
						continue;
					}					
					JSONObject jobj  = (JSONObject)currObj;					
					if(i==len-1){
						jobj.put(queries[i].getObjName(), value);
					}else{
						Object tobj = jobj.get(queries[i].getObjName());
						if(tobj!=null) matchObjects.add(tobj);
					}					
					break;
				case 1:
					if(!(currObj instanceof JSONArray)){
						continue;
					}
					JSONArray jaobj  = (JSONArray)currObj;
					if(i==len-1){
						jaobj.add(value);
					}else{
						matchObjects.add(jaobj.get(queries[i].getIndex()));
					}
					break;
				case 2:
					if(!(currObj instanceof JSONArray)){
						continue;
					}
					JSONArray jaobj2  = (JSONArray)currObj;
					int ct = jaobj2.size();					
					for(int j=0; j<ct; j++){
						if(jaobj2.get(j) instanceof JSONObject){
							JSONObject tmpObj = (JSONObject)jaobj2.get(j);
							boolean match = true;
							for(JSONQueryNameValuePair nvp:queries[i].getParams()){							
								Object tobj2 = tmpObj.get(nvp.getName());							
								if(tobj2==null || !tobj2.toString().equals(nvp.getValue())){
									match = false;
									break;
								}
							}
							if(!match){
								continue;
							}else{
								if(i==len-1){
									throw new RuntimeException("Cann't assign value to search pattern.");
								}else{
									matchObjects.add(tmpObj);	
								}
							}
						}
					}					
					break;
				}				
			} //end of k loop (src object count;
			
			if(i<len-1){
				currObjs.clear();
				currObjs.addAll(matchObjects);
				matchObjects.clear();
			}
		}//end of i loop (query steps)		
	}
	

	public Object get(String hirachyName) {
		JSONQuery[] queries = JSONQueryParse.parseQuery(hirachyName);
		Object currObj = jsonObject;
		int len = queries.length;
		for (int i = 0; i < len ; i++) {
			switch(queries[i].queryMethod())
			{
			case 0:
				if(currObj instanceof JSONArray){
					System.out.println(currObj.toString());
					throw new RuntimeException("JSON Array can not be queried by name at step " + i);
				}
				
				JSONObject jobj  = (JSONObject)currObj;
				if(i==len-1){
					Object tobj = jobj.get(queries[i].getObjName());					
					return (tobj==null)?null:tobj;
				}else{					
					currObj = jobj.get(queries[i].getObjName());
					if(currObj==null){
						throw new RuntimeException("Invalid path:" + hirachyName);
					}
				}
				break;
			case 1:
				if(!(currObj instanceof JSONArray)){
					throw new RuntimeException("Only JSON Array can  be queried by index at step " + i);
				}
				JSONArray jaobj  = (JSONArray)currObj;
				if(i==len-1){					
					return jaobj.get(queries[i].getIndex());					
				}else{					
					currObj = jaobj.get(queries[i].getIndex());		
				}			
				break;
			case 2:
				if(!(currObj instanceof JSONArray)){
					throw new RuntimeException("Only JSON Array can  be queried by name and value pairs at step " + i);
				}
				JSONArray jaobj2  = (JSONArray)currObj;
				int ct = jaobj2.size();
				boolean found = false;
				for(int j=0; j<ct; j++){
					if(jaobj2.get(j) instanceof JSONObject){
						JSONObject tmpObj = (JSONObject)jaobj2.get(j);
						boolean match = true;
						for(JSONQueryNameValuePair nvp:queries[i].getParams()){							
							Object tobj = tmpObj.get(nvp.getName());							
							if(tobj==null || !tobj.toString().equals(nvp.getValue())){
								match = false;
								break;
							}
						}
						if(!match){
							continue;
						}else{
							currObj = tmpObj;
							found = true;
							break;
						}
					}
				}
				if(!found){
					throw new RuntimeException("No match record found at query at step " + i);
				}else{
					if( i==len-1){
						return currObj;
					}
				}
				break;
			}
		}
		return null;
	}


	public void put(String hirachyName, Object obj) {
		JSONQuery[] queries = JSONQueryParse.parseQuery(hirachyName);	
		//for (int i = 0; i < queries.length; i++) {
		//	System.out.println(queries[i].queryMethod() + ":" + i);
		//}
		Object currObj = jsonObject;
		int len = queries.length;
		for (int i = 0; i < len ; i++) {
			switch(queries[i].queryMethod())
			{
			case 0:
				if(currObj instanceof JSONArray){
					System.out.println(currObj.toString());
					throw new RuntimeException("JSON Array can not be queried by name at step " + i);
				}
				JSONObject jobj  = (JSONObject)currObj;
				if(i==len-1){
					jobj.put(queries[i].getObjName(), obj);
				}else{
					currObj = jobj.get(queries[i].getObjName());
					if(currObj==null){
						throw new RuntimeException("Invalid path:" + hirachyName);
					}
				}
				break;
			case 1:
				if(!(currObj instanceof JSONArray)){
					throw new RuntimeException("Only JSON Array can  be queried by index at step " + i);
				}
				JSONArray jaobj  = (JSONArray)currObj;
				if(i==len-1){					
					jaobj.remove(queries[i].getIndex());
					jaobj.add(queries[i].getIndex(), obj);
				}else{					
					currObj = jaobj.get(queries[i].getIndex());		
				}
				break;
			case 2:
				if(!(currObj instanceof JSONArray)){
					throw new RuntimeException("Only JSON Array can  be queried by name and value pairs at step " + i);
				}
				JSONArray jaobj2  = (JSONArray)currObj;
				int ct = jaobj2.size();
				boolean found = false;
				for(int j=0; j<ct; j++){
					if(jaobj2.get(j) instanceof JSONObject){
						JSONObject tmpObj = (JSONObject)jaobj2.get(j);
						boolean match = true;
						for(JSONQueryNameValuePair nvp:queries[i].getParams()){							
							Object tobj = tmpObj.get(nvp.getName());
							//System.out.println("VALUE:[" + tobj.toString() + "] EXPECTED [" + nvp.getValue() + "]");
							if(tobj==null || !tobj.toString().equals(nvp.getValue())){
								match = false;
								break;
							}
						}
						if(!match){
							continue;
						}else{
							currObj = tmpObj;
							found = true;
							break;
						}
					}
					
				}
				if(!found){
					throw new RuntimeException("No match record found at query step " + i);
				}				
				break;
			}
		}
		
	}
	
	
	
	public void insert(String hirachyName, Object obj) {
		JSONQuery[] queries = JSONQueryParse.parseQuery(hirachyName);	
		Object currObj = jsonObject;
		if(queries.length==0 && currObj instanceof JSONArray){
			JSONArray jarray = (JSONArray)currObj;
			if(obj instanceof JSONArray){
				JSONArray newMembers = (JSONArray)obj;
				jarray.addAll(newMembers);
			}else{
				jarray.add(obj);
			}
			return;
		}		
		int len = queries.length;
		for (int i = 0; i < len ; i++) {
			switch(queries[i].queryMethod())
			{
			case 0:
				if(currObj instanceof JSONArray){					
					throw new RuntimeException("JSON Array Object can only support query by index at step" + i);
				}
				if(i==len-1){
					JSONObject jobj =  (JSONObject) currObj;
					currObj = jobj.get(queries[i].getObjName());
					if(currObj instanceof JSONArray){
						JSONArray jarray1 = (JSONArray)currObj;
						if(obj instanceof JSONArray){
							JSONArray newMembers = (JSONArray)obj;
							int idx = queries[i].getIndex();
							if(idx == -1){
								jarray1.addAll(newMembers);
							}else{
								jarray1.addAll(queries[i].getIndex(), newMembers);
							}
						}else{
							jarray1.add(obj);
						}
					}else{
						throw new RuntimeException("Can only insert into Array. at step" + i);
					}
				}else{
					JSONObject jobj =  (JSONObject) currObj;
					currObj = jobj.get(queries[i].getObjName());
					if(currObj==null){
						throw new RuntimeException("Invalid path:" + hirachyName);
					}
				}
				break;
			case 1:
				if(!(currObj instanceof JSONArray)){
					throw new RuntimeException("Only JSON Array can  be queried by index at step " + i);
				}
				JSONArray jaobj  = (JSONArray)currObj;
				if(i==len-1){
					if(obj instanceof JSONArray){
						JSONArray newMembers = (JSONArray)obj;
						if(queries[i].getIndex()==-1){
							jaobj.addAll(newMembers);
						}else{
							jaobj.addAll(queries[i].getIndex(), newMembers);
						}
					}else{
						jaobj.add(obj);
					}
					return;
				}else{					
					currObj = jaobj.get(queries[i].getIndex());		
				}
				break;
			case 2:
				if(!(currObj instanceof JSONArray)){
					throw new RuntimeException("Only JSON Array can  be queried by name and value pairs at step " + i);
				}
				JSONArray jaobj2  = (JSONArray)currObj;
				int ct = jaobj2.size();
				boolean found = false;
				for(int j=0; j<ct; j++){
					if(jaobj2.get(j) instanceof JSONObject){
						JSONObject tmpObj = (JSONObject)jaobj2.get(j);
						boolean match = true;
						for(JSONQueryNameValuePair nvp:queries[i].getParams()){							
							Object tobj = tmpObj.get(nvp.getName());
							//System.out.println("VALUE:[" + tobj.toString() + "] EXPECTED [" + nvp.getValue() + "]");
							if(tobj==null || !tobj.toString().equals(nvp.getValue())){
								match = false;
								break;
							}
						}
						if(!match){
							continue;
						}else{
							currObj = tmpObj;
							found = true;
							break;
						}
					}
				}
				if(!found){
					throw new RuntimeException("No match record found at query step " + i);
				}else if (currObj instanceof JSONArray && i==len-1){
					JSONArray jarray1 = (JSONArray)currObj;
					if(obj instanceof JSONArray){
						JSONArray newMembers = (JSONArray)obj;
						if(queries[i].getIndex()==-1){
							jarray1.addAll(newMembers);
						}else{
							jarray1.addAll(queries[i].getIndex(), newMembers);
						}
					}else{
						jarray1.add(obj);
					}
					return;
				}
				break;
			}
		}
		
	}
	

	public static class JSONQueryNameValuePair {
		String name;
		String value;

		public JSONQueryNameValuePair() {
		}

		public JSONQueryNameValuePair(String name, String value) {
			this.value = value;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class JSONQuery {
		List<JSONQueryNameValuePair> params = new ArrayList<JSONEditor.JSONQueryNameValuePair>();
		String objName;
		int index = -1;

		public List<JSONQueryNameValuePair> getParams() {
			return params;
		}

		
		public void addParam(JSONQueryNameValuePair param) {
			this.params.add(param);
		}
		public void addParam(String name, String value) {
			this.params.add(new JSONQueryNameValuePair(name, value));
		}
				
		public void setParams(List<JSONQueryNameValuePair> params) {
			this.params = params;
		}

		public String getObjName() {
			return objName;
		}

		public void setObjName(String objName) {
			this.objName = objName;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
		
		public int queryMethod()
		{
			if(objName!=null){
				return 0;
			}else if (index!=-1){
				return 1;
			}else{
				return 2;
			}
		}

	}

	public static class JSONQueryParse {

		final static char STEP_SEPARATOR = '/';
		final static char QUOTE = '"';
		final static char APOS = '\'';
		final static char LBRKT = '[';
		final static char RBRKT = ']';
		final static char EQ = '=';
		final static char APM = '&';
		
		public static JSONQuery[] parseQuery(String query) {

			int len = query.length();
			StringBuilder buf  = new StringBuilder();
			ArrayList<JSONQuery> queries = new ArrayList<JSONEditor.JSONQuery>();
			JSONQuery jquery = new JSONQuery();
			boolean nvpStart = false;
			String attrName = null;
			String attrValue = null;
			for (int i = 0; i < len; i++) {
				char c = query.charAt(i);
				switch(c)
				{
				case STEP_SEPARATOR:
					if(i>0){
						String name = buf.toString().trim();
						if(name.length()>0){
							jquery.setObjName(name);
							//System.out.println("[/]element name:" + name);
							queries.add(jquery);
						}						
						buf.setLength(0);						
						jquery = new JSONQuery();
					}
					break;
				case QUOTE:
					for(i=i+1; i<len; i++){
						char c2 = query.charAt(i);
						if(c2==QUOTE){
							break;
						}else{
							buf.append(c2);
						}
					}					
					break;
				case APOS:
					for(i=i+1; i<len; i++){
						char c2 = query.charAt(i);
						if(c2==APOS){
							break;
						}else{
							buf.append(c2);
						}
					}					
					break;
				case APM:
					if(i<len-1 && query.charAt(i+1)==APM){
						i++;
						String attvalue = buf.toString().trim();
						if ( attvalue.length()>0){
							attrValue = attvalue;
							//System.out.println("ATTR Value:" + attrValue);
							if(nvpStart){
								jquery.addParam(attrName, attrValue);
							}
						}
						buf.setLength(0);	
					}else{
						buf.append(c);
					}
					break;	
					
				case LBRKT:
					String name = buf.toString().trim();					
					if(name.length()>0){						
						jquery.setObjName(name);
						//System.out.println("']' Element name:" + name);
					}					
					buf.setLength(0);
					nvpStart = true;
					break;	
				case RBRKT:
					String attvalue = buf.toString().trim();
					if ( attvalue.length()>0){
						attrValue = attvalue;
						//System.out.println("ATTR attrName:" + attrName);
						if(nvpStart){
							if(attrName!=null && attrName.trim().length()>0){
								jquery.addParam(attrName, attrValue);
								attrName = null;
								attrValue = null;
							}else{
								try{
									int idx = Integer.valueOf(attrValue).intValue();
									jquery.setIndex(idx);
								}catch(NumberFormatException nfx){
									throw new RuntimeException("Invalid JSON query, index needs valid integer number.");
								}
							}
						}
					}					
					buf.setLength(0);
					queries.add(jquery);					
					jquery = null;
					nvpStart = false;
					break;		
				case EQ:					
					String attname = buf.toString().trim();					
					if(attname.length()>0){						
						attrName = attname;
						//System.out.println("ATTR Name:" + attname);
					}					
					buf.setLength(0);		
					break;
				default:
					buf.append(c);
					break;
				}				
			}
			String name = buf.toString().trim();
			if(jquery!=null && name.length()>0){
				jquery.setObjName(name);				
			}
			if(jquery!=null 
					&& (jquery.getObjName()!=null || jquery.getIndex()!=-1)){
				queries.add(jquery);
			}
			//System.out.println("--------------------------");
			//for(JSONQuery q : queries){
			//	System.out.println(q.getObjName());
			//}
			
			JSONQuery[] qs = new JSONQuery[queries.size()];
			queries.toArray(qs);
			return qs;

		}

		
	}

	public String getJSONAsString() {		
		return jsonObject.toString();
	}

	public JSONEditor() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String jsonTest1 = "[{\"IPADDR\":\"172.18.74.155\",\"MEMVALUE\":\"106428\",\"CPUVALUE\":\"99.75\",\"PROFILETIME\":\"2013-04-11:11.17\"},{\"IPADDR\":\"172.18.74.160\",\"MEMVALUE\":\"2257897\",\"CPUVALUE\":\"55.3765\",\"PROFILETIME\":\"2013-04-10:21.02\"},{\"IPADDR\":\"172.18.85.50\",\"MEMVALUE\":\"62726688\",\"CPUVALUE\":\"99.5\",\"PROFILETIME\":\"2013-04-11:11.17\"},{\"IPADDR\":\"172.18.74.159\",\"MEMVALUE\":\"1057581\",\"CPUVALUE\":\"25.3765\",\"PROFILETIME\":\"2013-04-10:21.02\"},{\"IPADDR\":\"172.18.74.26\",\"MEMVALUE\":\"1421596\",\"CPUVALUE\":\"100\",\"PROFILETIME\":\"2013-04-11:11.17\"},{\"IPADDR\":\"172.18.74.158\",\"MEMVALUE\":\"657581\",\"CPUVALUE\":\"45.3765\",\"PROFILETIME\":\"2013-04-10:21.02\"},{\"IPADDR\":\"172.18.74.156\",\"MEMVALUE\":\"455553\",\"CPUVALUE\":\"99.88\",\"PROFILETIME\":\"2013-04-10:21.02\"},{\"IPADDR\":\"172.18.74.157\",\"MEMVALUE\":\"956569\",\"CPUVALUE\":\"95.3765\",\"PROFILETIME\":\"2013-04-10:21.02\"}]";
		String jsonTest4 = "[{\"IPADDR\":\"111.18.74.155\",\"MEMVALUE\":\"106428\",\"CPUVALUE\":\"99.75\",\"PROFILETIME\":\"2013-04-11:11.17\"},{\"IPADDR\":\"123.18.74.160\",\"MEMVALUE\":\"2257897\",\"CPUVALUE\":\"55.3765\",\"PROFILETIME\":\"2013-04-10:21.02\"}]";
		String jsonTest5 = "[{\"IPADDR\":\"000.18.74.155\",\"MEMVALUE\":\"106428\",\"CPUVALUE\":\"99.75\",\"PROFILETIME\":\"2013-04-11:11.17\"},{\"IPADDR\":\"333.18.74.160\",\"MEMVALUE\":\"2257897\",\"CPUVALUE\":\"55.3765\",\"PROFILETIME\":\"2013-04-10:21.02\"}]";
		
		String jsonTest2 = "[{\"city\":\"Flushing\"},{\"city\":\"Jericho\"}]";
		String jsonTest3 = "[{\"city\":\"New York\"},{\"city\":\"Paris\"}]";
		
		//System.out.println(jsonTest);		
		JSONEditor editor = new JSONEditor(jsonTest1);
		;
		editor.put("/[IPADDR=172.18.74.155]/address", JSONEditor.toJSONObject(jsonTest2));
		System.out.println(editor.getJSONAsString());
		editor.insert("/[IPADDR=172.18.74.155]/address[0]", JSONEditor.toJSONObject(jsonTest3));
		//editor.put("/[IPADDR=172.18.74.155]/address", jsonTest2);
		//editor.put("/[IPADDR=172.18.74.160]/ROWSIZE", "0");	
		//System.out.println(editor.get("/[IPADDR=172.18.74.155]"));
		System.out.println(editor.getJSONAsString());
		editor.insert("/", JSONEditor.toJSONObject(jsonTest4));
		
		
		//editor.put("/[IPADDR=172.18.74.155]/address", jsonTest2);
		//editor.put("/[IPADDR=172.18.74.160]/ROWSIZE", "0");	
		//System.out.println(editor.get("/[IPADDR=172.18.74.155]"));
		System.out.println(editor.getJSONAsString());		
		editor.insert("/[0]", JSONEditor.toJSONObject(jsonTest5));
		
		
		//editor.put("/[IPADDR=172.18.74.155]/address", jsonTest2);
		//editor.put("/[IPADDR=172.18.74.160]/ROWSIZE", "0");	
		//System.out.println(editor.get("/[IPADDR=172.18.74.155]"));
		System.out.println(editor.getJSONAsString());
		
	}

}
