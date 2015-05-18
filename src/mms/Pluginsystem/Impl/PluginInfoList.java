package mms.Pluginsystem.Impl;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pluginNames")
public class PluginInfoList {
	private List<PluginInfo> pluginInfos;

    public List<PluginInfo> getPluginInfos() {
        return pluginInfos;
    }

    public void setPluginInfos(List<PluginInfo> infos) {
        this.pluginInfos = infos;
    }
}
