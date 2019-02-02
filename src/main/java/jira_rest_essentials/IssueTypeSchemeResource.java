package jira_rest_essentials;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author Jens Piegsa
 *
 */
@Path("/issuetypescheme")
public class IssueTypeSchemeResource {

	private static final Logger log = LogManager.getLogger("atlassian.plugin");

	// private IssueTypeSchemeManager itsm() {
	// return ComponentAccessor.getIssueTypeSchemeManager();
	// }

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getIssueTypeScheme(@QueryParam("projectKey") final String projectKey) {

		log.debug("get issue type scheme for project " + projectKey);

		// TODO not yet implemented

		/*
		 * @NamedQuery(name = JiraIssueTypeScheme.findByProjectKey, query = "
		 * SELECT b FROM JiraIssueTypeScheme b"
		 * + " INNER JOIN b.jiraIssueTypeSchemeOfProject c"
		 * + " INNER JOIN c.jiraProject d"
		 * + " WHERE d.projectKey = :projectKey"),
		 */

		// itsm();

		// final ProjectManager projectManager = ComponentAccessor.getProjectManager();
		// final Project project = projectManager.getProjectByCurrentKey(projectKey);
		// final Collection<IssueType> issueTypes = project.getIssueTypes();

		// project.get
		// for (final IssueType issueType : issueTypes) {
		// }
		return Response.status(Status.NOT_FOUND).build();
	}

}
