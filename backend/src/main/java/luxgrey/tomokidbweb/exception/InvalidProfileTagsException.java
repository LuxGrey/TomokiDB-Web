package luxgrey.tomokidbweb.exception;

/**
 * An Exception that is meant to be thrown when the Tag information that is part of a
 * Profile POST or PUT request is invalid
 */
public class InvalidProfileTagsException extends Exception {

  public InvalidProfileTagsException(String message) {
    super(message);
  }
}
