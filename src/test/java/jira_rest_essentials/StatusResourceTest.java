package jira_rest_essentials;

import static com.github.jenspiegsa.restassuredextension.PostConstructPojoResourceFactory.wired;
import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.will;
import static org.mockito.Mockito.spy;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.status.MockStatus;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.web.action.admin.translation.TranslationManager;
import com.github.jenspiegsa.restassuredextension.ConfigureRestAssured;
import com.github.jenspiegsa.restassuredextension.RestAssuredExtension;
import io.restassured.http.ContentType;
import java.util.LinkedHashMap;
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

	@BeforeEach
	void setUp() {

		ComponentAccessor.initialiseWorker(new MockComponentWorker()
				.addMock(TranslationManager.class, translationManager)
				.addMock(ConstantsManager.class, constantsManager));

		final Map<String,String> testLocales = new LinkedHashMap<>();
		testLocales.put("de", Locale.GERMAN.getDisplayName(Locale.GERMAN));
		testLocales.put("en_US", Locale.US.getDisplayName(Locale.GERMAN));

		given(translationManager.getInstalledLocales())
				.willReturn(testLocales);

		final MockStatus statusOne = spy(new MockStatus("1", "Open"));
		statusOne.setDescription("An open issue.");
		will(invocation -> "de".equals(invocation.getArgument(0)) ? "Offen" : "Open")
				.given(statusOne).getNameTranslation(anyString());
		will(invocation -> "de".equals(invocation.getArgument(0)) ? "Ein offener Vorgang." : "An open issue.")
				.given(statusOne).getDescTranslation(anyString());

		final MockStatus statusTwo = spy(new MockStatus("2", "Closed"));
		statusTwo.setDescription("A closed issue.");
		will(invocation -> "de".equals(invocation.getArgument(0)) ? "Geschlossen" : "Closed")
				.given(statusTwo).getNameTranslation(anyString());
		will(invocation -> "de".equals(invocation.getArgument(0)) ? "Ein geschlossener Vorgang." : "A closed issue.")
				.given(statusTwo).getDescTranslation(anyString());

		given(constantsManager.getStatuses())
				.willReturn(asList(statusOne, statusTwo));
	}

	@Test @DisplayName("HTTP GET /status/translations.")
	void testGet() {

		given()
			.accept(ContentType.JSON)
		.when()
			.get("/jira/rest/essentials/1.0/status/translations")
		.then()
			.log().all()
			.statusCode(Response.Status.OK.getStatusCode());
	}
}