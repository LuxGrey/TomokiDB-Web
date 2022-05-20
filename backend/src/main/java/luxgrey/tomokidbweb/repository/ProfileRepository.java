package luxgrey.tomokidbweb.repository;

import luxgrey.tomokidbweb.model.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends PagingAndSortingRepository<Profile, Long>, ProfileRepositoryCustom {

  Page<Profile> findAll(Pageable pageable);
}
