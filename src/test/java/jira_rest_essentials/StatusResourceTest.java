package jira_rest_essentials;

import static com.github.jenspiegsa.restassuredextension.PostConstructPojoResourceFactory.wired;
import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.IssueConstant;
import com.atlassian.jira.issue.status.MockStatus;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.web.action.admin.translation.TranslationManager;
import com.github.jenspiegsa.restassuredextension.ConfigureRestAssured;
import com.github.jenspiegsa.restassuredextension.RestAssuredExtension;
import io.restassured.http.ContentType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.spi.ResourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Jens Piegsa
 */
@DisplayName("StatusResource")
@ExtendWith(MockitoExtension.class)
@ExtendWith(RestAssuredExtension.class)
class StatusResourceTest {

	@ConfigureRestAssured(contextPath = "/jira/rest/essentials/1.0", port = 8989)
	ResourceFactory[] resourceFactory = {wired(StatusResource.class, nop -> {})};

	@Mock TranslationManager translationManager;
	@Mock ConstantsManager constantsManager;
	private MockStatus openStatus;
	private MockStatus closedStatus;

	@BeforeEach
	void setUp() {

		ComponentAccessor.initialiseWorker(new MockComponentWorker()
				.addMock(TranslationManager.class, translationManager)
				.addMock(ConstantsManager.class, constantsManager));

		final Map<String, String> allLocales = new LinkedHashMap<>();
		allLocales.put("de", Locale.GERMAN.getDisplayName(Locale.GERMAN));
		allLocales.put("en_US", Locale.US.getDisplayName(Locale.GERMAN));
		allLocales.put("zh", Locale.CHINESE.getDisplayName(Locale.GERMAN));

		given(translationManager.getInstalledLocales())
				.willReturn(allLocales);

		openStatus = mockStatus("1", "Open", "An open issue.");
		closedStatus = mockStatus("2", "Closed", "A closed issue.");
		final List<Status> allStatus = asList(openStatus, closedStatus);

		final List<String> translatedLocales = asList("de", "en_US");

		given(constantsManager.getStatuses())
				.willReturn(allStatus);

		//noinspection SuspiciousMethodCalls
		given(translationManager.hasLocaleTranslation(any(IssueConstant.class), any(String.class)))
				.will(invocation -> allStatus.contains(invocation.getArgument(0)) &&
						translatedLocales.contains(invocation.getArgument(1)));

		given(translationManager.getIssueConstantTranslation(any(IssueConstant.class), anyBoolean(), any(String.class)))
				.will(invocation -> mockedTranslations(invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2)));
	}

	private String mockedTranslations(final IssueConstant issueConstant, final boolean name, final String localeName) {

		if (openStatus.equals(issueConstant)) {
			switch (localeName) {
				case "de" : return name ? "Offen" : "Ein offener Vorgang.";
				case "en_US" : return name ? "Open" : "An open issue.";
			}
		} else if (closedStatus.equals(issueConstant)) {
			switch (localeName) {
				case "de" : return name ? "Geschlossen" : "Ein geschlossener Vorgang.";
				case "en_US" : return name ? "Closed" : "A closed issue.";
			}
		}
		return null;
	}

	@Test @DisplayName("HTTP GET /status/translations.")
	void testGet() {

		given()
			.accept(ContentType.JSON)
		.when()
			.get("/jira/rest/essentials/1.0/status/translations")
		.then()
			.log().all()
			.statusCode(Response.Status.OK.getStatusCode())
			.body("find { it.id == '1'}.name.de", equalTo("Offen"))
			.body("find { it.id == '1'}.description.de", equalTo("Ein offener Vorgang."))
			.body("find { it.id == '1'}.name.en_US", equalTo("Open"))
			.body("find { it.id == '1'}.description.en_US", equalTo("An open issue."))
			.body("find { it.id == '2'}.name.de", equalTo("Geschlossen"))
			.body("find { it.id == '2'}.description.de", equalTo("Ein geschlossener Vorgang."))
			.body("find { it.id == '2'}.name.en_US", equalTo("Closed"))
			.body("find { it.id == '2'}.description.en_US", equalTo("A closed issue."))
		;
	}

	private static MockStatus mockStatus(final String id, final String defaultName, final String defaultDescription) {
		final MockStatus status = new MockStatus(id, defaultName);
		status.setDescription(defaultDescription);
		return status;
	}
}