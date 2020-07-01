package net.jplugin.core.kernel.api;

public interface IBindAnnotationTransformer {
	public void transform(AbstractPlugin p,Class c, Object anno);
}
