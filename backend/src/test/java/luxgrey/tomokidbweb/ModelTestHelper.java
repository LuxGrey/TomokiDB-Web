package luxgrey.tomokidbweb;

import java.util.ArrayList;
import java.util.List;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.model.Weblink;

/**
 * Provides functions for generating model instances with example data
 */
public class ModelTestHelper {

  public static List<Profile> createProfilesWithAliasesAndWeblinks(
      int amountProfiles,
      int aliasesPerProfile,
      int weblinksPerProfile) {
    List<Profile> profiles = new ArrayList<>();

    for (int i = 0; i < amountProfiles; i++) {
      Profile profile = new Profile();

      for (int k = 0; k < aliasesPerProfile; k++) {
        Alias alias = new Alias();
        alias.setName("Alias" + i + "_" + k);
        profile.getAliases().add(alias);
      }

      for (int l = 0; l < weblinksPerProfile; l++) {
        Weblink weblink = new Weblink();
        weblink.setUrl("https://www.example.org/" + i + "-" + l);
        profile.getWeblinks().add(weblink);
      }

      profiles.add(profile);
    }

    return profiles;
  }

  public static List<Tag> createTags(int amount) {
    List<Tag> tags = new ArrayList<>();

    for (int i = 0; i < amount; i++) {
      Tag tag = new Tag();
      tag.setName("Tag" + amount);
      tags.add(tag);
    }

    return tags;
  }
}
