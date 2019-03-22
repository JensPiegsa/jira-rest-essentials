package jira_rest_essentials;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author Jens Piegsa
 */
@Path("comment")
public class CommentResource {

	private static final Logger log = LogManager.getLogger("atlassian.plugin");

	static final String ADMINISTRATORS = "Administrators";

	private static CommentManager commentManager() {
		return ComponentAccessor.getCommentManager();
	}

	private static UserManager userManager() {
		return ComponentAccessor.getUserManager();
	}

	private static IssueManager issueManager() {
		return ComponentAccessor.getIssueManager();
	}

	private static ProjectRoleManager projectRoleManager() {
		return ComponentAccessor.getComponentOfType(ProjectRoleManager.class);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON})
	public Response createComment(@QueryParam("issueKey") final String issueKey, final SimpleComment simpleComment) {

		log.info("Creating comment for issue " + issueKey + "...");

		final Issue issue = issueManager().getIssueObject(issueKey);
		if (issue == null) {
			return error(NOT_FOUND, "No issue found for issueKey " + issueKey);
		}

		final ApplicationUser applicationUser = userManager().getUserByName(simpleComment.getAuthor());
		if (applicationUser == null) {
			return error(BAD_REQUEST, "No user found with name " + simpleComment.getAuthor());
		}

		final String body = simpleComment.getBody();
		if (body == null) {
			return error(BAD_REQUEST, "No \"body\" defined for comment.");
		}

		final String groupLevel = null;
		final Long roleLevelId = simpleComment.getProjectAdminsOnly() ? projectRoleManager().getProjectRole(ADMINISTRATORS).getId() : null; // 10002L;
		final boolean dispatchEvent = true;

		final Comment comment = commentManager().create(issue, applicationUser, body, groupLevel, roleLevelId, dispatchEvent);
		if (comment == null || comment.getId() == null) {
			return error(INTERNAL_SERVER_ERROR, "Could not create comment for issue " + issueKey + ".");
		}

		log.info("Comment created with id " + comment.getId() + " for issue " + issue.getKey() + ".");

		return Response.status(CREATED)
				.entity(singletonMap("id", comment.getId()))
				.build();
	}

	private static Response error(final Response.Status status, final String message) {
		return Response.status(status)
				.entity(singletonMap("message", message))
				.build();
	}
}
