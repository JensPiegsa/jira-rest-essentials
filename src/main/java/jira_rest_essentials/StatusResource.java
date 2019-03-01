package jira_rest_essentials;

import static java.util.stream.Collectors.joining;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.web.action.admin.translation.TranslationManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/**
 * @author Jens Piegsa
 */
@Path("status")
public class StatusResource {

	private static final Logger log = LogManager.getLogger("atlassian.plugin");

	private static final String issueConstantPrefix = "jira.translation.status";

	private static TranslationManager translationManager() {
		return ComponentAccessor.getTranslationManager();
	}

	private static ConstantsManager constantsManager() {
		return ComponentAccessor.getConstantsManager();
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("translations")
	public Response getTranslations() {

		final Set<String> installedLocaleNames = getInstalledLocaleNames();

		List<StatusTranslation> translations = new ArrayList<>();
		for (Status status : constantsManager().getStatuses()) {
			Map<String, String> namesByLocale = new LinkedHashMap<>();
			Map<String, String> descriptionsByLocale = new LinkedHashMap<>();
			namesByLocale.put("default", status.getName());
			descriptionsByLocale.put("default", status.getDescription());
			for (String localeName : installedLocaleNames) {
				if (translationManager().hasLocaleTranslation(status, localeName)) {
					final String nameTranslation = translationManager().getIssueConstantTranslation(status, true, localeName);
					final String descriptionTranslation = translationManager().getIssueConstantTranslation(status, false, localeName);
					namesByLocale.put(localeName, nameTranslation);
					descriptionsByLocale.put(localeName, descriptionTranslation);
				}
			}
			final StatusTranslation statusTranslation = new StatusTranslation();
			statusTranslation.setId(status.getId());
			statusTranslation.setName(namesByLocale);
			statusTranslation.setDescription(descriptionsByLocale);
			translations.add(statusTranslation);
		}
		return Response.ok(translations).build();
	}

	private Set<String> getInstalledLocaleNames() {
		final Set<String> installedLocaleNames = translationManager().getInstalledLocales().keySet();
		debugLocales(installedLocaleNames);
		return installedLocaleNames;
	}

	private void debugLocales(final Set<String> localeNames) {
		log.info(localeNames.stream()
				.map(n -> "'" + n + "' -> " + StringUtils.parseLocaleString(n))
				.collect(joining("\n\t", "All locales:\n\t", "\n")));
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("translations")
	public Response putTranslations(final List<StatusTranslation> translations) {
		for (StatusTranslation translation : translations) {
			final Status status = constantsManager().getStatus(translation.getId());
			if (status != null) {
				final Map<String, String> nameByLocale = translation.getName();
				final Map<String, String> descriptionByLocale = translation.getDescription();
				final Set<String> localeNames = nameByLocale.keySet();
				for (String localeName : localeNames) {
					if (!"default".equals(localeName)) {
						final Locale locale = StringUtils.parseLocaleString(localeName);
						if (locale != null) {
							final String name = nameByLocale.get(localeName);
							final String description = descriptionByLocale.get(localeName);

							if (name != null) {
								log.info("Setting translation " + locale + " of "
										+ status.getName() + "[" + status.getId() + "] - "
										+ "name: '" + name + "' description:  '" + description + "' ...");
								translationManager().setIssueConstantTranslation(status, issueConstantPrefix, locale, name, description);
							} else {
								translationManager().deleteIssueConstantTranslation(status, issueConstantPrefix, locale);
							}
						} else {
							log.warn("Could not parse locale " + localeName + ".");
						}
					}
				}
			} else {
				log.warn("Could not find status " + translation.getId() + " -> ignoring translation.");
			}
		}
		return Response.status(Response.Status.ACCEPTED).build();
	}
}
