package luxgrey.tomokidbweb.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

/**
 * Central entity of the application
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ElementCollection
  private List<Alias> aliases = new ArrayList<>();

  @ElementCollection
  private List<Weblink> weblinks = new ArrayList<>();

  @ManyToMany
  // define unqiue constraint to prevent duplicates of profile-tag relationships
  // unique constraint is only used during DDL generation
  @JoinTable (name = "profile_tags",
      joinColumns = @JoinColumn(name="profile_id"),
      inverseJoinColumns = @JoinColumn(name="tag_id"),
      uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "tag_id"})
  )
  private List<Tag> tags = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Profile profile = (Profile) o;
    return id != null && Objects.equals(id, profile.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
