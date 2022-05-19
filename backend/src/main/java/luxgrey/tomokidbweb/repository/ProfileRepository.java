package luxgrey.tomokidbweb.repository;

import java.util.Collection;
import luxgrey.tomokidbweb.model.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends PagingAndSortingRepository<Profile, Long> {

  Page<Profile> findAll(Pageable pageable);

  /**
   * Returns a Page with Profiles where
   * - at least one Alias of a Profile has a name that contains the argument for alias
   * - a Profile is related to all tags for which the IDs have been provided in tagIds
   *
   * @param pageable configuration for returned Page
   * @param alias alias to filter
   * @param tagIds Collection with tag IDs to filter
   * @param tagIdsSize size of tagIds
   */
  @Query(value = "SELECT p"
      + " FROM Profile p JOIN p.aliases a JOIN p.tags t"
      + " WHERE a.name LIKE %:alias%"
      + " AND t.id IN :tagIds"
      + " GROUP BY t.id"
      + " HAVING COUNT(t.id) = :tagIdsSize")
  Page<Profile> findByAliasAndTagIds(
      Pageable pageable,
      @Param("alias") String alias,
      @Param("tagIds") Collection<Long> tagIds,
      @Param("tagIdsSize") int tagIdsSize);
}
