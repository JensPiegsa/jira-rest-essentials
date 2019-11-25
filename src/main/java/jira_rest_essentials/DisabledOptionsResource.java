package jira_rest_essentials;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;

@Path("/disabled-options")
public class DisabledOptionsResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDisabledOptions() {

		final List<Option> allOptions = options().getAllOptions();

		final List<Long> disabledOptions = allOptions.stream()
				.filter(Option::getDisabled)
				.map(Option::getOptionId)
				.collect(toList());

		return Response.ok(disabledOptions).build();
	}

	private OptionsManager options() {
		return ComponentAccessor.getOptionsManager();
	}
}
