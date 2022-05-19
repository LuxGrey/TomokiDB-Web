package luxgrey.tomokidbweb.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

/**
 * Used to categorise profiles
 */
@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  // name column should be unique, as it does not make sense for there to be duplicate tags
  // unique constraint is only used during DDL generation
  @Column(unique=true)
  private String name;

  @ManyToMany
  private List<Profile> profiles = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Tag tag = (Tag) o;
    return id != null && Objects.equals(id, tag.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
