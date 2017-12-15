package es.udc.muei.riws;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;

import org.apache.avro.util.Utf8;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.util.Bytes;
import org.apache.nutch.storage.WebPage.Field;

/**
 * Adds basic searchable fields to a document. The fields are: host - add host
 * as un-stored, indexed and tokenized url - url is both stored and indexed, so
 * it's both searchable and returned. This is also a required field. content -
 * content is indexed, so that it's searchable, but not stored in index title -
 * title is stored and indexed cache - add cached content/summary display
 * policy, if available tstamp - add timestamp when fetched, for deduplication
 */
public class HTMLTagIndexer implements IndexingFilter {

    private Configuration conf;

    private static final Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

    static {
        FIELDS.add(WebPage.Field.BASE_URL);
        FIELDS.add(WebPage.Field.METADATA);
    }

    @Override
    public Collection<Field> getFields() {
        return FIELDS;
    }

    /**
     * Set the {@link Configuration} object
     */
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    /**
     * Get the {@link Configuration} object
     */
    public Configuration getConf() {
        return this.conf;
    }

    /**
     * The {@link HTMLTagIndexer} filter object.
     *
     * @param doc
     *          The {@link NutchDocument} object
     * @param url
     *          URL to be filtered for rel-tag's
     * @param page
     *          {@link WebPage} object relative to the URL
     * @return filtered NutchDocument
     */
    @Override
    public NutchDocument filter(NutchDocument doc, String url, WebPage page)
            throws IndexingException {
        // Check if some Rel-Tags found, possibly put there by RelTagParser
        ByteBuffer bb = page.getMetadata().get(new Utf8("description"));

        doc.add("description",Bytes.toString(bb));
        return doc;
    }
}
