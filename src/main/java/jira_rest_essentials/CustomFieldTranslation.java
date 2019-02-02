package jira_rest_essentials;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;


/**
 * @author Jens Piegsa
 */
@JsonAutoDetect
public class CustomFieldTranslation implements Serializable {

	@JsonProperty
	private String id;

	@JsonProperty
	@JsonDeserialize(keyAs = String.class, contentAs = String.class, as = LinkedHashMap.class)
	private Map<String, String> name = new LinkedHashMap<>();

	@JsonProperty
	@JsonDeserialize(keyAs = String.class, contentAs = String.class, as = LinkedHashMap.class)
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
