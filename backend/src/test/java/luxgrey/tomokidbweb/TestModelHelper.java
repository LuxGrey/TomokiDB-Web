package luxgrey.tomokidbweb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import luxgrey.tomokidbweb.dto.ProfileDTOPostOrPut;
import luxgrey.tomokidbweb.dto.ProfileDTOShort;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.model.Weblink;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;

/**
 * Provides functions for generating model instances with example data
 */
public class TestModelHelper {

  /**
   * @param uniqueSuffix String used for generation of Alias name to satisfy uniqueness constraints
   */
  public static Alias createAlias(String uniqueSuffix) {
    Alias alias = new Alias();
    alias.setName("Alias" + uniqueSuffix);
    return alias;
  }

  /**
   * @param profileIdentifier number used for generation of Alias names, included in all
   *                          created Alias names and used for a unique suffix,
   *                          helps to point out to which Profile the Alias belongs
   */
  public static List<Alias> createAliases(int amount, int profileIdentifier) {
    List<Alias> aliases = new ArrayList<>();

    for (int i = 0; i < amount; i++) {
      aliases.add(createAlias(profileIdentifier + "_" + i));
    }

    return aliases;
  }

  /**
   * @param uniqueSuffix String used for generation of Weblink url to satisfy uniqueness constraints
   */
  public static Weblink createWeblink(String uniqueSuffix) {
    Weblink weblink = new Weblink();
    weblink.setUrl("https://www.example.org/" + uniqueSuffix);
    return weblink;
  }

  /**
   * @param profileIdentifier number used for generation of Weblink urls, included in all
   *                          created Weblink urls and used for a unique suffix,
   *                          helps to point out to which Profile the Weblink belongs
   */
  public static List<Weblink> createWeblinks(int amount, int profileIdentifier) {
    List<Weblink> weblinks = new ArrayList<>();

    for (int i = 0; i < amount; i++) {
      weblinks.add(createWeblink(profileIdentifier + "-" + i));
    }

    return weblinks;
  }

  /**
   * @param uniqueSuffix number used for generation of Tag name to satisfy uniqueness constraints
   */
  public static Tag createTag(int uniqueSuffix) {
    Tag tag = new Tag();
    tag.setName("Tag" + uniqueSuffix);
    return  tag;
  }

  public static List<Tag> createTags(int amount) {
    List<Tag> tags = new ArrayList<>();

    for (int i = 0; i < amount; i++) {
      tags.add(createTag(i));
    }

    return tags;
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

    profile.setAliases(createAliases(amountAliases, uniqueNumberForProfile));
    profile.setWeblinks(createWeblinks(amountWeblinks, uniqueNumberForProfile));

    return profile;
  }

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
      if (assignIds) {
        profileDTOShort.setId((long) i + 1);
      }

      profileDTOShort.setAliases(createAliases(aliasesPerProfile, i));

      profileDTOShorts.add(profileDTOShort);
    }

    return profileDTOShorts;
  }

  /**
   * Creates an instance of ProfileDTOPostOrPut that is valid as far as creation or updating
   * of a Profile is concerned.
   * Returned instance won't have an ID assigned.
   *
   * @param uniqueNumber number used for generation of attribute values to satisfy uniqueness
   *                     constraints
   */
  public static ProfileDTOPostOrPut createValidProfileDTOPostOrPut(int uniqueNumber) {
    ProfileDTOPostOrPut validProfileDTO = new ProfileDTOPostOrPut();

    validProfileDTO.setNote("This is a note");
    validProfileDTO.setAliases(new HashSet<>(createAliases(2, 1)));
    validProfileDTO.setWeblinks(new HashSet<>(createWeblinks(2, 1)));

    return validProfileDTO;
  }
}
