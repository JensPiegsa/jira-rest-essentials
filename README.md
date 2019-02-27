# JIRA REST Essentials

[![Build Status](https://travis-ci.org/JensPiegsa/jira-rest-essentials.svg?branch=master)](https://travis-ci.org/JensPiegsa/jira-rest-essentials)
[![codecov](https://codecov.io/gh/JensPiegsa/jira-rest-essentials/branch/master/graph/badge.svg)](https://codecov.io/gh/JensPiegsa/jira-rest-essentials)

This JIRA plugin exists to close some gaps of the [JIRA REST API](https://docs.atlassian.com/jira/REST/ondemand/). 

### FieldScreen

#### `GET https:/example.com/jira/rest/essentials/1.0/screen`

* lists all screens by id and name

#### `GET https:/example.com/jira/rest/essentials/1.0/screen?projectKey={projectKey}`

* lists all screens associated with a given project

#### `GET https:/example.com/jira/rest/essentials/1.0/screen?name={screenName}`

* returns the id for a screen with the given name

### CustomField

#### `GET https:/example.com/jira/rest/essentials/1.0/customfield/description`

* lists all custom field descriptions by field (with prefix `custom_*`)

#### `GET https:/example.com/jira/rest/essentials/1.0/customfield/description?id={id}`

* returns a custom field description for a given field (id as `long` without prefix)

#### `GET https:/example.com/jira/rest/essentials/1.0/customfield/translations`

* returns all available translations for all fields

#### `PUT https:/example.com/jira/rest/essentials/1.0/customfield/translations`

Request body example:
```json
[
    {
        "id" : "customfield_12345",
        "name" : {
            "de_DE" : "Hinweis",
            "en_UK" : "Hint"
        },
        "description" : {
            "de_DE" : "Zusatzinformation",
            "en_UK" : "Additional information"
        }
    }
]
```

---

### Build the Add-on ###

* Use Maven to build a deployable jar file: `mvn clean package`