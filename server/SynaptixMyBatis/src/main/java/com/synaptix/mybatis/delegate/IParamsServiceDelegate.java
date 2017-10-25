package com.synaptix.mybatis.delegate;

import com.synaptix.entity.IId;

public interface IParamsServiceDelegate {
    /**
     * Get param, if not exists value is defaultValue
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getParam(String key, String defaultValue) ;

    /**
     * Get int param, if not exists value is defaultValue
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Integer getIntParam(String key, Integer defaultValue);

    /**
     * Get boolean param, if not exists value is defaultValue
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Boolean getBooleanParam(String key, Boolean defaultValue);

    /**
     * Get double param, if not exists value is defaultValue
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Double getDoubleParam(String key, Double defaultValue);

    /**
     * Get float param, if not exists value is defaultValue
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Float getFloatParam(String key, Float defaultValue);
    /**
     * Get id param, if not exists value is defaultValue
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public IId getIdParam(String key, IId defaultValue);
}
