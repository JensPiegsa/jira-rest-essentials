package ut.jira_rest_essentials;

import org.junit.Test;
import jira_rest_essentials.api.MyPluginComponent;
import jira_rest_essentials.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest {

	@Test
	public void testMyName() {
		MyPluginComponent component = new MyPluginComponentImpl(null);
		assertEquals("names do not match!", "myComponent", component.getName());
	}
}