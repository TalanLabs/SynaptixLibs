package com.synaptix.taskmanager.controller.helper;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.common.util.IResultCallback;

public interface ILocationSearchFieldWidgetContext {

	/**
	 * Search a location
	 * 
	 * @param name
	 * @param resultCallback
	 */
	public abstract void searchLocation(String country, String state, String zip, String city, final IResultCallback<Location> resultCallback, Integer locationFilter);

	public static class Location {

		private String country;

		private String state;

		private String zip;

		private String city;

		public Location(String country, String state, String zip, String city) {
			super();
			this.country = country;
			this.zip = zip;
			this.city = city;
			this.state = state;
		}

		public String getCountry() {
			return country;
		}

		public String getZip() {
			return zip;
		}

		public String getCity() {
			return city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

}
