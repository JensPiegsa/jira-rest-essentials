package jira_rest_essentials;

import static com.github.jenspiegsa.restassuredextension.PostConstructPojoResourceFactory.wired;
import static io.restassured.RestAssured.given;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.jboss.resteasy.spi.ResourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atlassian.jira.action.issue.customfields.option.MockOption;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.MockCustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.MockFieldConfigScheme;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.github.jenspiegsa.restassuredextension.ConfigureRestAssured;
import com.github.jenspiegsa.restassuredextension.RestAssuredExtension;

import io.restassured.http.ContentType;

/**
 * @author Konrad Biedowicz
 */
@DisplayName("CreateOptionsResource")
@ExtendWith(MockitoExtension.class)
@ExtendWith(RestAssuredExtension.class)
class CreateOptionsRessourceTest {


	@ConfigureRestAssured(contextPath = "/jira/rest/essentials/1.0", port = 8989)
	ResourceFactory[] resourceFactory = { wired(OptionRessource.class, nop -> {
	}) };

	@Mock OptionsManager optionsManager;
	@Mock CustomFieldManager customFieldManager;
	@Mock FieldConfig fieldConfig;

	@BeforeEach
	void setUp() {

		ComponentAccessor.initialiseWorker(
				new MockComponentWorker()
						.addMock(OptionsManager.class, optionsManager)
						.addMock(CustomFieldManager.class, customFieldManager)
		);
	}

	@Test
	@DisplayName("HTTP PUT /option")
	void testPutOption() {

		final List<FieldConfigScheme> configSchemes = singletonList(new FakeFieldConfigScheme(fieldConfig));
		final CustomField customField = new FakeCustomField("customfield_12345", "Optionsfeld", configSchemes);

		given(customFieldManager.getCustomFieldObject("customfield_12345"))
				.willReturn(customField);

		given(optionsManager.createOption(any(), any(), anyLong(), anyString()))
				.willReturn(makeOption(1L, "A"));

		given()
			.accept(ContentType.JSON)
		.when()
			.contentType("application/json")
				.put("/jira/rest/essentials/1.0/option?customfieldId=customfield_12345&value=A")
		.then()
			.statusCode(CREATED.getStatusCode());

	}

	private static Option makeOption(final long optionId, final String optionValue) {
		return new MockOption(null, null, null, optionValue, null, optionId);
	}

	private static class FakeFieldConfigScheme extends MockFieldConfigScheme {

		FieldConfig fieldConfig;

		public FakeFieldConfigScheme(final FieldConfig fieldConfig) {
			this.fieldConfig = fieldConfig;
		}

		@Override
		public MultiMap getConfigsByConfig() {
			final MultiMap multiMap = new MultiValueMap();
			multiMap.put(fieldConfig, "unused value");
			return multiMap;
		}
	}

	private static class FakeCustomField extends MockCustomField {
		private final List<FieldConfigScheme> configSchemes;

		private FakeCustomField(final String id, final String name, final List<FieldConfigScheme> configSchemes) {
			super(id, name, null);
			this.configSchemes = configSchemes;
		}

		@Override
		public List<FieldConfigScheme> getConfigurationSchemes() {
			return configSchemes;
		}
	}
}