package jira_rest_essentials;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Jens Piegsa
 */
@JsonAutoDetect
public class WorkflowReference implements Serializable {

	@JsonProperty
	private Object id;

	public WorkflowReference(final Long id) {
		this.id = id;
	}

	public Object getId() {
		return id;
	}

	public void setId(final Object id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "WorkflowReference[\n"
				+ " id = " + id + "\n"
				+ "]";
	}
}
