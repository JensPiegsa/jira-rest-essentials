package jira_rest_essentials;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;

/**
 * @author Jens Piegsa
 *
 */
@Path("/customfield")
public class CustomFieldResource {

	private static final Logger log = LogManager.getLogger("atlassian.plugin");

	private static CustomFieldManager cfm() {
		return ComponentAccessor.getCustomFieldManager();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("description")
	public Response getScreen(@QueryParam("id") final Long id) {

		if (id != null) {
			log.debug("get custom field description id: " + id);
			return Response.ok(getCustomFieldById(id)).build();
		}
		log.debug("get all custom field descriptions");
		return Response.ok(getCustomFields()).build();
	}

	private Map<String, String> getCustomFields() {
		final Collection<CustomField> customFields = cfm().getCustomFieldObjects();
		final Map<String, String> screens = new HashMap<>();
		for (final CustomField customField : customFields) {
			screens.put(customField.getId(), customField.getDescription());
		}

		return screens;
	}

	private Map<String, String> getCustomFieldById(final long id) {
		final CustomField customField = cfm().getCustomFieldObject(id);
		return Collections.singletonMap(customField.getId(), customField.getName());
	}
}
