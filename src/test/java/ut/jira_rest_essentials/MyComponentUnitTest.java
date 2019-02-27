package ut.jira_rest_essentials;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jira_rest_essentials.api.MyPluginComponent;
import jira_rest_essentials.impl.MyPluginComponentImpl;
import org.junit.jupiter.api.Test;

class MyComponentUnitTest {

	@Test
	public void testMyName() {
		MyPluginComponent component = new MyPluginComponentImpl(null);
		assertEquals("myComponent", component.getName(), "names do not match!");
	}
}