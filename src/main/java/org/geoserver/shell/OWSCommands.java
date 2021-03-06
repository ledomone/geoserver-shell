package org.geoserver.shell;

import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OWSCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({
            "ows wcs list", "ows wcs modify", "ows wcs create", "ows wcs delete",
            "ows wms list", "ows wms modify", "ows wms create", "ows wms delete",
            "ows wfs list", "ows wfs modify", "ows wfs create", "ows wfs delete"
    })
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    @CliCommand(value = "ows wcs list", help = "List Global or Local OWS WCS Settings.")
    public String wcsList(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        // @TODO http://docs.geoserver.org/stable/en/user/rest/api/services.html is incorrect, it is missing /workspaces/<ws>
        String url = geoserver.getUrl() + "/rest/services/wcs/" + (workspace != null ? "workspaces/" + workspace + "/" : "") + "settings.xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        if (xml != null) {
            Element element = JDOMBuilder.buildElement(xml);
            String TAB = "   ";
            builder.append(element.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Enabled: ").append(element.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Cite Compliant: ").append(element.getChildText("citeCompliant")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Schema Base URL: ").append(element.getChildText("schemaBaseURL")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Verbose: ").append(element.getChildText("verbose")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("GML Prefixing: ").append(element.getChildText("gmlPrefixing")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("LatLon: ").append(element.getChildText("latLon")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Max Input Memory: ").append(element.getChildText("maxInputMemory")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Max Output Memory: ").append(element.getChildText("maxOutputMemory")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Subsampling Enabled: ").append(element.getChildText("subsamplingEnabled")).append(OsUtils.LINE_SEPARATOR);
            Element vElement = element.getChild("versions");
            if (vElement != null) {
                List<Element> versionElements = vElement.getChildren("org.geotools.util.Version");
                builder.append(TAB).append("Versions:").append(OsUtils.LINE_SEPARATOR);
                for (Element versionElement : versionElements) {
                    builder.append(TAB).append(TAB).append(versionElement.getChildText("version")).append(OsUtils.LINE_SEPARATOR);
                }
            }
        }
        return builder.toString();
    }

    @CliCommand(value = "ows wcs modify", help = "Modify Global or Local OWS WCS Settings.")
    public boolean wcsModify(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "enabled", mandatory = false, help = "The enabled flag") String enabled,
            @CliOption(key = "verbose", mandatory = false, help = "The verbose flag") String verbose,
            @CliOption(key = "gmlprefixing", mandatory = false, help = "The GML prefixing flag") String gmlPrefixing,
            @CliOption(key = "latlon", mandatory = false, help = "The latlon flag") String latLon,
            @CliOption(key = "maxinputmemory", mandatory = false, help = "The max input memory") String maxInputMemory,
            @CliOption(key = "maxoutputmemory", mandatory = false, help = "The max output memory") String maxOutputMemory,
            @CliOption(key = "subsamplingenabled", mandatory = false, help = "The subsamping enabled flag") String subsamplingEnabled
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wcs/" + (workspace != null ? "workspaces/" + URLUtil.encode(workspace) + "/" : "") + "settings.xml";
        Element element = new Element("wcs");
        if (enabled != null) {
            element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        }
        if (verbose != null) {
            element.addContent(new Element("verbose").setText(String.valueOf(verbose)));
        }
        if (gmlPrefixing != null) {
            element.addContent(new Element("gmlPrefixing").setText(String.valueOf(gmlPrefixing)));
        }
        if (maxInputMemory != null) {
            element.addContent(new Element("latLon").setText(String.valueOf(latLon)));
        }
        if (maxInputMemory != null) {
            element.addContent(new Element("maxInputMemory").setText(String.valueOf(maxInputMemory)));
        }
        if (maxOutputMemory != null) {
            element.addContent(new Element("maxOutputMemory").setText(String.valueOf(maxOutputMemory)));
        }
        if (subsamplingEnabled != null) {
            element.addContent(new Element("subsamplingEnabled").setText(String.valueOf(subsamplingEnabled)));
        }
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "ows wcs create", help = "Create workspace specific OWS WCS Settings.")
    public boolean wcsCreate(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "enabled", mandatory = false, unspecifiedDefaultValue = "true", help = "The enabled flag") boolean enabled,
            @CliOption(key = "verbose", mandatory = false, unspecifiedDefaultValue = "false", help = "The verbose flag") boolean verbose,
            @CliOption(key = "gmlprefixing", mandatory = false, unspecifiedDefaultValue = "false", help = "The GML prefixing flag") boolean gmlPrefixing,
            @CliOption(key = "latlon", mandatory = false, unspecifiedDefaultValue = "false", help = "The latlon flag") boolean latLon,
            @CliOption(key = "maxinputmemory", mandatory = false, unspecifiedDefaultValue = "0", help = "The max input memory") int maxInputMemory,
            @CliOption(key = "maxoutputmemory", mandatory = false, unspecifiedDefaultValue = "0", help = "The max output memory") int maxOutputMemory,
            @CliOption(key = "subsamplingenabled", mandatory = false, unspecifiedDefaultValue = "true", help = "The subsampling flag") boolean subsamplingEnabled
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wcs/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        Element element = new Element("wcs");
        element.addContent(new Element("workspace").addContent("name").setText(workspace));
        element.addContent(new Element("name").setText("WCS"));
        element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        element.addContent(new Element("verbose").setText(String.valueOf(verbose)));
        element.addContent(new Element("gmlPrefixing").setText(String.valueOf(gmlPrefixing)));
        element.addContent(new Element("latLon").setText(String.valueOf(latLon)));
        element.addContent(new Element("maxInputMemory").setText(String.valueOf(maxInputMemory)));
        element.addContent(new Element("maxOutputMemory").setText(String.valueOf(maxOutputMemory)));
        element.addContent(new Element("subsamplingEnabled").setText(String.valueOf(subsamplingEnabled)));
        String content = JDOMUtil.toString(element);
        // @TODO shouldn't this be POST?
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "ows wcs delete", help = "Delete workspace specific OWS WCS Settings.")
    public boolean wcsDelete(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wcs/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "ows wms list", help = "List OWS WMS Settings.")
    public String wmsList(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wms/" + (workspace != null ? "workspaces/" + URLUtil.encode(workspace) + "/" : "") + "settings.xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        if (xml != null) {
            Element element = JDOMBuilder.buildElement(xml);
            String TAB = "   ";
            builder.append(element.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Enabled: ").append(element.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Cite Compliant: ").append(element.getChildText("citeCompliant")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Schema Base URL: ").append(element.getChildText("schemaBaseURL")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Verbose: ").append(element.getChildText("verbose")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Interpolation: ").append(element.getChildText("interpolation")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Max Buffer: ").append(element.getChildText("maxBuffer")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Max Request Memory: ").append(element.getChildText("maxRequestMemory")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Max Rendering Time: ").append(element.getChildText("maxRenderingTime")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Max Rendering Errors: ").append(element.getChildText("maxRenderingErrors")).append(OsUtils.LINE_SEPARATOR);
            Element waterMarkElement = element.getChild("watermark");
            builder.append(TAB).append("Watermark:").append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append("Enabled: ").append(waterMarkElement.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append("Position: ").append(waterMarkElement.getChildText("position")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append("Transparency: ").append(waterMarkElement.getChildText("transparency")).append(OsUtils.LINE_SEPARATOR);
            Element vElement = element.getChild("versions");
            if (vElement != null) {
                List<Element> versionElements = vElement.getChildren("org.geotools.util.Version");
                builder.append(TAB).append("Versions:").append(OsUtils.LINE_SEPARATOR);
                for (Element versionElement : versionElements) {
                    builder.append(TAB).append(TAB).append(versionElement.getChildText("version")).append(OsUtils.LINE_SEPARATOR);
                }
            }
        }

        return builder.toString();
    }

    @CliCommand(value = "ows wms modify", help = "Modify global OWS WMS Settings.")
    public boolean wmsModify(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "enabled", mandatory = false, help = "") String enabled,
            @CliOption(key = "verbose", mandatory = false, help = "") String verbose,
            @CliOption(key = "citecompliant", mandatory = false, help = "") String citeCompliant,
            @CliOption(key = "maxbuffer", mandatory = false, help = "") String maxBuffer,
            @CliOption(key = "maxrequestmemory", mandatory = false, help = "") String maxRequestMemory,
            @CliOption(key = "maxrenderingtime", mandatory = false, help = "") String maxRenderingTime,
            @CliOption(key = "maxrenderingerrors", mandatory = false, help = "") String maxRenderingErrors,
            @CliOption(key = "interpolation", mandatory = false, help = "Nearest, Bilinear, Bicubic") String interpolation,
            @CliOption(key = "watermarkenabled", mandatory = false, help = "Whether to enable the watermark or not") String waterMarkEnabled,
            @CliOption(key = "watermarkposition", mandatory = false, help = "The water mark position: {TOP,MID,BOT}_{LEFT,CENTER,RIGHT}") String waterMarkPosition,
            @CliOption(key = "watermarktransparency", mandatory = false, help = "The water mark transparency (0-100)") String waterMarkTransparency
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wms/" + (workspace != null ? "workspaces/" + URLUtil.encode(workspace) + "/" : "") + "settings.xml";
        Element element = new Element("wms");
        if (enabled != null) {
            element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        }
        if (verbose != null) {
            element.addContent(new Element("verbose").setText(String.valueOf(verbose)));
        }
        if (citeCompliant != null) {
            element.addContent(new Element("citeCompliant").setText(String.valueOf(citeCompliant)));
        }
        Element waterMarkElement = new Element("watermark");
        if (waterMarkEnabled != null) {
            waterMarkElement.addContent(new Element("enabled").setText(String.valueOf(waterMarkEnabled)));
        }
        if (waterMarkPosition != null) {
            waterMarkElement.addContent(new Element("position").setText(waterMarkPosition));
        }
        if (waterMarkTransparency != null) {
            waterMarkElement.addContent(new Element("transparency").setText(String.valueOf(waterMarkTransparency)));
        }
        if (waterMarkElement.getChildren().size() > 0) {
            element.addContent(waterMarkElement);
        }
        if (interpolation != null) {
            element.addContent(new Element("interpolation").setText(interpolation));
        }
        if (maxBuffer != null) {
            element.addContent(new Element("maxBuffer").setText(String.valueOf(maxBuffer)));
        }
        if (maxRequestMemory != null) {
            element.addContent(new Element("maxRequestMemory").setText(String.valueOf(maxRequestMemory)));
        }
        if (maxRenderingTime != null) {
            element.addContent(new Element("maxRenderingTime").setText(String.valueOf(maxRenderingTime)));
        }
        if (maxRenderingErrors != null) {
            element.addContent(new Element("maxRenderingErrors").setText(String.valueOf(maxRenderingErrors)));
        }
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "ows wms create", help = "Create workspace specific OWS WMS Settings.")
    public boolean wmsCreate(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "enabled", mandatory = false, unspecifiedDefaultValue = "true", help = "") boolean enabled,
            @CliOption(key = "verbose", mandatory = false, unspecifiedDefaultValue = "false", help = "") boolean verbose,
            @CliOption(key = "citecompliant", mandatory = false, unspecifiedDefaultValue = "false", help = "") boolean citeCompliant,
            @CliOption(key = "maxbuffer", mandatory = false, unspecifiedDefaultValue = "0", help = "") long maxBuffer,
            @CliOption(key = "maxrequestmemory", mandatory = false, unspecifiedDefaultValue = "0", help = "") long maxRequestMemory,
            @CliOption(key = "maxrenderingtime", mandatory = false, unspecifiedDefaultValue = "0", help = "") long maxRenderingTime,
            @CliOption(key = "maxrenderingerrors", mandatory = false, unspecifiedDefaultValue = "0", help = "") long maxRenderingErrors,
            @CliOption(key = "interpolation", mandatory = false, unspecifiedDefaultValue = "Nearest", help = "Nearest, Bilinear, Bicubic") String interpolation,
            @CliOption(key = "watermarkenabled", mandatory = false, unspecifiedDefaultValue = "false", help = "Whether to enable the watermark or not") boolean waterMarkEnabled,
            @CliOption(key = "watermarkposition", mandatory = false, unspecifiedDefaultValue = "BOT_RIGHT", help = "The water mark position: {TOP,MID,BOT}_{LEFT,CENTER,RIGHT}") String waterMarkPosition,
            @CliOption(key = "watermarktransparency", mandatory = false, unspecifiedDefaultValue = "100", help = "The water mark transparency (0-100)") int waterMarkTransparency
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wms/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        Element element = new Element("wms");
        element.addContent(new Element("workspace").addContent("name").setText(workspace));
        element.addContent(new Element("name").setText("WMS"));
        element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        element.addContent(new Element("verbose").setText(String.valueOf(verbose)));
        element.addContent(new Element("citeCompliant").setText(String.valueOf(citeCompliant)));
        Element waterMarkElement = new Element("watermark");
        waterMarkElement.addContent(new Element("enabled").setText(String.valueOf(waterMarkEnabled)));
        waterMarkElement.addContent(new Element("position").setText(waterMarkPosition));
        waterMarkElement.addContent(new Element("transparency").setText(String.valueOf(waterMarkTransparency)));
        element.addContent(waterMarkElement);
        element.addContent(new Element("interpolation").setText(interpolation));
        element.addContent(new Element("maxBuffer").setText(String.valueOf(maxBuffer)));
        element.addContent(new Element("maxRequestMemory").setText(String.valueOf(maxRequestMemory)));
        element.addContent(new Element("maxRenderingTime").setText(String.valueOf(maxRenderingTime)));
        element.addContent(new Element("maxRenderingErrors").setText(String.valueOf(maxRenderingErrors)));
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "ows wms delete", help = "Delete workspace specific OWS WMS Settings.")
    public boolean wmsDelete(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wms/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }


    @CliCommand(value = "ows wfs list", help = "List OWS WFS Settings.")
    public String wfsList(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wfs/" + (workspace != null ? "workspaces/" + URLUtil.encode(workspace) + "/" : "") + "settings.xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        if (xml != null) {
            Element element = JDOMBuilder.buildElement(xml);
            String TAB = "   ";
            builder.append(element.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Enabled: ").append(element.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Cite Compliant: ").append(element.getChildText("citeCompliant")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Schema Base URL: ").append(element.getChildText("schemaBaseURL")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Verbose: ").append(element.getChildText("verbose")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("serviceLevel: ").append(element.getChildText("serviceLevel")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("maxFeatures: ").append(element.getChildText("maxFeatures")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("featureBounding: ").append(element.getChildText("featureBounding")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("canonicalSchemaLocation: ").append(element.getChildText("canonicalSchemaLocation")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("encodeFeatureMember: ").append(element.getChildText("encodeFeatureMember")).append(OsUtils.LINE_SEPARATOR);
            Element gmlElement = element.getChild("gml");
            builder.append(TAB).append("GML:").append(OsUtils.LINE_SEPARATOR);
            List<Element> entryElements = gmlElement.getChildren("entry");
            for (Element entryElement : entryElements) {
                builder.append(TAB).append(TAB).append("Version: ").append(entryElement.getChildText("version")).append(OsUtils.LINE_SEPARATOR);
                Element entryGmlElement = entryElement.getChild("gml");
                if (entryGmlElement != null) {
                    builder.append(TAB).append(TAB).append(TAB).append("SRS Name Style: ").append(entryGmlElement.getChildText("srsNameStyle")).append(OsUtils.LINE_SEPARATOR);
                    builder.append(TAB).append(TAB).append(TAB).append("Override GML Attributes: ").append(entryGmlElement.getChildText("overrideGMLAttributes")).append(OsUtils.LINE_SEPARATOR);
                }
            }
            Element vElement = element.getChild("versions");
            if (vElement != null) {
                List<Element> versionElements = vElement.getChildren("org.geotools.util.Version");
                builder.append(TAB).append("Versions:").append(OsUtils.LINE_SEPARATOR);
                for (Element versionElement : versionElements) {
                    builder.append(TAB).append(TAB).append(versionElement.getChildText("version")).append(OsUtils.LINE_SEPARATOR);
                }
            }
        }
        return builder.toString();
    }

    @CliCommand(value = "ows wfs modify", help = "Modify global OWS WFS Settings.")
    public boolean wfsModify(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "enabled", mandatory = false, help = "The enabled flag") String enabled,
            @CliOption(key = "verbose", mandatory = false, help = "The verbose flag") String verbose,
            @CliOption(key = "maxfeatures", mandatory = false, help = "The max features") String maxFeatures,
            @CliOption(key = "featurebounding", mandatory = false, help = "The feature bounding flag") String featureBounding,
            @CliOption(key = "canonicalschemalocation", mandatory = false, help = "The canonical schema location") String canonicalSchemaLocation,
            @CliOption(key = "encodefeaturemember", mandatory = false, help = "The encode feature member flag") String encodeFeatureMember,
            @CliOption(key = "citecompliant", mandatory = false, help = "The cite compliant flag") String citeCompliant,
            @CliOption(key = "servicelevel", mandatory = false, help = "The service level: COMPLETE, BASIC, TRANSACTIONAL") String serviceLevel
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wfs/" + (workspace != null ? "workspaces/" + URLUtil.encode(workspace) + "/" : "") + "settings.xml";
        Element element = new Element("wfs");
        if (enabled != null) {
            element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        }
        if (verbose != null) {
            element.addContent(new Element("verbose").setText(String.valueOf(verbose)));
        }
        if (citeCompliant != null) {
            element.addContent(new Element("citeCompliant").setText(String.valueOf(citeCompliant)));
        }
        if (serviceLevel != null) {
            element.addContent(new Element("serviceLevel").setText(serviceLevel));
        }
        if (maxFeatures != null) {
            element.addContent(new Element("maxFeatures").setText(String.valueOf(maxFeatures)));
        }
        if (featureBounding != null) {
            element.addContent(new Element("featureBounding").setText(String.valueOf(featureBounding)));
        }
        if (canonicalSchemaLocation != null) {
            element.addContent(new Element("canonicalSchemaLocation").setText(String.valueOf(canonicalSchemaLocation)));
        }
        if (encodeFeatureMember != null) {
            element.addContent(new Element("encodeFeatureMember").setText(String.valueOf(encodeFeatureMember)));
        }
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "ows wfs create", help = "Create workspace specific OWS WFS Settings.")
    public boolean wfsCreate(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "enabled", mandatory = false, unspecifiedDefaultValue = "true", help = "") boolean enabled,
            @CliOption(key = "verbose", mandatory = false, unspecifiedDefaultValue = "false", help = "") boolean verbose,
            @CliOption(key = "maxfeatures", mandatory = false, unspecifiedDefaultValue = "1000000", help = "") long maxFeatures,
            @CliOption(key = "featurebounding", mandatory = false, unspecifiedDefaultValue = "true", help = "") boolean featureBounding,
            @CliOption(key = "canonicalschemalocation", mandatory = false, unspecifiedDefaultValue = "false", help = "") boolean canonicalSchemaLocation,
            @CliOption(key = "encodefeaturemember", mandatory = false, unspecifiedDefaultValue = "false", help = "") boolean encodeFeatureMember,
            @CliOption(key = "citecompliant", mandatory = false, unspecifiedDefaultValue = "false", help = "") boolean citeCompliant,
            @CliOption(key = "servicelevel", mandatory = false, unspecifiedDefaultValue = "COMPLETE", help = "COMPLETE, BASIC, TRANSACTIONAL") String serviceLevel
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wfs/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        Element element = new Element("wfs");
        element.addContent(new Element("workspace").addContent("name").setText(workspace));
        element.addContent(new Element("name").setText("WFS"));
        element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        element.addContent(new Element("verbose").setText(String.valueOf(verbose)));
        element.addContent(new Element("citeCompliant").setText(String.valueOf(citeCompliant)));
        element.addContent(new Element("serviceLevel").setText(serviceLevel));
        element.addContent(new Element("maxFeatures").setText(String.valueOf(maxFeatures)));
        element.addContent(new Element("featureBounding").setText(String.valueOf(featureBounding)));
        element.addContent(new Element("canonicalSchemaLocation").setText(String.valueOf(canonicalSchemaLocation)));
        element.addContent(new Element("encodeFeatureMember").setText(String.valueOf(encodeFeatureMember)));
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "ows wfs delete", help = "Delete workspace specific OWS WFS Settings.")
    public boolean wfsDelete(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wfs/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }
}
