package jira_rest_essentials;

import static com.github.jenspiegsa.restassuredextension.PostConstructPojoResourceFactory.wired;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.nullable;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyBoolean;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MockComment;
import com.atlassian.jira.mock.MockProjectRoleManager;
import com.atlassian.jira.mock.MockProjectRoleManager.MockProjectRole;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.jira.user.util.UserManager;
import com.github.jenspiegsa.restassuredextension.ConfigureRestAssured;
import com.github.jenspiegsa.restassuredextension.RestAssuredExtension;
import io.restassured.http.ContentType;
import java.util.Date;
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
@DisplayName("CommentResource")
@ExtendWith(MockitoExtension.class)
@ExtendWith(RestAssuredExtension.class)
class CommentResourceTest {

	@ConfigureRestAssured(contextPath = "/jira/rest/essentials/1.0", port = 8989)
	ResourceFactory[] resourceFactory = {wired(CommentResource.class, nop -> {})};

	@Mock CommentManager commentManager;
	@Mock IssueManager issueManager;

	private MockUserManager userManager = new MockUserManager();
	private MockProjectRoleManager projectRoleManager = new MockProjectRoleManager();

	@BeforeEach
	void setUp() {

		ComponentAccessor.initialiseWorker(
				new MockComponentWorker()
						.addMock(CommentManager.class, commentManager)
						.addMock(UserManager.class, userManager)
						.addMock(IssueManager.class, issueManager)
						.addMock(ProjectRoleManager.class, projectRoleManager)
		);

		final MockIssue issue = new MockIssue(123, "NAKO-109");
		given(issueManager.getIssueObject("NAKO-109")).willReturn(issue);

		final MockApplicationUser user = new MockApplicationUser("piegsaj");
		userManager.addUser(user);

		projectRoleManager.addRole(new MockProjectRole(10002L, CommentResource.ADMINISTRATORS, "project administrators"));

		given(commentManager.create(issue, user, "secret comment", null, 10002L, true))
				.willReturn(new MockComment(5L, "piegsaj", "secret comment", null, 10_002L, new Date(), issue));
	}

	@Test @DisplayName("HTTP POST /comment")
	void testPost() {

		given()
			.accept(ContentType.JSON)
		.when()
			.contentType("application/json")
			.body(getClass().getResourceAsStream("post_comment.json"))
			.post("/jira/rest/essentials/1.0/comment?issueKey=NAKO-109")
		.then()
			.log().all()
			.statusCode(CREATED.getStatusCode())
			.body("id", equalTo(5));

		verify(commentManager, times(1))
				.create(any(Issue.class), any(ApplicationUser.class), anyString(), nullable(String.class), any(Long.class), anyBoolean());
	}
}