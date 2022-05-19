package luxgrey.tomokidbweb.model;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a weblink that is part of a profile
 */
@Embeddable
@Getter
@Setter
public class Weblink {

  private String url;
}
