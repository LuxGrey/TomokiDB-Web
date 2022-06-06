package luxgrey.tomokidbweb;

import java.util.ArrayList;
import java.util.List;
import luxgrey.tomokidbweb.dto.ProfileDTOShort;
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
    return createProfilesWithAliasesAndWeblinks(
        amountProfiles, aliasesPerProfile, weblinksPerProfile, false);
  }

  public static List<Profile> createProfilesWithAliasesAndWeblinks(
      int amountProfiles,
      int aliasesPerProfile,
      int weblinksPerProfile,
      boolean assignIds) {
    List<Profile> profiles = new ArrayList<>();

    for (int i = 0; i < amountProfiles; i++) {
      Profile profile = createProfileWithAliasesAndWeblinks(
          aliasesPerProfile, weblinksPerProfile, i);

      if (assignIds) {
        profile.setId((long) i + 1);
      }

      profiles.add(profile);
    }

    return profiles;
  }

  public static List<ProfileDTOShort> createProfileDTOShortsWithAliases(
      int amountProfiles,
      int aliasesPerProfile,
      boolean assignIds
  ) {
    List<ProfileDTOShort> profileDTOShorts = new ArrayList<>();

    for (int i = 0; i < amountProfiles; i++) {
      ProfileDTOShort profileDTOShort = new ProfileDTOShort();

      for (int k = 0; k < aliasesPerProfile; k++) {
        Alias alias = new Alias();
        alias.setName("Alias" + i + "_" + k);
        profileDTOShort.getAliases().add(alias);
      }

      profileDTOShorts.add(profileDTOShort);
    }

    return profileDTOShorts;
  }

  /**
   * @param uniqueNumberForProfile number used for generation of Alias names and Weblink URLs that
   *                               must be unique across all profiles in order to satisfy uniqueness
   *                               constraints of Aliases and Weblinks
   */
  public static Profile createProfileWithAliasesAndWeblinks(
      int amountAliases,
      int amountWeblinks,
      int uniqueNumberForProfile) {
    Profile profile = new Profile();

    for (int k = 0; k < amountAliases; k++) {
      Alias alias = new Alias();
      alias.setName("Alias" + uniqueNumberForProfile + "_" + k);
      profile.getAliases().add(alias);
    }

    for (int l = 0; l < amountWeblinks; l++) {
      Weblink weblink = new Weblink();
      weblink.setUrl("https://www.example.org/" + uniqueNumberForProfile + "-" + l);
      profile.getWeblinks().add(weblink);
    }

    return profile;
  }

  public static List<Tag> createTags(int amount) {
    List<Tag> tags = new ArrayList<>();

    for (int i = 0; i < amount; i++) {
      tags.add(createTag(i));
    }

    return tags;
  }

  /**
   * @param uniqueNumber number used for generation of Tag name to satisfy uniqueness constraints
   */
  public static Tag createTag(int uniqueNumber) {
    Tag tag = new Tag();
    tag.setName("Tag" + uniqueNumber);
    return  tag;
  }
}
