package net.jplugin.core.kernel.api;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoBindExtensionManager {
	public static AutoBindExtensionManager INSTANCE = new AutoBindExtensionManager();
	
	private List<IBindExtensionHandler> handlers = new ArrayList<IBindExtensionHandler>();
	private Map<Class,IBindAnnotationTransformer> transformers = new HashMap<Class, IBindAnnotationTransformer>();
	
	private AutoBindExtensionManager(){
		handlers.add(new Handler4AnnoTransformer());
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
	
	public void addBindAnnotationTransformer(Class annoClass,IBindAnnotationTransformer bat) {
		transformers.put(annoClass,bat);
	}
	
	class Handler4AnnoTransformer implements IBindExtensionHandler {
		@Override
		public void handle(AbstractPlugin p) {
			if (transformers.isEmpty()) 
				return;
			
			p.filterContainedClassesByChecker(p.getClass().getPackage().getName(),(c)->{
				//假定大部分都没Annotation,所以先获取所有的anno，这样性能更好
				Annotation[] annos = c.getAnnotations();
				if (annos==null || annos.length==0) {
					//do nothing
				}else {
					for (Annotation a:annos) {
						IBindAnnotationTransformer trans = transformers.get(a.getClass());
						if (trans!=null) {
							trans.transform(p, c, a);
						}
					}
				}
				//已经发挥效果，直接返回false
				return false;
			});
		}
	}
}
