package jira_rest_essentials;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * @author Jens Piegsa
 */
@JsonAutoDetect
public class CustomFieldTranslation implements Serializable {

	@JsonProperty
	private String id;

	@JsonProperty
	private Map<String, String> name = new LinkedHashMap<>();

	@JsonProperty
	private Map<String, String> description = new LinkedHashMap<>();

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

	@Override
	public String toString() {
		return "CustomFieldTranslation[\n"
				+ " id = " + id + "\n"
				+ " name = " + name + "\n"
				+ " description = " + description + "\n"
				+ "]\n";
	}
}
