package com.gridgain.sales.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Payment definition.
 * 
 * This file was generated by Ignite Web Console (04/23/2020, 16:26)
 **/
public class Payment implements Serializable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Value for paymentdate. */
    private Date paymentdate;

    /** Value for amount. */
    private BigDecimal amount;

    /** Empty constructor. **/
    public Payment() {
        // No-op.
    }

    /** Full constructor. **/
    public Payment(Date paymentdate,
        BigDecimal amount) {
        this.paymentdate = paymentdate;
        this.amount = amount;
    }

    /**
     * Gets paymentdate
     * 
     * @return Value for paymentdate.
     **/
    public Date getPaymentdate() {
        return paymentdate;
    }

    /**
     * Sets paymentdate
     * 
     * @param paymentdate New value for paymentdate.
     **/
    public void setPaymentdate(Date paymentdate) {
        this.paymentdate = paymentdate;
    }

    /**
     * Gets amount
     * 
     * @return Value for amount.
     **/
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets amount
     * 
     * @param amount New value for amount.
     **/
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /** {@inheritDoc} **/
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        
        if (!(o instanceof Payment))
            return false;
        
        Payment that = (Payment)o;

        if (paymentdate != null ? !paymentdate.equals(that.paymentdate) : that.paymentdate != null)
            return false;
        

        if (amount != null ? !amount.equals(that.amount) : that.amount != null)
            return false;
        
        return true;
    }

    /** {@inheritDoc} **/
    @Override public int hashCode() {
        int res = paymentdate != null ? paymentdate.hashCode() : 0;

        res = 31 * res + (amount != null ? amount.hashCode() : 0);

        return res;
    }

    /** {@inheritDoc} **/
    @Override public String toString() {
        return "Payment [" + 
            "paymentdate=" + paymentdate + ", " + 
            "amount=" + amount +
        "]";
    }
}