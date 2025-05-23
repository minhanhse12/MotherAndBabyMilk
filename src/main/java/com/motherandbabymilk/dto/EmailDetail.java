package com.motherandbabymilk.dto;


import com.motherandbabymilk.entity.Users;
import java.util.Date;
import lombok.Generated;

public class EmailDetail {
    private Users receiver;
    private String subject;
    private String link;
    private String reason;
    private Date createOrder;

    @Generated
    public EmailDetail() {
    }

    @Generated
    public Users getReceiver() {
        return this.receiver;
    }

    @Generated
    public String getSubject() {
        return this.subject;
    }

    @Generated
    public String getLink() {
        return this.link;
    }

    @Generated
    public String getReason() {
        return this.reason;
    }

    @Generated
    public Date getCreateOrder() {
        return this.createOrder;
    }

    @Generated
    public void setReceiver(final Users receiver) {
        this.receiver = receiver;
    }

    @Generated
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    @Generated
    public void setLink(final String link) {
        this.link = link;
    }

    @Generated
    public void setReason(final String reason) {
        this.reason = reason;
    }

    @Generated
    public void setCreateOrder(final Date createOrder) {
        this.createOrder = createOrder;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EmailDetail)) {
            return false;
        } else {
            EmailDetail other = (EmailDetail)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$receiver = this.getReceiver();
                    Object other$receiver = other.getReceiver();
                    if (this$receiver == null) {
                        if (other$receiver == null) {
                            break label71;
                        }
                    } else if (this$receiver.equals(other$receiver)) {
                        break label71;
                    }

                    return false;
                }

                Object this$subject = this.getSubject();
                Object other$subject = other.getSubject();
                if (this$subject == null) {
                    if (other$subject != null) {
                        return false;
                    }
                } else if (!this$subject.equals(other$subject)) {
                    return false;
                }

                label57: {
                    Object this$link = this.getLink();
                    Object other$link = other.getLink();
                    if (this$link == null) {
                        if (other$link == null) {
                            break label57;
                        }
                    } else if (this$link.equals(other$link)) {
                        break label57;
                    }

                    return false;
                }

                Object this$reason = this.getReason();
                Object other$reason = other.getReason();
                if (this$reason == null) {
                    if (other$reason != null) {
                        return false;
                    }
                } else if (!this$reason.equals(other$reason)) {
                    return false;
                }

                Object this$createOrder = this.getCreateOrder();
                Object other$createOrder = other.getCreateOrder();
                if (this$createOrder == null) {
                    if (other$createOrder == null) {
                        return true;
                    }
                } else if (this$createOrder.equals(other$createOrder)) {
                    return true;
                }

                return false;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof EmailDetail;
    }

    @Generated
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $receiver = this.getReceiver();
        result = result * 59 + ($receiver == null ? 43 : $receiver.hashCode());
        Object $subject = this.getSubject();
        result = result * 59 + ($subject == null ? 43 : $subject.hashCode());
        Object $link = this.getLink();
        result = result * 59 + ($link == null ? 43 : $link.hashCode());
        Object $reason = this.getReason();
        result = result * 59 + ($reason == null ? 43 : $reason.hashCode());
        Object $createOrder = this.getCreateOrder();
        result = result * 59 + ($createOrder == null ? 43 : $createOrder.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        Users var10000 = this.getReceiver();
        return "EmailDetail(receiver=" + var10000 + ", subject=" + this.getSubject() + ", link=" + this.getLink() + ", reason=" + this.getReason() + ", createOrder=" + this.getCreateOrder() + ")";
    }
}