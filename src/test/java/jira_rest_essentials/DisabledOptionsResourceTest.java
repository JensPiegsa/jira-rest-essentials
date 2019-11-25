package jira_rest_essentials;

import static com.github.jenspiegsa.restassuredextension.PostConstructPojoResourceFactory.wired;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.spi.ResourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atlassian.jira.action.issue.customfields.option.MockOption;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.github.jenspiegsa.restassuredextension.ConfigureRestAssured;
import com.github.jenspiegsa.restassuredextension.RestAssuredExtension;

import io.restassured.http.ContentType;

/**
 * @author Konrad Biedowicz
 */
@DisplayName("DisabledOptionsResource")
@ExtendWith(MockitoExtension.class)
@ExtendWith(RestAssuredExtension.class)
class DisabledOptionsResourceTest {

	private static final boolean ENABLED = false;
	private static final boolean DISABLED = true;

	private static final String SIMPLE_JSON_ARRAY = "$";

	@ConfigureRestAssured(contextPath = "/jira/rest/essentials/1.0", port = 8989)
	ResourceFactory[] resourceFactory = { wired(DisabledOptionsResource.class, nop -> {
	}) };

	@Mock OptionsManager optionsManager;

	@BeforeEach
	void setUp() {

		ComponentAccessor.initialiseWorker(
				new MockComponentWorker()
						.addMock(OptionsManager.class, optionsManager)
		);
	}

	@Test
	@DisplayName("HTTP GET /disabled-options")
	void testGet() {

		final List<Option> allOptions = new ArrayList<>();
		allOptions.add(makeOption(1L, "enabledValue A", ENABLED));
		allOptions.add(makeOption(2L, "disabledValue B", DISABLED));
		allOptions.add(makeOption(3L, "disabledValue C", DISABLED));

		given(optionsManager.getAllOptions()).willReturn(allOptions);

		given()
			.accept(ContentType.JSON)
		.when()
			.get("/jira/rest/essentials/1.0/disabled-options")
		.then()
			.statusCode(OK.getStatusCode())
			.body(SIMPLE_JSON_ARRAY, containsInAnyOrder(2, 3)); // assert json response: [2,3]
	}

	private static Option makeOption(final long optionId, final String optionValue, final boolean disabled) {
		final Option option = new MockOption(null, null, null, optionValue, null, optionId);
		option.setDisabled(disabled);
		return option;
	}
}