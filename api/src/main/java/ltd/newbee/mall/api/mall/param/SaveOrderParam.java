
package ltd.newbee.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class SaveOrderParam implements Serializable {

    @ApiModelProperty("订单项id数组")
    private Long[] cartItemIds;

    @ApiModelProperty("地址id")
    private Long addressId;
}
