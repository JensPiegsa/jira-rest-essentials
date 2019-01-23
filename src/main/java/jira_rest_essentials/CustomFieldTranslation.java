package jira_rest_essentials;

import java.util.Map;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;


/**
 * @author Jens Piegsa
 */
@JsonAutoDetect
public class CustomFieldTranslation {

	private String id;
	private Map<String, String> name;
	private Map<String, String> description;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Map<String, String> getName() {
		return name;
	}

	public void setName(final Map<String, String> name) {
		this.name = name;
	}

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(final Map<String, String> description) {
		this.description = description;
	}
}
