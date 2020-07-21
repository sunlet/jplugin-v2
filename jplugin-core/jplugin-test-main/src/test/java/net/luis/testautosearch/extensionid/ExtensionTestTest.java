package net.luis.testautosearch.extensionid;

import net.jplugin.common.kits.AssertKit;
import net.jplugin.core.kernel.api.ExtensionFactory;

public class ExtensionTestTest {

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
	}

}
