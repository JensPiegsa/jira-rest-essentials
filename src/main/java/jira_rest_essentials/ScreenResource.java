package jira_rest_essentials;

import static java.util.Collections.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenImpl;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeManager;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;

/**
 * @author Jens Piegsa
 *
 */
@Path("screen")
public class ScreenResource {

	private static final Logger log = LogManager.getLogger("atlassian.plugin");

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response createOrUpdateScreen(@QueryParam("name") final String name, @QueryParam("description") final String description) {

		if (isBlank(name)) {
			return Response.status(Status.BAD_REQUEST).entity(singletonMap("message", "name param must not be empty.")).build();
		}

		if (isBlank(description)) {
			return Response.status(Status.BAD_REQUEST).entity(singletonMap("message", "description param must not be empty.")).build();
		}

		final FieldScreen existingScreen = findScreenByName(name);
		if (existingScreen == null) {
			final FieldScreen screen = makeScreen(name, description);

			try {
				fieldScreens().createFieldScreen(screen);
				return Response.ok(singletonMap("id", screen.getId())).build();
			} catch (final RuntimeException e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(singletonMap("message", e.getMessage())).build();
			}
		} else {
			return Response.ok(singletonMap("id", existingScreen.getId())).build();
		}
	}

	private FieldScreen makeScreen(final String name, final String description) {
		final FieldScreen screen = new FieldScreenImpl(fieldScreens());
		screen.setName(name);
		screen.setDescription(description);
		return screen;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getScreen(
			@QueryParam("id") final Long id,
			@QueryParam("name") final String name,
			@QueryParam("projectKey") final String projectKey) {

		log.debug("get screen id: " + id + " name: " + name);

		if (isNotBlank(name) && id != null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		final Map<Long, String> screens = findMatchingScreens(id, name, projectKey);

		log.debug("screens found: " + screens);

		final ResponseBuilder response = screens.isEmpty() ? Response.status(Status.NOT_FOUND) : Response.ok(screens);
		return response.build();
	}

	private Map<Long, String> findMatchingScreens(final Long id, final String name, final String projectKey) {

		if (isNotBlank(projectKey)) {
			return findFieldScreensOfProject(projectKey);
		} else if (id != null) {
			return findFieldScreenById(id);
		} else if (isNotBlank(name)) {
			return findFieldScreenByName(name);
		}
		return getFieldScreens();
	}

	private Map<Long, String> findFieldScreensOfProject(final String projectKey) {

		final Map<Long, String> screens = new HashMap<>();

		final Project project = projects().getProjectByCurrentKey(projectKey);

		final IssueTypeScreenScheme itss = issueTypeScreenSchemes().getIssueTypeScreenScheme(project);

		for (final IssueType issueType : project.getIssueTypes()) {
			final FieldScreenScheme fss = itss.getEffectiveFieldScreenScheme(issueType);

			for (final FieldScreenSchemeItem fssi : fieldScreenSchemes().getFieldScreenSchemeItems(fss)) {
				final FieldScreen fieldScreen = fssi.getFieldScreen();
				screens.put(fieldScreen.getId(), fieldScreen.getName());
			}
		}
		return screens;
	}

	private Map<Long, String> getFieldScreens() {

		final Map<Long, String> screens = new HashMap<>();
		for (final FieldScreen fieldScreen : getAllFieldScreens()) {
			screens.put(fieldScreen.getId(), fieldScreen.getName());
		}

		return screens;
	}

	private Map<Long, String> findFieldScreenById(final long id) {
		final FieldScreen fieldScreen = findScreenById(id);
		return Collections.singletonMap(fieldScreen.getId(), fieldScreen.getName());
	}

	private Map<Long, String> findFieldScreenByName(final String name) {
		final FieldScreen fieldScreen = findScreenByName(name);
		return Collections.singletonMap(fieldScreen.getId(), fieldScreen.getName());
	}

	private FieldScreen findScreenByName(final String name) {
		final Collection<FieldScreen> fieldScreens = getAllFieldScreens();
		for (final FieldScreen fieldScreen : fieldScreens) {
			if (Objects.equals(name, fieldScreen.getName())) {
				return fieldScreen;
			}
		}
		return null;
	}

	private Collection<FieldScreen> getAllFieldScreens() {
		return fieldScreens().getFieldScreens();
	}

	private FieldScreen findScreenById(final long id) {
		return fieldScreens().getFieldScreen(id);
	}

	private ProjectManager projects() {
		return ComponentAccessor.getProjectManager();
	}

	private FieldScreenManager fieldScreens() {
		return ComponentAccessor.getFieldScreenManager();
	}

	private FieldScreenSchemeManager fieldScreenSchemes() {
		return ComponentAccessor.getComponent(FieldScreenSchemeManager.class);
	}

	private IssueTypeScreenSchemeManager issueTypeScreenSchemes() {
		return ComponentAccessor.getIssueTypeScreenSchemeManager();
	}
}
