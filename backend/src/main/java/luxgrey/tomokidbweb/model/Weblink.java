package luxgrey.tomokidbweb.model;

import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a weblink that is part of a profile
 */
@Embeddable
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Weblink {

  @EqualsAndHashCode.Include
  private String url;
}
