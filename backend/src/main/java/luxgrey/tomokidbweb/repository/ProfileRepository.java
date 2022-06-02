package luxgrey.tomokidbweb.repository;

import java.util.Optional;
import luxgrey.tomokidbweb.model.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends PagingAndSortingRepository<Profile, Long>, ProfileRepositoryCustom {

  Page<Profile> findAll(Pageable pageable);

  /**
   * @param id cannot be null or negative, as this will otherwise result in a
   *           MultipleBagFetchException
   */
  @EntityGraph(value = "Profile.details", type = EntityGraphType.LOAD)
  Optional<Profile> findById(Long id);
}
