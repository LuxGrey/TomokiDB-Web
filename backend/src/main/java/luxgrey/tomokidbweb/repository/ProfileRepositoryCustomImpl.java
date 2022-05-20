package luxgrey.tomokidbweb.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Alias_;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Profile_;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.model.Tag_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Provides manual implementations of complex functions for ProfileRepository
 */
@Repository
public class ProfileRepositoryCustomImpl implements ProfileRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;


  @Override
  public Page<Profile> findByAliasAndTagIds(
      Pageable pageable,
      String alias,
      Collection<Long> tagIds) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

    // get count of all Profiles
    Long profilesCount = getProfilesCount(criteriaBuilder);

    // get filtered Profiles
    CriteriaQuery<Profile> criteriaQuery = criteriaBuilder.createQuery(Profile.class);
    Metamodel metamodel = entityManager.getMetamodel();

    // build base query
    Root<Profile> profile = criteriaQuery.from(Profile.class);
    criteriaQuery.select(profile);

    // dynamically add query restrictions

    List<Predicate> predicates = new ArrayList<>();
    if (alias != null) {
      Join<Profile, Alias> aliasesJoin = profile.join(Profile_.aliases);
      predicates.add(
          criteriaBuilder.like(
            // make comparison case-insensitive by converting both sides to lower-case
            criteriaBuilder.lower(
                aliasesJoin.get(Alias_.name)
            ),
            "%" + alias.toLowerCase() + "%"
      ));
    }
    if (tagIds != null && !tagIds.isEmpty()) {
      Join<Profile, Tag> tagsJoin = profile.join(Profile_.tags);
      predicates.add(
          tagsJoin.get(Tag_.id).in(tagIds)
      );

      criteriaQuery
          .groupBy(
              profile.get(Profile_.id)
          ).having(
              criteriaBuilder.equal(
                  criteriaBuilder.count(profile.get(Profile_.id)),
                  tagIds.size()
              )
          );
    }
    criteriaQuery.where(predicates.toArray(new Predicate[]{}));

    TypedQuery<Profile> typedQuery = entityManager.createQuery(criteriaQuery);
    // configure page
    typedQuery.setFirstResult((pageable.getPageSize() * pageable.getPageNumber()));
    typedQuery.setMaxResults(pageable.getPageSize());

    List<Profile> profiles = typedQuery.getResultList();
    return new PageImpl<>(profiles, pageable, profilesCount);
  }

  /**
   * Queries the amount of all persisted Profiles
   */
  private Long getProfilesCount(CriteriaBuilder criteriaBuilder) {
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    countQuery.select(criteriaBuilder.count(countQuery.from(Profile.class)));
    return entityManager.createQuery(countQuery).getSingleResult();
  }
}
