package ltd.newbee.mall.api.mall.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 新蜂商城用户信息展示的视图对象
 */
@Data
public class NewBeeMallUserVO implements Serializable {

    // 用户昵称
    @ApiModelProperty("用户昵称")
    private String nickName;

    // 用户登录名
    @ApiModelProperty("用户登录名")
    private String loginName;

    // 个性签名
    @ApiModelProperty("个性签名")
    private String introduceSign;
}
