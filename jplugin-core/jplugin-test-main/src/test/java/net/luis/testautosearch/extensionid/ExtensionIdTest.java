package net.luis.testautosearch.extensionid;

import net.jplugin.common.kits.AssertKit;
import net.jplugin.core.kernel.api.ExtensionFactory;
import net.jplugin.core.kernel.api.IStartup;

public class ExtensionIdTest {

	public static void assertExceptionInCreateService() {
		AssertKit.assertException(()->{
			ExtensionFactory.get("WebControllerTest");
		});
	}
	public static void test() {
		Object ext = ExtensionFactory.get("theidabcde");
		AssertKit.assertNotNull(ext, "ext");
		ext = ExtensionFactory.get("theidabcde",ExportTest1.class);
		((ExportTest1)ext).a();
		
		
		Object ext2 = ExtensionFactory.get("theidabcde");
		AssertKit.assertNotNull(ext2, "ext2");
		ext = ExtensionFactory.get("theExportTest2",ExportTest2.class);
		((ExportTest2)ext).a();
		
		Object o = ExtensionFactory.get("WebControllerTest");
		AssertKit.assertNotNull(o, "o");
		((WebControllerTest)o).aaa(null,null);
		
		o = ExtensionFactory.get("WebExControllerTest");
		AssertKit.assertNotNull(o, "o");
		((WebExControllerTest)o).aa();
		
		
		IService1ForId svc = ExtensionFactory.get("IService1ForId",IService1ForId.class);
		svc.a();
		
		IRuleServiceForId rs = ExtensionFactory.get("IRuleServiceForId",IRuleServiceForId.class);
		rs.a();
		
		IStartup bs = ExtensionFactory.get("BindStartUpForId",IStartup.class);
		AssertKit.assertNotNull(bs, "startup");
		
		
		IExtensionForIdTest efit = ExtensionFactory.get("ExtensionForIdTest",IExtensionForIdTest.class);
		AssertKit.assertNotNull(efit, "efit");
		efit.aaa();
		
		
		RuleMethodInterceptorForIdTest rmift = ExtensionFactory.get("RuleMethodInterceptorForIdTest1",RuleMethodInterceptorForIdTest.class);
		AssertKit.assertNotNull(rmift, "rmift");
		rmift.aaa();
		
		RuleMethodInterceptorForIdTest rmift2 = ExtensionFactory.get("RuleMethodInterceptorForIdTest2",RuleMethodInterceptorForIdTest.class);
		AssertKit.assertTrue(rmift==rmift2);
		
	}

}
