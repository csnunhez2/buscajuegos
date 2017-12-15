package es.udc.muei.riws;

import org.apache.avro.util.Utf8;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.gora.util.Null;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseFilter;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.storage.WebPage.Field;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.nutch.parse.ParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.gora.persistency.impl.PersistentBase;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

public class HTMLTagParser implements ParseFilter {

    private static final Log LOG = LogFactory.getLog(ParserFactory.class);


    private static final int CHUNK_SIZE = 2000;


    private static Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

    private Configuration conf;

    static {
        FIELDS.add(WebPage.Field.BASE_URL);
        LOG.debug("static log");
    }

    private String description = null;

    public void tagDescription(Node node){
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("Start parse");
//        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            if ("div".equalsIgnoreCase(node.getNodeName()) || "p".equalsIgnoreCase(node.getNodeName())) {
                NamedNodeMap attrs = node.getAttributes();
                Node itempropNode = attrs.getNamedItem("itemprop");
//                if (LOG.isDebugEnabled()) {
//                    LOG.debug("found container");
//                }
                if (itempropNode != null) {
                    if ("description".equalsIgnoreCase(itempropNode.getNodeValue())) {
                        this.description = node.getTextContent().toString();

//                        if (LOG.isDebugEnabled()) {
//                            LOG.debug("Found description: " + node.getTextContent());
//                        }
                    }
                }
            }

        }
        NodeList children = node.getChildNodes();
        for (int i = 0; children != null && i < children.getLength(); i++) {
            tagDescription(children.item(i));
            if (this.description != null) break;
        }
    }

    public void metaDescription(Node node){
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("Start parse");
//        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            if ("meta".equalsIgnoreCase(node.getNodeName())) {
                NamedNodeMap attrs = node.getAttributes();
                Node nameNode = attrs.getNamedItem("name");
//                if (LOG.isDebugEnabled()) {
//                    LOG.debug("found container");
//                }
                if (nameNode != null) {
                    if ("description".equalsIgnoreCase(nameNode.getNodeValue())) {
                        this.description = attrs.getNamedItem("content").getNodeValue().toString();

//                        if (LOG.isDebugEnabled()) {
//                            LOG.debug("Found description: " + node.getTextContent());
//                        }
                    }
                }
            }

        }
        NodeList children = node.getChildNodes();
        for (int i = 0; children != null && i < children.getLength(); i++) {
            metaDescription(children.item(i));
            if (this.description != null) break;
        }
    }

    public void parse(Node node) {
        tagDescription(node);
        if(this.description==null){
            metaDescription(node);
        }

    }


    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Configuration getConf() {
        return this.conf;
    }

    public String getDescription() {
        return this.description;
    }


    public Parse filter(String url, WebPage page, Parse parse,
                HTMLMetaTags metaTags, DocumentFragment doc){
//            LOG.debug("Hello world!");

        Configuration conf = NutchConfiguration.create();
        HTMLTagParser parser = new HTMLTagParser();
        parser.setConf(conf);

        parser.parse(doc);
        if(parser.getDescription()!=null){
            ByteBuffer bb = ByteBuffer.wrap(parser.getDescription().getBytes());
            page.getMetadata().put(new Utf8("description"), bb);
        }
        return parse;

    }


    public Collection<Field> getFields() {
        return null;
    }

}
