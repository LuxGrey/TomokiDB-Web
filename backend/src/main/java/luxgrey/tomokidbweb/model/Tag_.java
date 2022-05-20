package luxgrey.tomokidbweb.model;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Tag.class)
public class Tag_ {
  public static volatile SingularAttribute<Tag, Long> id;
  public static volatile SingularAttribute<Tag, String> name;
  public static volatile ListAttribute<Tag, Profile> profiles;
}
