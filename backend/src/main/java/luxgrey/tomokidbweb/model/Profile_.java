package luxgrey.tomokidbweb.model;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Profile.class)
public class Profile_ {

  public static volatile SingularAttribute<Profile, Long> id;
  public static volatile ListAttribute<Profile, Alias> aliases;
  public static volatile ListAttribute<Profile, Weblink> weblinks;
  public static volatile ListAttribute<Profile, Tag> tags;

}
