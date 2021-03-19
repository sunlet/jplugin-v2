package net.jplugin.core.kernel.api;

import java.util.Map;

/**
 * 本类帮助获取一个扩展点下的扩展形成的数组或者Map.
 * 注意：如果想获取到Map结构的扩展，增加的扩展点的时候 nameUnique 必须传true，注册扩展的时候也必须指定一个本扩展点内唯一的名字。系统会自动校验这些逻辑。
 * @author LiuHang
 */

public class ExtensionPointHelper {

	/**
	 * 获取一个扩展点pointName下的所有扩展形成的Map, key为扩展点名字,value为扩展的对象.
	 * @param pointName
	 * @return
	 */
	public static Map<String, Object> getExtensionMap(String pointName) {
		return PluginEnvirement.getInstance().getExtensionMap(pointName);
	}
	/**
	 * 获取一个扩展点pointName下的所有扩展形成的Map, key为扩展点名字,value为扩展的对象. 并进行泛型类型转化。
	 * @param <T>
	 * @param pointName
	 * @param t 
	 * @return
	 */
	public static <T> Map<String,T> getExtensionMap(String pointName,Class<T> t){
		return PluginEnvirement.getInstance().getExtensionMap(pointName,t);
	}
	/**
	 * 获取一个扩展点pointName下的所有扩展形成的数组.
	 * @param pointName
	 * @return
	 */
	public static Object[] getExtensionObjects(String pointName) {
		return PluginEnvirement.getInstance().getExtensionObjects(pointName);
	}
	
	/**
	 * 获取一个扩展点pointName下的所有扩展形成的数组.并进行泛型类型转化。
	 * @param <T>
	 * @param pointName
	 * @param t
	 * @return
	 */
	public static <T> T[] getExtensionObjects(String pointName, Class<T> t) {
		return PluginEnvirement.getInstance().getExtensionObjects(pointName,t);
	}
	
	/**
	 * 获取单个Extension,扩展点的类型需要是Singleton类型
	 * @param <T>
	 * @param pointName
	 * @param t
	 * @return
	 */
	public static <T> T getExtension(String pointName,Class<T> t) {
		return PluginEnvirement.getInstance().getExtension(pointName,t);
	}
}
