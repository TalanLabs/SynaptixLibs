package com.mongo.test.service.dao.access.project;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.jmkgreen.morphia.Key;
import com.github.jmkgreen.morphia.query.Criteria;
import com.github.jmkgreen.morphia.query.CriteriaContainerImpl;
import com.github.jmkgreen.morphia.query.Query;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.mongo.test.domain.impl.report.Campaign;
import com.mongo.test.domain.impl.report.Project;
import com.mongo.test.service.dao.common.AbstractMongoDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;
import com.mongo.test.service.init.DbStarter;

public class ProjectDaoService extends AbstractMongoDaoService<Project> {

	public interface Factory {
		ProjectDaoService create(@Assisted String dbName);
	}

	private final CampaignDaoService cDaoService;

	@Inject
	public ProjectDaoService(DbStarter starter, CommonMongoDaoService cService, @Assisted String dbName, @Named("default_db") String default_db, CampaignDaoService.Factory cDaoServiceFactory) {
		super(Project.class, starter.getDatabaseByName((dbName == null ? default_db : dbName)), cService);
		this.cDaoService = cDaoServiceFactory.create(dbName);
	}

	public Project getByName(String name) {
		Query<Project> query = createQuery();
		query.field("name").equal(name);
		return query.get();
	}

	public List<Project> getProjectHistory(Project project) {
		Query<Project> query = createQuery();
		Criteria nameCriteria = query.criteria("name").equal(project.getName());
		Criteria versionCriteria = query.criteria("version").equal(project.getVersion());
		Criteria iterationCriteria = query.criteria("iteration").lessThan(project.getIteration());
		query.and(nameCriteria, versionCriteria, iterationCriteria);
		List<Project> projectHistory = find(query).asList();
		Collections.sort(projectHistory, new Comparator<Project>() {

			@Override
			public int compare(Project o1, Project o2) {
				return o1.getIteration() - o2.getIteration();
			}

		});

		return projectHistory;
	}

	public Key<Project> saveNewIteration(Project p) {
		p.setIteration((short) (p.getIteration() + 1));
		p.setId(null);
		for (Campaign c : p.getCampaigns()) {
			cDaoService.saveAsNewIteration(c);
		}
		return save(p);
	}

	public List<Project> findAllIterationsByProjectName(String pName, String version) {
		Query<Project> query = createQuery();
		CriteriaContainerImpl equal2 = query.criteria("version").equal(version);
		query.criteria("name").equal(pName).and(equal2);
		return find(query).asList();
	}

	public Project getLastByName(String name) {
		Query<Project> query = createQuery();
		query.field("name").equal(name).order("-iteration");
		return query.get();
	}

	public Project getByNameAndIteration(String pName, String iter) {
		Query<Project> query = createQuery();
		Criteria nameCriteria = query.criteria("name").equal(pName);
		Criteria iterationCriteria = query.criteria("iteration").equal(Short.valueOf(iter));
		query.and(nameCriteria, iterationCriteria);
		return find(query).get();
	}

}
