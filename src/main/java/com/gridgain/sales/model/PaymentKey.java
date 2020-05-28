package com.gridgain.sales.model;

import java.io.Serializable;

/**
 * PaymentKey definition.
 * 
 * This file was generated by Ignite Web Console (04/23/2020, 16:26)
 **/
public class PaymentKey implements Serializable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Value for customernumber. */
    private int customernumber;

    /** Value for checknumber. */
    private String checknumber;

    /** Empty constructor. **/
    public PaymentKey() {
        // No-op.
    }

    /** Full constructor. **/
    public PaymentKey(int customernumber,
        String checknumber) {
        this.customernumber = customernumber;
        this.checknumber = checknumber;
    }

    /**
     * Gets customernumber
     * 
     * @return Value for customernumber.
     **/
    public int getCustomernumber() {
        return customernumber;
    }

    /**
     * Sets customernumber
     * 
     * @param customernumber New value for customernumber.
     **/
    public void setCustomernumber(int customernumber) {
        this.customernumber = customernumber;
    }

    /**
     * Gets checknumber
     * 
     * @return Value for checknumber.
     **/
    public String getChecknumber() {
        return checknumber;
    }

    /**
     * Sets checknumber
     * 
     * @param checknumber New value for checknumber.
     **/
    public void setChecknumber(String checknumber) {
        this.checknumber = checknumber;
    }

    /** {@inheritDoc} **/
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        
        if (!(o instanceof PaymentKey))
            return false;
        
        PaymentKey that = (PaymentKey)o;

        if (customernumber != that.customernumber)
            return false;
        

        if (checknumber != null ? !checknumber.equals(that.checknumber) : that.checknumber != null)
            return false;
        
        return true;
    }

    /** {@inheritDoc} **/
    @Override public int hashCode() {
        int res = customernumber;

        res = 31 * res + (checknumber != null ? checknumber.hashCode() : 0);

        return res;
    }

    /** {@inheritDoc} **/
    @Override public String toString() {
        return "PaymentKey [" + 
            "customernumber=" + customernumber + ", " + 
            "checknumber=" + checknumber +
        "]";
    }
}