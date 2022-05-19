package luxgrey.tomokidbweb.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents one of possibly multiple names for the same profile
 */
@Embeddable
@Getter
@Setter
public class Alias {

  // name column has to be unique, as it is used to uniquely identify profiles during searches
  // unique constraint is only used during DDL generation
  @Column(unique=true)
  private String name;
}
