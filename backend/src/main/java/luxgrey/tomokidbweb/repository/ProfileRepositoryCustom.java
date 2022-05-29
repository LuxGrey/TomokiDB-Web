package luxgrey.tomokidbweb.repository;

import java.util.Collection;
import luxgrey.tomokidbweb.model.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines functions for ProfileRepository that have to be implemented manually
 */
public interface ProfileRepositoryCustom {

  /**
   * Returns a Page with Profiles where
   * - at least one Alias of a Profile has a name that contains the argument for alias
   * - a Profile is related to all tags for which the IDs have been provided in tagIds
   */
  Page<Profile> findByAliasAndTagIds(
      Pageable pageable,
      String aliasName,
      Collection<Long> tagIds);
}
