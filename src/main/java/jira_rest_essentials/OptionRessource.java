package jira_rest_essentials;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;

/**
 *
 * Adds option values only to the first configuration scheme of the custom field.
 *
 * @author Konrad Biedowicz
 * @see: https://community.atlassian.com/t5/Answers-Developer-Questions/How-do-I-programatically-create-and-add-options-to-custom-fields/qaq-p/483301
 *
 */

@Path("option")
public class OptionRessource {

	private static final Logger log = LogManager.getLogger("atlassian.plugin");

	@PUT
	@Produces({MediaType.APPLICATION_JSON})
	public Response createOption(
			@QueryParam("customfieldId") final String customfieldId,
			@QueryParam("value") final String optionValue) {

		log.info("Creating option for customField: " + customfieldId + " and value: " + optionValue + " ...");

		final CustomField customField = customFieldManager().getCustomFieldObject(customfieldId);
		if (customField == null) {
			return error(NOT_FOUND, "No customField found for id " + customfieldId);
		}

		final Option createdOption = createOption(customField, optionValue);

		if (createdOption == null) {
			return error(INTERNAL_SERVER_ERROR, "Could not create new option " + optionValue + " to customField with id " + customfieldId);
		}

		log.info("Creating option for successfully.");
		return Response.status(CREATED)
				.entity(singletonMap("id", createdOption.getOptionId()))
				.build();
	}

	private Option createOption(final CustomField customField, final String optionValue) {

		final List<FieldConfigScheme> schemes = customField.getConfigurationSchemes();
		if (schemes == null || schemes.isEmpty()) {
			log.error("Error: no field config schemes found");
			return null;
		}

		final FieldConfigScheme firstScheme = readFirstFieldConfigScheme(schemes);

		final Map<?, ?> configs = firstScheme.getConfigsByConfig();
		if (configs == null || configs.isEmpty()) {
			log.error("Warning: no field config found");
			return null;
		}

		final FieldConfig fieldConfig = readFirstFieldConfig(configs);

		// TODO What is this
		final long numberAdded = 100L;
		final Long parentOptionId = null;

		return optionsManager().createOption(fieldConfig, parentOptionId, numberAdded, optionValue);
	}

	private FieldConfigScheme readFirstFieldConfigScheme(final List<FieldConfigScheme> schemes) {
		final FieldConfigScheme firstScheme = schemes.get(0);
		if (schemes.size() > 1) {
			log.warn("Warning: multiple field config schemes found, using first: " + firstScheme.getName());
		}
		return firstScheme;
	}

	private FieldConfig readFirstFieldConfig(final Map<?, ?> configs) {
		final Iterator<?> iterator = configs.keySet().iterator();
		final FieldConfig fieldConfig = (FieldConfig) iterator.next();
		if (iterator.hasNext()) {
			log.warn("Warning: multiple field configs found, using first: " + fieldConfig.getName());
		}
		return fieldConfig;
	}

	private OptionsManager optionsManager() {
		return ComponentAccessor.getOptionsManager();
	}

	private CustomFieldManager customFieldManager() {
		return ComponentAccessor.getCustomFieldManager();
	}

	private static Response error(final Response.Status status, final String message) {
		return Response.status(status)
				.entity(singletonMap("message", message))
				.build();
	}
}
