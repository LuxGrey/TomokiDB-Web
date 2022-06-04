package luxgrey.tomokidbweb.controller.validator;

/**
 * A container for constants that describe the locations of various
 * JSON schemas
 */
public interface JsonSchemaLocations {
  String SCHEMA_PATH_PREFIX = "classpath:/json-schema/";

  String PROFILE_POST_OR_PUT = SCHEMA_PATH_PREFIX + "profile-post-or-put.json";
}
