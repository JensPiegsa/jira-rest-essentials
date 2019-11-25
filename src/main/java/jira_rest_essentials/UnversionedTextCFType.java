package jira_rest_essentials;

import com.atlassian.jira.issue.customfields.impl.RenderableTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

/**
 * see: https://community.atlassian.com/t5/Jira-questions/How-to-avoid-to-track-changes-in-history-for-a-custom-field/qaq-p/448406
 * see: https://developer.atlassian.com/server/jira/platform/creating-a-custom-field-type/
 * see: https://community.atlassian.com/t5/Answers-Developer-Questions/How-do-I-change-the-Type-of-a-Custom-field/qaq-p/507030
 */
@Scanned
public class UnversionedTextCFType extends RenderableTextCFType {

	public UnversionedTextCFType(
			@JiraImport final CustomFieldValuePersister customFieldValuePersister,
			@JiraImport final GenericConfigManager genericConfigManager,
			@JiraImport final TextFieldCharacterLengthValidator textFieldCharacterLengthValidator,
			@JiraImport final JiraAuthenticationContext jiraAuthenticationContext) {

		super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator, jiraAuthenticationContext);
	}

	@Override
	public String getChangelogValue(final CustomField field, final String value) {
		// No log into history tab
		return null;
	}
}
