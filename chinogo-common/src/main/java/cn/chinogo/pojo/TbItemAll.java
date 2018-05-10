package cn.chinogo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TbItem 扩展pojo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbItemAll extends TbItem {

    private String itemDesc;

    private String paramData;

    public TbItemAll(TbItem tbItem, TbItemDesc tbItemDesc, TbItemParamItem tbItemParamItem) {
        //初始化属性
        this.setId(tbItem.getId());
        this.setTitle(tbItem.getTitle());
        this.setSellPoint(tbItem.getSellPoint());
        this.setImage(tbItem.getImage());
        this.setPrice(tbItem.getPrice());
        this.setNum(tbItem.getNum());
        this.setBarcode(tbItem.getBarcode());
        this.setImage(tbItem.getImage());
        this.setCid(tbItem.getCid());
        this.setStatus(tbItem.getStatus());
        this.setCreated(tbItem.getCreated());
        this.setUpdated(tbItem.getUpdated());
        this.setSize(tbItem.getSize());
        this.setWeight(tbItem.getWeight());
        this.setColour(tbItem.getColour());

        this.setItemDesc(tbItemDesc.getItemDesc());

        this.setParamData(tbItemParamItem.getParamData());
    }

    public TbItem getTbItem() {
        TbItem tbItem = new TbItem();
        tbItem.setId(getId());
        tbItem.setTitle(getTitle());
        tbItem.setSellPoint(getSellPoint());
        tbItem.setImage(getImage());
        tbItem.setPrice(getPrice());
        tbItem.setNum(getNum());
        tbItem.setBarcode(getBarcode());
        tbItem.setImage(getImage());
        tbItem.setCid(getCid());
        tbItem.setStatus(getStatus());
        tbItem.setCreated(getCreated());
        tbItem.setUpdated(getUpdated());
        tbItem.setSize(getSize());
        tbItem.setWeight(getWeight());
        tbItem.setColour(getColour());
        
        return tbItem;
    }

    public TbItemDesc getTbItemDesc() {
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemDesc(getItemDesc());

        return tbItemDesc;
    }

    public TbItemParamItem getTbItemParamItem() {
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setParamData(getParamData());

        return tbItemParamItem;
    }
}
