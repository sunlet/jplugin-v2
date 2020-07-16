package net.jplugin.core.kernel.api;

import java.util.List;

public interface IBindAnnotationTransformer {
	public void transform(Class c, Object anno, List<Extension> extensionList);
}
