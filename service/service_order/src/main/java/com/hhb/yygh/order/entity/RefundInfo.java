package com.hhb.yygh.order.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 退款信息表
 * </p>
 *
 * @author hhb
 * @since 2023-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RefundInfo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 编号
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 对外业务编号
     */
    private String outTradeNo;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 支付类型（微信 支付宝）
     */
    private Integer paymentType;

    /**
     * 交易编号
     */
    private String tradeNo;

    /**
     * 退款金额
     */
    private BigDecimal totalAmount;

    /**
     * 交易内容
     */
    private String subject;

    /**
     * 退款状态
     */
    private Integer refundStatus;

    /**
     * 回调信息
     */
    private String callbackContent;

    private LocalDateTime callbackTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除(1:已删除，0:未删除)
     */
    private Integer isDeleted;


}
