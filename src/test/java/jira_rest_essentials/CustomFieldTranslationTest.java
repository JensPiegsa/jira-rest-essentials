package jira_rest_essentials;

import static org.junit.Assert.*;

import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.junit.Test;

public class CustomFieldTranslationTest {

	@Test
	public void shouldBeDeserializable() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ObjectReader reader = mapper.reader(CustomFieldTranslation.class);

		// given
		final String src = "{\n" +
				"  \"id\" : \"customfield_12345\",\n" +
				"  \"name\" : {\n" +
				"    \"de_DE\" : \"Benutzerdefiniertes Feld\",\n" +
				"    \"en_GB\" : \"Custom field\"\n" +
				"  },\n" +
				"  \"description\" : {\n" +
				"    \"de_DE\" : \"Dieses Feld ist toll!\",\n" +
				"    \"en_GB\" : \"This field is great!\"\n" +
				"  }\n" +
				"}";

		// when
		final Object actual = reader.readValue(src);

		// then
		assertEquals("CustomFieldTranslation[\n" +
				" id = customfield_12345\n" +
				" name = {de_DE=Benutzerdefiniertes Feld, en_GB=Custom field}\n" +
				" description = {de_DE=Dieses Feld ist toll!, en_GB=This field is great!}\n" +
				"]\n", actual.toString());
	}

	@Test
	public void shouldBeDeserializableFromList() throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		// given
		final String json = "[\n" +
				"  {\n" +
				"    \"id\" : \"customfield_12345\",\n" +
				"    \"name\" : {\n" +
				"      \"de_DE\" : \"Benutzerdefiniertes Feld\",\n" +
				"      \"en_GB\" : \"Custom field\"\n" +
				"    },\n" +
				"    \"description\" : {\n" +
				"      \"de_DE\" : \"Dieses Feld ist toll!\",\n" +
				"      \"en_GB\" : \"This field is great!\"\n" +
				"    }\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\" : \"customfield_55555\",\n" +
				"    \"name\" : {\n" +
				"      \"de_DE\" : \"Einfach noch ein Feld\",\n" +
				"      \"en_GB\" : \"yet another field\"\n" +
				"    },\n" +
				"    \"description\" : {\n" +
				"      \"de_DE\" : \"Bitte nicht ausfüllen.\",\n" +
				"      \"en_GB\" : \"Please leave this field empty.\"\n" +
				"    }\n" +
				"  }\n" +
				"]";

		// when
		List<CustomFieldTranslation> actual = mapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, CustomFieldTranslation.class));

		// then
		assertEquals("[CustomFieldTranslation[\n" +
				" id = customfield_12345\n" +
				" name = {de_DE=Benutzerdefiniertes Feld, en_GB=Custom field}\n" +
				" description = {de_DE=Dieses Feld ist toll!, en_GB=This field is great!}\n" +
				"]\n" +
				", CustomFieldTranslation[\n" +
				" id = customfield_55555\n" +
				" name = {de_DE=Einfach noch ein Feld, en_GB=yet another field}\n" +
				" description = {de_DE=Bitte nicht ausfüllen., en_GB=Please leave this field empty.}\n" +
				"]\n" +
				"]", actual.toString());
	}
}