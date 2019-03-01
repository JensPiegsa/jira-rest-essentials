package jira_rest_essentials;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

/**
 * @author Jens Piegsa
 */
@Path("workflowscheme")
public class WorkflowSchemeResource {

	private static final Logger log = LogManager.getLogger("atlassian.plugin");

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getId(@QueryParam("projectKey") final String projectKey) {

		final Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		try {
			final GenericValue workflowScheme = ComponentAccessor.getWorkflowSchemeManager().getWorkflowScheme(project);
			log.info("workflowScheme: " + workflowScheme);
			final Long id = (Long) workflowScheme.get("id");
			return Response.ok(new WorkflowReference(id)).build();
		} catch (GenericEntityException e) {
			log.warn(e.getMessage(), e);
			return Response.status(NOT_FOUND).entity(e.getMessage()).build();
		}
	}
}
