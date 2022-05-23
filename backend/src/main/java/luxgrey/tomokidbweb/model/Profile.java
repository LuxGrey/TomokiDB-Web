package luxgrey.tomokidbweb.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import luxgrey.tomokidbweb.annotation.Generated;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Central entity of the application
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@Schema(description =
    "Model that encompasses multiple aliases and weblinks and can be associated with tags")
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ElementCollection(fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  private List<Alias> aliases = new ArrayList<>();

  @ElementCollection
  private List<Weblink> weblinks = new ArrayList<>();

  @ManyToMany(fetch = FetchType.EAGER)
  // define unqiue constraint to prevent duplicates of profile-tag relationships
  // unique constraint is only used during DDL generation
  @Fetch(value = FetchMode.SUBSELECT)
  @JoinTable(name = "profile_tags",
      joinColumns = @JoinColumn(name = "profile_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"),
      uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "tag_id"})
  )
  private List<Tag> tags = new ArrayList<>();

  @Generated
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

  @Generated
  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
