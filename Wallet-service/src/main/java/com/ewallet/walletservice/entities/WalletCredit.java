package com.ewallet.walletservice.entities;

import com.ewallet.walletservice.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class WalletCredit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_credit_id", nullable = false)
    private UUID walletCreditId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "razorpay_payment_link_id", unique = true)
    private String razorpayPaymentLinkId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private OffsetDateTime lastModifiedDate;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        WalletCredit that = (WalletCredit) o;
        return getWalletCreditId() != null && Objects.equals(getWalletCreditId(), that.getWalletCreditId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
