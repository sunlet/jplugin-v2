package net.jplugin.core.kernel.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import net.jplugin.common.kits.AssertKit;
import net.jplugin.common.kits.ReflactKit;
import net.jplugin.common.kits.StringKit;
import net.jplugin.core.kernel.impl.PropertyUtil;

/**
 *
 * @author: LiuHang
 * @version 创建时间：2015-2-22 上午11:43:22
 **/

public class Extension {
	public static IPropertyFilter propertyFilter=null;
	String refExtensionPoint;
	String name;
	Class clazz;
	Vector<Property> propertyList=new Vector<Property>(1);
	
	Object extensionObject;
	
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("refPoint:"+refExtensionPoint+" clazz:"+clazz.getName()+" name:"+name);
		sb.append(" property:[");
		for (int i=0;i<propertyList.size();i++) {
			sb.append(propertyList.get(i).key+"-"+propertyList.get(i).value);
			sb.append("  ");
		}
		sb.append("]");
		return sb.toString();
	}
	/**
	 * 实现规则，对象Equal时，必须有相同的hashCode.
	 * 这里 name为null也不影响结果
	 */
	@Override
	public int hashCode() {
		return (refExtensionPoint+clazz.getName()+name).hashCode();
	}
	

	/**
	 * 重复 Extension的标准：重复的refExtensionPoint、clazz、name、propertyList
	 */
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Extension))
			return false;
		else {
			Extension e = (Extension) obj;
			return  
					clazz.equals(e.getClazz())
					&&
					(refExtensionPoint.equals(e.refExtensionPoint))
					&&
					 StringKit.eqOrNull(name, e.name)
					&&
					 checkPropertyDup(propertyList,e.propertyList)
					&& 
					 StringKit.eqOrNull(this.id,e.id);
		}
	}
		

	private boolean checkPropertyDup(Vector<Property> p1, Vector<Property> p2) {
		//長度不同
		if (p1.size()!=p2.size()) 
			return false;
		
		//長度相同，對每一個屬性看能否找到
		for (Property item:p1) {
			
			boolean found=false;
			for (Property o:p2) {
				if (StringKit.eqOrNull(item.key,o.key) && StringKit.eqOrNull(item.value, o.value)) {
					found = true;
					break;
				}
			}
			//如果上面的循環執行完畢，仍然沒有找到
			if (!found)
				return false;
		}
		//相同
		return true;
	}

	public String getExtensionPointName(){
		return this.refExtensionPoint;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Class getClazz(){
		return this.clazz;
	}
	
	public List<Property> getProperties(){
		return this.propertyList;
	}
	
	public Object getObject(){
		return extensionObject;
	}
	
	public static class Property{
		String key;
		String value;
		public String getKey() {
			return key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String v){
			value = v;
		}
		
	}
	
	public synchronized void load() throws Exception{
		if (propertyFilter!=null){
			filterProperty(this.propertyList);
		}
		if (this.extensionObject == null){
			if (clazz.equals(String.class)){
				//字符串类型采用特殊加载方式
				if (this.propertyList.size()!=1){
					throw new RuntimeException("String type extension must has one property with the val");
				}
				this.extensionObject = this.propertyList.get(0).getValue();
			}else{
				this.extensionObject = clazz.newInstance();
				//处理extension工厂机制
				this.extensionObject = resolveFactory(this.extensionObject);
				
				PluginEnvirement.getInstance().resolveRefAnnotation(this.extensionObject);
	
				//带属性的加载方式
				if (this.propertyList.size()>0){
					setProperty(this.extensionObject,this.propertyList);
				}
			}
		}
	}
	
	private Object resolveFactory(Object o) {
		if (o instanceof IExtensionFactory) {
			return ((IExtensionFactory)o).create();
		}else {
			return o;
		}
	}
	
	private void filterProperty(Vector<Property> list) {
		for (Property p:list){
			p.setValue(propertyFilter.filte(p.getValue()));
		}
	}

	/**
	 * @param extensionObject2
	 * @param propertyList2
	 */
	private static void setProperty(Object o,
			Vector<Property> p) {
		//看能否找到method
		Method method = null;
		try {
			method = o.getClass().getMethod("setExtensionProperty", new Class[]{java.util.List.class});
		} catch (Exception e){
		}
		
		if (method != null){
			ReflactKit.invoke(o,"setExtensionProperty",new Object[]{p});
		}else{
			PropertyUtil.setProperties(o,p);
		}
	}

	public static Extension createStringExtension(String aPointName,String value){
		return create(aPointName,"",String.class,new String[][]{{"StringValue",value}});
	}
	
	public static Extension create(String aPointName,Class cls){
		return create(aPointName,"",cls);
	}
	
	public static Extension create(String aPointName,String aName,Class cls){
		return create(aPointName,aName,cls,null);
	}

	public static Extension create(String aPointName,Class cls,String[][] property){
		return create(aPointName,"",cls,property);
	}
	public static Extension create(String aPointName,String aName,Class cls,String[][] property){
		Extension ext = new Extension();
		ext.name = aName;
		ext.clazz = cls;
		ext.refExtensionPoint = aPointName;
		if (property!=null){
			for (int i=0;i<property.length;i++){
				Property p = new Property();
				p.key = property[i][0];
				p.value = property[i][1];
				ext.propertyList.add(p);
			}
		}
		return ext;
	}
	
	/**
	 * Extension Id相关的方法和属性
	 */
	private  String id;
	
	public String getId() {
		return id;
	}
	
	/**
	 * 不提供public方法，请调用ExtensionFactory的方法
	 * @param o
	 */
	void setId(String o) {
		this.id = o;
	}
	

	
	public static void main(String[] args) {
		Extension e1 = Extension.create("a", Extension.class,new String[][] {{"a1","b1"},{"a2","v2"}});
		Extension e2 = Extension.create("a", Extension.class,new String[][] {{"a1","b1"},{"a2","v2"}});
		AssertKit.assertTrue(e1.equals(e2));
		
		e1 = Extension.create("a", Extension.class,new String[][] {{"a1","b1"},{"a2","v2"}});
		e2 = Extension.create("a", Extension.class,new String[][] {{"a1","b1"},{"a2","v3"}});
		AssertKit.assertFalse(e1.equals(e2));
		
		e1 = Extension.create("a", null,Extension.class,new String[][] {{"a1","b1"},{"a2","v2"}});
		e2 = Extension.create("a", null,Extension.class,new String[][] {{"a1","b1"},{"a2","v2"}});
		AssertKit.assertTrue(e1.equals(e2));
		
		e1 = Extension.create("a", null,Extension.class,new String[][] {{"a1","b1"},{"a2","v2"}});
		e2 = Extension.create("a", "b",Extension.class,new String[][] {{"a1","b1"},{"a2","v2"}});
		AssertKit.assertFalse(e1.equals(e2));
		
		e1 = Extension.create("a", Extension.class);
		e2 = Extension.create("a", Extension.class);
		AssertKit.assertTrue(e1.equals(e2));
		
		e1 = Extension.create("a",Extension.class);
		e2 = Extension.create("a", Extension.class);
		AssertKit.assertTrue(e1.equals(e2));
		
	}
}
