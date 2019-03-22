package jira_rest_essentials;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Jens Piegsa
 */
@JsonAutoDetect
public class SimpleComment implements Serializable {

	@JsonProperty
	private String author;

	@JsonProperty
	private String body;

	@JsonProperty
	private Boolean projectAdminsOnly;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public String getBody() {
		return body;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public Boolean getProjectAdminsOnly() {
		return projectAdminsOnly != null && projectAdminsOnly;
	}

	public void setProjectAdminsOnly(final Boolean projectAdminsOnly) {
		this.projectAdminsOnly = projectAdminsOnly;
	}

	@Override
	public String toString() {
		return "SimpleComment[\n"
				+ " author = " + author + "\n"
				+ " body = " + body + "\n"
				+ "]\n";
	}
}
