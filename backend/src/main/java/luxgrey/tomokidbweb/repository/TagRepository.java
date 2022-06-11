package luxgrey.tomokidbweb.repository;

import java.util.Collection;
import luxgrey.tomokidbweb.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

  long countByIdIn(Collection<Long> ids);

  long countByNameIn(Collection<String> names);
}
