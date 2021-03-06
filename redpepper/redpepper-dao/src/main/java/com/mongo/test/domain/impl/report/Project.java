package com.mongo.test.domain.impl.report;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Reference;
import com.mongo.test.domain.impl.common.BasicTaggableMongoBean;
import com.mongo.test.domain.impl.test.TestPage;

@Entity(value = "report.projects")
@Indexes({ @Index(value = "name"), @Index("version") })
public class Project extends BasicTaggableMongoBean {

	@Id
	private ObjectId id;
	private short iteration;
	@Reference
	private List<Campaign> campaigns;

	public String version;

	private Date startDate;

	private Date demoDate;

	private Date prodDate;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getDemoDate() {
		return demoDate;
	}

	public void setDemoDate(Date demoDate) {
		this.demoDate = demoDate;
	}

	public Date getProdDate() {
		return prodDate;
	}

	public void setProdDate(Date prodDate) {
		this.prodDate = prodDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public short getIteration() {
		return iteration;
	}

	public void setIteration(short iteration) {
		this.iteration = iteration;
	}

	public int getTotalOk() {
		int total = 0;
		for (Campaign campaign : getCampaigns()) {
			for (TestPage testPage : campaign.getTestCases()) {
				if (testPage.isSuccess()) {
					total++;
				}
			}
		}
		return total;
	}

	public int getTotalKo() {
		int total = 0;
		for (Campaign campaign : getCampaigns()) {
			for (TestPage testPage : campaign.getTestCases()) {
				if (!testPage.isSuccess()) {
					total++;
				}
			}
		}
		return total;
	}

	@Override
	@PrePersist
	public void prePersist() {
		// iteration++;
	}

}
