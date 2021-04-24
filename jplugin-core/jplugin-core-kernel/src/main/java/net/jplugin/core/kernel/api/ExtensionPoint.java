package net.jplugin.core.kernel.api;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jplugin.common.kits.StringKit;

/**
 *
 * @author: LiuHang
 * @version 创建时间：2015-2-22 上午11:43:28
 **/

public class ExtensionPoint {
	/**
	 * <PRE>
	 * 扩展点类型，分为NAMED,LIST,UNIQUE
	 * NAMED: 每一个扩展都有一个唯一的名字，保存在extensionMap里面。
	 * LIST:扩展没有名字，或者可以重名，保存在extensionObjects里面。
	 * UNIQUE:最多只能有一个扩展,保存在extensionObjects里面。
	 *</PRE>
	 */
	public enum Type {NAMED,LIST,UNIQUE}
	
	String name;
	Class<?> extensionClass;
	Type type;
	List<Extension> extensions;
	Object[] extensionObjects;
	Map<String,Object> extensionMap;
	
	
	/**
	 * 创建一个扩展点, 扩展为多个不限定名称的实例
	 * @param aName
	 * @param clazz
	 * @return
	 */
	public static ExtensionPoint createList(String aName,Class<?> clazz){
		return new ExtensionPoint(aName,clazz,Type.LIST);
	}
	/**
	 * 创建一个扩展点, 扩展为唯一名称的多个实例
	 * @param aName
	 * @param clazz
	 * @return
	 */
	public static ExtensionPoint createNamed(String aName,Class<?> clazz){
		return new ExtensionPoint(aName,clazz,Type.NAMED);
	}
	/**
	 * 创建一个单例扩展点，只能有一个扩展实例，如果注册了多个，系统自动检查
	 * @param aName
	 * @param clazz
	 * @return
	 */
	public static ExtensionPoint createUnique(String aName,Class<?> clazz){
		return new ExtensionPoint(aName,clazz,Type.UNIQUE);
	}
	
	
	@Deprecated
	public static ExtensionPoint create(String aName,Class<?> clazz){
		return createList(aName,clazz);
	}
	
	/**
	 * 创建一个扩展点，nameUnique表示是否需要每一个扩展都有一个唯一的名字(name)。
	 * @param aName
	 * @param clazz
	 * @param nameUnique
	 * @return
	 */
	@Deprecated
	public static ExtensionPoint create(String aName,Class<?> clazz,boolean nameUnique){
		if (nameUnique) {
			return createNamed(aName,clazz);
		}else {
			return createList(aName,clazz);
		}
	}
	
	private ExtensionPoint(String aName,Class<?> clazz,Type tp){
		this.name = aName;
		this.extensionClass = clazz;
		this.extensions = new ArrayList<Extension>();
		this.type = tp;
	}
	
	
	/**
	 * 定义阶段
	 * @return
	 */
	public String getName(){
		return name;
	}
	/**
	 * 定义阶段
	 * @return
	 */
	public Class<?> getExtensionClass(){
		return extensionClass;
	}
	
	/**
	 * 定义阶段
	 * @return
	 */
	public boolean extensionNameReqiredAndUnique(){
		return this.type==Type.NAMED;
	}

	/**
	 * 运行阶段
	 * @return
	 */
	public List<Extension> getExtensions(){
		return this.extensions;
	}
	
	/**
	 * @param e
	 */
	public void addExtension(Extension e) {
		this.extensions.add(e);
	}
	
//	void findExtensionObjectByName(String nm){
//		Object find = null;
//		for (Extension e:this.extensions){
//			if (nm.equals(e.getName())){
//				if (find == null){
//					find = e.getObject();
//				}else{
//					throw new RuntimeException("find duplicate object with name:"+nm);
//				}
//			}
//		}
//	}
	
	/**
	 * 获取单个Extension，类型必须是 SINGLETON
	 * @param <T>
	 * @param t
	 * @return
	 */
	public <T> T getExtension(Class<T> t) {
		if (! (this.type==Type.UNIQUE)){
			throw new RuntimeException("can't call getExtension when type is not SINGLETON");
		}
		
		if (this.extensionObjects==null){
			//初始化一下
			getExtensionObjects(t);
		}
		
		if (this.extensionObjects==null || this.extensionObjects.length==0) {
			return null;
		}else {
			return (T) this.extensionObjects[0];
		}
	}
	
	/**
	 * 运行阶段
	 * @param <T>
	 * @param t
	 * @return
	 */
	public <T> T[] getExtensionObjects(Class <T> t){
		if (this.extensionObjects==null){
			synchronized (this) {
				if (this.extensionObjects==null){
					this.extensionObjects = (Object[]) Array.newInstance(extensionClass, this.extensions.size());
					for (int i=0;i<this.extensionObjects.length;i++){
						this.extensionObjects[i] = this.extensions.get(i).getObject();
					}		
				}
			}
		}
		return (T[]) this.extensionObjects;
	}
	
	public Map<String,Object> getExtensionMap(){
		if (! (this.type==Type.NAMED)){
			throw new RuntimeException("can't call getExtensionMap when extensionNameReqiredAndUnique is false");
		}
		
		if (this.extensionMap==null){
			synchronized (this) {
				if (this.extensionMap==null){
					this.extensionMap = new HashMap<String, Object>();
					
					for (Extension e:this.extensions){
						this.extensionMap.put(e.getName(), e.getObject());
					}	
				}
			}
		}
		return this.extensionMap;
	}

	/**
	 * @param name2
	 * @return
	 */
	boolean validToAddExtensionByName(String nm) {
		if (this.type==Type.NAMED){
			if (StringKit.isNull(nm)){
				return false;
			}
			
			for (Extension e:this.extensions){
				if (nm.equals(e.getName())){
					return false;
				}
			}
			return true;
		}else if (this.type == Type.UNIQUE) {
			return this.extensions.size()==0;
		}else
			return true;
		
	}

	
}
