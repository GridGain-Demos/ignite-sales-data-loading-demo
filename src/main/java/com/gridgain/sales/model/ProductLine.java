package com.gridgain.sales.model;

import java.io.Serializable;

/**
 * ProductLine definition.
 * 
 * This file was generated by Ignite Web Console (04/23/2020, 16:26)
 **/
public class ProductLine implements Serializable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Value for textdescription. */
    private String textdescription;

    /** Value for htmldescription. */
    private String htmldescription;

    /** Value for image. */
    private byte[] image;

    /** Empty constructor. **/
    public ProductLine() {
        // No-op.
    }

    /** Full constructor. **/
    public ProductLine(String textdescription,
        String htmldescription,
        byte[] image) {
        this.textdescription = textdescription;
        this.htmldescription = htmldescription;
        this.image = image;
    }

    /**
     * Gets textdescription
     * 
     * @return Value for textdescription.
     **/
    public String getTextdescription() {
        return textdescription;
    }

    /**
     * Sets textdescription
     * 
     * @param textdescription New value for textdescription.
     **/
    public void setTextdescription(String textdescription) {
        this.textdescription = textdescription;
    }

    /**
     * Gets htmldescription
     * 
     * @return Value for htmldescription.
     **/
    public String getHtmldescription() {
        return htmldescription;
    }

    /**
     * Sets htmldescription
     * 
     * @param htmldescription New value for htmldescription.
     **/
    public void setHtmldescription(String htmldescription) {
        this.htmldescription = htmldescription;
    }

    /**
     * Gets image
     * 
     * @return Value for image.
     **/
    public byte[] getImage() {
        return image;
    }

    /**
     * Sets image
     * 
     * @param image New value for image.
     **/
    public void setImage(byte[] image) {
        this.image = image;
    }

    /** {@inheritDoc} **/
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        
        if (!(o instanceof ProductLine))
            return false;
        
        ProductLine that = (ProductLine)o;

        if (textdescription != null ? !textdescription.equals(that.textdescription) : that.textdescription != null)
            return false;
        

        if (htmldescription != null ? !htmldescription.equals(that.htmldescription) : that.htmldescription != null)
            return false;
        

        if (image != null ? !image.equals(that.image) : that.image != null)
            return false;
        
        return true;
    }

    /** {@inheritDoc} **/
    @Override public int hashCode() {
        int res = textdescription != null ? textdescription.hashCode() : 0;

        res = 31 * res + (htmldescription != null ? htmldescription.hashCode() : 0);

        res = 31 * res + (image != null ? image.hashCode() : 0);

        return res;
    }

    /** {@inheritDoc} **/
    @Override public String toString() {
        return "ProductLine [" + 
            "textdescription=" + textdescription + ", " + 
            "htmldescription=" + htmldescription + ", " + 
            "image=" + image +
        "]";
    }
}