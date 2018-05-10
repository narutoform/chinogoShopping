package cn.chinogo.pojo;

/**
 * TbItem 扩展pojo
 */
public class Item extends TbItem {

    public Item(TbItem tbItem) {
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
    }

}
