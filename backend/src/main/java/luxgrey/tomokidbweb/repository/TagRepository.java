package luxgrey.tomokidbweb.repository;

import java.util.Collection;
import java.util.List;
import luxgrey.tomokidbweb.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

  List<Tag> findByIdIn(Collection<Long> ids);

  long countByNameIn(Collection<String> names);
}
