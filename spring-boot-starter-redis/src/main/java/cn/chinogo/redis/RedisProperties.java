package cn.chinogo.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis配置
 */
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private String singleHost;
    private Integer singlePort;
    private String clusterNodes;

    // false Single true Cluster
    private Boolean cluster;

    public String getSingleHost() {
        return singleHost;
    }

    public void setSingleHost(String singleHost) {
        this.singleHost = singleHost;
    }

    public Integer getSinglePort() {
        return singlePort;
    }

    public void setSinglePort(Integer singlePort) {
        this.singlePort = singlePort;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public Boolean getCluster() {
        return cluster;
    }

    public void setCluster(Boolean cluster) {
        this.cluster = cluster;
    }
}
