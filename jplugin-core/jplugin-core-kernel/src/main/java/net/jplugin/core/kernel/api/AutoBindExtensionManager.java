package net.jplugin.core.kernel.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoBindExtensionManager {
	public static AutoBindExtensionManager INSTANCE = new AutoBindExtensionManager();
	
	private List<IBindExtensionHandler> handlers = new ArrayList<IBindExtensionHandler>();
	private Map<Class,IBindAnnotationTransformer> transformers = new HashMap<Class, IBindAnnotationTransformer>();
	
	private AutoBindExtensionManager(){
		handlers.add(new Handler4Transformers());
	}
	
	/**
	 * 推荐使用addBindAnnotationTransformer,应该可以更简单解决问题
	 * @param h
	 */
	@Deprecated
	public void addBindExtensionHandler(IBindExtensionHandler h){
		this.handlers.add(h);
	}
	
	public List<IBindExtensionHandler> getHandlers() {
		return handlers;
	}
	
	public void addBindExtensionTransformer(Class annoClass,IBindAnnotationTransformer bat) {
		transformers.put(annoClass,bat);
	}
	
	class Handler4Transformers implements IBindExtensionHandler {
		@Override
		public void handle(AbstractPlugin p) {
			if (transformers.isEmpty()) 
				return;
			
			List<Extension> extensionList = new ArrayList();
			p.filterContainedClassesByChecker(p.getClass().getPackage().getName(),(c)->{
				//假定大部分都没Annotation,所以先获取所有的anno，这样性能更好
				Annotation[] annos = c.getAnnotations();
				if (annos==null || annos.length==0) {
					//do nothing
				}else {
					for (Annotation a:annos) {
						extensionList.clear();
						IBindAnnotationTransformer trans = transformers.get(a.getClass());
						if (trans!=null) {
							trans.transform(c, a, extensionList);
							for (Extension ext:extensionList) {
								addExtensionAndLog(p,ext,c,a);
							}
						}
					}
				}
				//已经发挥效果，直接返回false
				return false;
			});
		}

		private void addExtensionAndLog(AbstractPlugin p, Extension ext, Class c, Annotation a){
			p.addExtension(ext);
			StringBuffer sb = new StringBuffer("$$$ Auto add extension for ");
			sb.append(a.getClass().getSimpleName());
			sb.append(" class=").append(c.getClass().getSimpleName());
			Field[] fields = a.getClass().getDeclaredFields();
			for (Field f:fields) {
				String name = f.getName();
				Object v;
				try {
					v = f.get(a);
					sb.append(" "+name+"=");
					sb.append(v.toString());
				} catch (IllegalArgumentException e) {
					throw new RuntimeException("Error when transorm anno."+sb.toString(),e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Error when transorm anno."+sb.toString(),e);
				}
				
			}
			PluginEnvirement.getInstance().getStartLogger().log(sb);
		}
	}
}
