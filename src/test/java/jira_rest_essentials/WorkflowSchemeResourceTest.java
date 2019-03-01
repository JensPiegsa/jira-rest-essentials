package jira_rest_essentials;

import static com.github.jenspiegsa.restassuredextension.PostConstructPojoResourceFactory.wired;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.github.jenspiegsa.restassuredextension.ConfigureRestAssured;
import com.github.jenspiegsa.restassuredextension.RestAssuredExtension;
import io.restassured.http.ContentType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.spi.ResourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ofbiz.core.entity.GenericEntityException;

/**
 * @author Jens Piegsa
 */
@DisplayName("WorkflowSchemeResource")
@ExtendWith(MockitoExtension.class)
@ExtendWith(RestAssuredExtension.class)
class WorkflowSchemeResourceTest {

	@ConfigureRestAssured(contextPath = "/jira/rest/essentials/1.0", port = 8989)
	ResourceFactory[] resourceFactory = {wired(WorkflowSchemeResource.class, nop -> {})};

	@Mock ProjectManager projectManager;
	@Mock WorkflowSchemeManager workflowSchemeManager;

	@BeforeEach
	void setUp() throws GenericEntityException {

		ComponentAccessor.initialiseWorker(new MockComponentWorker()
				.addMock(ProjectManager.class, projectManager)
				.addMock(WorkflowSchemeManager.class, workflowSchemeManager));

		given(projectManager.getProjectByCurrentKey("NAKO"))
				.willReturn(new MockProject(1, "NAKO"));

		given(workflowSchemeManager.getWorkflowScheme(any(Project.class)))
				.willReturn(new MockGenericValue("WorkflowScheme", 42L));
	}

	@Test @DisplayName("Test HTTP GET with projectKey query parameter.")
	void testGet() {

		given()
			.accept(ContentType.JSON)
		.when()
			.get("/jira/rest/essentials/1.0/workflowscheme?projectKey=NAKO")
		.then()
			.log().all()
			.statusCode(Response.Status.OK.getStatusCode())
			.body("id", equalTo(42));
	}
}