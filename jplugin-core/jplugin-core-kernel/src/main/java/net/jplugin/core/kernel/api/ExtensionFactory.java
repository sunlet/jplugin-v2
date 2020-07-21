package net.jplugin.core.kernel.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.jplugin.common.kits.StringKit;

public class ExtensionFactory {
	static HashMap<String,Object> initiateMap=new HashMap<String, Object>();
	static HashMap<Object,Object> resetingMap=new HashMap<Object, Object>();

	
	public static <T> T get(String id,Class<T> t) {
		return (T) get(id);
	}
	
	public static Object get(String id) {
		Object o = find(id);
		if (o!=null) return o;
		else throw new RuntimeException("Extension not found by id:"+id);
	}
	public static Object find(String id) {
		if (PluginEnvirement.INSTANCE.getStateLevel()<PluginEnvirement.STAT_LEVEL_INITING)
			throw new RuntimeException("Can't call when state is before STAT_LEVEL_INITING");
		
		Object val = initiateMap.get(id);
		if (val==null) {
			return null;
		}
		Object resetVal = resetingMap.get(val);
		
		//优先返回resetVal,空时才返回 val
		if (resetVal!=null) {
			return resetVal;
		}else {
			return val;
		}
	}
	
	/**
	 * id一定是已经存在的，并且只能在disabledModify=true情况下
	 * @param id
	 * @param value
	 */
	public static void resetValue(Object key,Object value) {
		if (PluginEnvirement.INSTANCE.getStateLevel()>=PluginEnvirement.STAT_LEVEL_INITING)
			throw new RuntimeException("Can't be called after Modify STAT_LEVEL_INITING!");

		
		//找不到对应的value，则不会调用
		if (initiateMap.containsValue(key)) {
			resetingMap.put(key, value);
		}
	}
	


	/**
	 * 在load以后被调用，这时所有的ExtensionObject都创建好了。
	 * ExtensionObject必须是new出来不重复对象才可以，所以：如果是extensionObject是Class或者String，则不能有ID！
	 */
	static void initFromPluginList() { 
		if (PluginEnvirement.INSTANCE.getStateLevel()>=PluginEnvirement.STAT_LEVEL_INITING)
			throw new RuntimeException("Can't be called after Modify STAT_LEVEL_INITING!");
		
		Set<String> dupCheckSet = new HashSet<String>();
		
		List<AbstractPlugin> list = PluginEnvirement.getInstance().getPluginRegistry().getPluginList();
		for (AbstractPlugin plugin:list) {
			List<Extension> exts = plugin.getExtensions();
			for (Extension ext:exts) {
				Object extObject = ext.getObject();
				if (extObject instanceof Class || extObject instanceof String) {
					if (StringKit.isNotNull(ext.getId())) {
						throw new RuntimeException("Extension with type Class or String can't have id: Plugin:"+plugin.getName()+" RefExtensionPoint:"+ext.getExtensionPointName());
					}
				}else {
					String extid = ext.getId();
					if (StringKit.isNotNull(extid)) {
						if (dupCheckSet.contains(extid)){
							throw new RuntimeException("Extension ID is duplicated. Plugin:"+plugin.getName()+" RefExtensionPoint:"+ext.getExtensionPointName()+" id="+extid);
						}else {
							dupCheckSet.add(extid);
						}
						initiateMap.put(extid, ext.getObject());
					}
				}
			}
		}
	}
	
	
	//以下为 extension id相关的维护方法
	
	static Extension lastAddedExtension;
	static void setLastExtension(Extension e) {
		lastAddedExtension = e;
	}
	public static void setLastId(String id) {
		//设置上一次调用addExtension的extension的ID
		if (lastAddedExtension!=null) {
			lastAddedExtension.setId(id);
		}else {
			throw new RuntimeException("Last extension is null.");
		}
	}
	public static String getLastId() {
		if (lastAddedExtension!=null) {
			return lastAddedExtension.getId();
		}else {
			throw new RuntimeException("Last extension is null.");
		}
	}
}
